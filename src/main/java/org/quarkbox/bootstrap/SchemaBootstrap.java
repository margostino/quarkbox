package org.quarkbox.bootstrap;

import graphql.schema.GraphQLSchema;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.quarkbox.configuration.NamespaceMapping;
import org.quarkbox.schema.SchemaBuilder;

import javax.enterprise.event.Observes;
import javax.inject.Inject;

public class SchemaBootstrap {

    @ConfigProperty(name = "quarkbox.graphql.schema")
    String schemaSource;

    @Inject
    NamespaceMapping namespaceMapping;

    public GraphQLSchema.Builder build(@Observes GraphQLSchema.Builder builder) {
        return SchemaBuilder.build(schemaSource, namespaceMapping, builder);
    }

}
