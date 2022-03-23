package org.quarkbox.bootstrap;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.quarkbox.configuration.DataProviderConfig;
import org.quarkbox.configuration.DataProviderMapping;
import org.quarkbox.configuration.NamespaceMapping;
import org.quarkbox.provider.HttpProvider;
import org.quarkbox.schema.DataFetcherBuilder;
import org.quarkbox.schema.NamespaceFetcher;
import org.quarkbox.schema.SchemaBuilder;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class SchemaBootstrap {

    @ConfigProperty(name = "quarkbox.graphql.schema")
    String schemaSource;

    @Inject
    NamespaceMapping namespaceMapping;

    public GraphQLSchema.Builder build(@Observes GraphQLSchema.Builder builder) {
        return SchemaBuilder.build(schemaSource, namespaceMapping, builder);
    }

    public void boostrapDataProviders(@Observes StartupEvent ev) {
        Map<String, DataProviderMapping> namespaces = namespaceMapping.mappings();
        for (Map.Entry<String, DataFetcher<?>> entry : DataFetcherBuilder.dataFetchers.entrySet()) {
            List<DataProviderConfig> dataProviderConfigs = ofNullable(namespaces.get(entry.getKey()))
                    .map(DataProviderMapping::dataProviders)
                    .orElse(emptyList());

            List<HttpProvider> httpProviders = dataProviderConfigs.stream()
                                                                  .map(dataProvider -> createHttpProvider(dataProvider.url()))
                                                                  .collect(toList());
            ((NamespaceFetcher) entry.getValue()).dataProviders.addAll(httpProviders);
        }
    }

    private HttpProvider createHttpProvider(String url) {
        return RestClientBuilder.newBuilder()
                                .baseUri(URI.create(url))
                                .build(HttpProvider.class);
    }
}
