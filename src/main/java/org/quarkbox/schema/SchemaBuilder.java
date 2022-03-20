package org.quarkbox.schema;

import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.TypeDefinitionRegistry;
import org.quarkbox.configuration.NamespaceMapping;
import org.quarkbox.provider.HttpProvider;

import java.util.List;
import java.util.Set;

public class SchemaBuilder {

    public static GraphQLSchema.Builder build(String schemaSource, NamespaceMapping namespaceMapping, GraphQLSchema.Builder builder) {
        TypeDefinitionRegistry schemaDefinition = SchemaLoader.load(schemaSource);
        Set<GraphQLDirective> directives = DirectiveBuilder.build(schemaDefinition);
        List<GraphQLFieldDefinition> indicators = FieldBuilder.build(schemaDefinition, directives);

        GraphQLObjectType queryType = GraphQLObjectType.newObject()
                                                       .name("Query")
                                                       .fields(indicators)
                                                       .build();
        GraphQLCodeRegistry codeRegistry = DataFetcherBuilder.build(namespaceMapping, indicators);

        return builder.query(queryType)
                      .clearDirectives()
                      .codeRegistry(codeRegistry)
                      .additionalDirectives(directives);
    }

}
