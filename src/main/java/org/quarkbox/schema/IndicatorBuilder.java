package org.quarkbox.schema;

import graphql.language.Directive;
import graphql.language.FieldDefinition;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLScalarType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.quarkbox.schema.TypeBuilder.getScalarType;


public class IndicatorBuilder {


    protected static List<GraphQLFieldDefinition> build(List<FieldDefinition> fieldDefinitions, Set<GraphQLDirective> queryDirectives, List<GraphQLArgument> arguments) {
        List<GraphQLFieldDefinition> indicatorDefinitions = new ArrayList<>();

        for (FieldDefinition fieldDefinition : fieldDefinitions) {
            String indicatorName = fieldDefinition.getName();
            String indicatorDescription = fieldDefinition.getDescription().getContent();
            GraphQLScalarType indicatorType = getScalarType(fieldDefinition.getType());
            List<Directive> directives = fieldDefinition.getDirectives();
            List<GraphQLDirective> indicatorDirectives = DirectiveBuilder.build(directives, queryDirectives);

            GraphQLFieldDefinition indicatorDefinition = GraphQLFieldDefinition.newFieldDefinition()
                                                                               .name(indicatorName)
                                                                               .type(indicatorType)
                                                                               .replaceDirectives(indicatorDirectives)
                                                                               .arguments(arguments)
                                                                               .description(indicatorDescription)
                                                                               .build();

            indicatorDefinitions.add(indicatorDefinition);
        }

        return indicatorDefinitions;

    }

}
