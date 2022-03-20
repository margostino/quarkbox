package org.quarkbox.schema;

import graphql.schema.DataFetcher;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.GraphQLFieldDefinition;
import io.smallrye.metrics.MetricRegistries;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;
import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.quarkbox.configuration.DataProviderConfig;
import org.quarkbox.configuration.DataProviderMapping;
import org.quarkbox.configuration.NamespaceMapping;
import org.quarkbox.provider.HttpProvider;

import javax.enterprise.event.Observes;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;

public class DataFetcherBuilder {


    public void build(@Observes HttpProvider httpProvider) {
        System.out.println(httpProvider.toString());
    }

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
            //List<HttpProvider> httpProviders = new ArrayList<>();
            NamespaceFetcher namespaceFetcher = new NamespaceFetcher(namespaceName, httpProviders);
            dataFetchers.put(namespaceName, namespaceFetcher);
        }

        return GraphQLCodeRegistry.newCodeRegistry()
                                  .dataFetchers("Query", dataFetchers)
                                  .build();

    }

    private static HttpProvider createHttpProvider(String url) {
        MetricRegistry metricRegistry = MetricRegistries.get(MetricRegistry.Type.APPLICATION);
        metricRegistry.timer(new MetricID("provider_timer", new Tag[]{new Tag("pepe", "juan")}));
        return RestClientBuilder.newBuilder()
                                .baseUri(URI.create(url))
                                .build(HttpProvider.class);
    }

}
