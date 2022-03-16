package org.quarkbox.schema;

import graphql.language.FieldDefinition;
import graphql.language.TypeDefinition;
import graphql.language.TypeName;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class FieldBuilder {

    protected static List<GraphQLFieldDefinition> build(TypeDefinitionRegistry schemaDefinition, Set<GraphQLDirective> queryDirectives) {
        Optional<TypeDefinition> typeDefinition = schemaDefinition.getType("Query");
        List<FieldDefinition> fieldDefinitions = typeDefinition.get().getChildren();
        List<GraphQLFieldDefinition> queryIndicatorDefinitions = new ArrayList<>();

        for (FieldDefinition field : fieldDefinitions) {
            String namespaceType = ((TypeName) field.getType()).getName();
            String namespaceDescription = field.getDescription().getContent();
            List<FieldDefinition> namespaceFieldDefinitions = schemaDefinition.getType(namespaceType).get().getChildren();

            List<GraphQLArgument> arguments = ArgumentBuilder.build(field);
            List<GraphQLFieldDefinition> indicatorDefinitions = IndicatorBuilder.build(namespaceFieldDefinitions, queryDirectives, arguments);

            GraphQLObjectType indicatorType = GraphQLObjectType.newObject()
                                                               .name(namespaceType)
                                                               .description(namespaceDescription)
                                                               .fields(indicatorDefinitions)
                                                               .build();

            GraphQLFieldDefinition queryIndicatorDefinition = GraphQLFieldDefinition.newFieldDefinition()
                                                                                    .name(field.getName())
                                                                                    .description(namespaceDescription)
                                                                                    .arguments(arguments)
                                                                                    .type(indicatorType)
                                                                                    .build();
            queryIndicatorDefinitions.add(queryIndicatorDefinition);
        }

        return queryIndicatorDefinitions;
    }

}
