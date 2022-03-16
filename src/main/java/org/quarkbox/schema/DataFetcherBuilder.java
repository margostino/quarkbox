package org.quarkbox.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.quarkbox.configuration.DataProviderConfig;
import org.quarkbox.configuration.DataProviderMapping;
import org.quarkbox.configuration.NamespaceMapping;
import org.quarkbox.provider.HttpProvider;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class DataFetcherBuilder {

    protected static GraphQLCodeRegistry build(NamespaceMapping namespaces, List<GraphQLFieldDefinition> indicators) {
        Map<String, DataProviderMapping> namespaceMapping = namespaces.mappings();

        Map<String, DataFetcher<?>> dataFetchers = new HashMap<>();

        for (GraphQLFieldDefinition indicator : indicators) {
            final String namespaceName = indicator.getName();
            List<DataProviderConfig> dataProviderConfigs = ofNullable(namespaceMapping.get(namespaceName))
                    .map(DataProviderMapping::dataProviders)
                    .orElse(emptyList());

            List<HttpProvider> httpProviders = dataProviderConfigs.stream()
                                                                  .map(dataProvider -> createHttpProvider(dataProvider.url()))
                                                                  .collect(toList());
            NamespaceFetcher namespaceFetcher = new NamespaceFetcher(namespaceName, httpProviders);
            dataFetchers.put(namespaceName, namespaceFetcher);
        }

        return GraphQLCodeRegistry.newCodeRegistry()
                                  .dataFetchers("Query", dataFetchers)
                                  .build();


        //.dataFetcher(coordinates("Query", "account"), namespaceFetcher)
    }

    private static HttpProvider createHttpProvider(String url) {
        return RestClientBuilder.newBuilder()
                                .baseUri(URI.create(url))
                                .build(HttpProvider.class);
    }

}
