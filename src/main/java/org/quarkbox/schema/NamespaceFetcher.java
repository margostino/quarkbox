package org.quarkbox.schema;

import com.google.common.base.CaseFormat;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import graphql.schema.GraphQLSchema;
import graphql.schema.SelectedField;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.quarkbox.domain.RequestedIndicator;
import org.quarkbox.provider.HttpProvider;
import org.quarkbox.provider.HttpResponse;
import org.quarkbox.provider.IndicatorsHttpRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.time.Duration.ofSeconds;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.quarkbox.domain.AppConstants.REQUEST_ID_HEADER;

@Getter
@AllArgsConstructor
public class NamespaceFetcher implements DataFetcher<Map<String, Object>> {

    private String name;
    public List<HttpProvider> dataProviders;

    @Override
    public Map<String, Object> get(DataFetchingEnvironment environment) {
        Optional<IndicatorsHttpRequest> request = createIndicatorsRequest(environment);

        List<Uni<HttpResponse>> asyncCallResponses = request.map(this::asyncCall)
                                                            .orElse(emptyList());

        return Uni.combine().all()
                  .unis(asyncCallResponses)
                  .combinedWith(this::combineProviderResponses)
                  .onFailure()
                  .invoke(this::fail)
                  .await()
                  .atMost(ofSeconds(1));
    }

    private List<Uni<HttpResponse>> asyncCall(IndicatorsHttpRequest request) {
        return dataProviders.stream()
                            .map(httpProvider -> httpProvider.query(request.getPayload()))
                            .collect(toList());
    }

    private Map<String, Object> combineProviderResponses(List<?> providerResponses) {
        Log.info("Size of responses: " + providerResponses.size());
        return providerResponses.stream()
                                .map(HttpResponse.class::cast)
                                .map(this::logging)
                                .map(response -> response.indicators)
                                .flatMap(map -> map.entrySet().stream())
                                .collect(Collectors.toMap(
                                        Map.Entry::getKey,
                                        Map.Entry::getValue));
    }

    private HttpResponse logging(HttpResponse response) {
        Log.info("Response is: " + response.toString());
        return response;
    }

    private Uni<Map<String, String>> fail(Throwable error) {
        Map<String, String> response = new HashMap<>();
        response.put("error", error.getMessage());
        Log.error(format("Error calling HTTP provider: %s", error.getMessage()), error);
        return Uni.createFrom().item(response);
    }

    private List<RequestedIndicator> getRequestedIndicators(DataFetchingEnvironment environment) {
        final GraphQLSchema schema = environment.getGraphQLSchema();
        final Map<String, List<SelectedField>> selectedIndicators = getSelectedIndicators(environment);

        return selectedIndicators.entrySet()
                                 .stream()
                                 .map(indicator -> createRequestedIndicators(schema, indicator))
                                 .collect(toList());
    }

    private String indicatorTypeLookup(GraphQLSchema schema, String namespace, String indicatorName) {
        final Optional<GraphQLOutputType> type = schema.getQueryType()
                                                       .getFieldDefinition(namespace)
                                                       .getType()
                                                       .getChildren()
                                                       .stream()
                                                       .map(GraphQLFieldDefinition.class::cast)
                                                       .filter(field -> field.getName().equalsIgnoreCase(indicatorName))
                                                       .map(GraphQLFieldDefinition::getType)
                                                       .findAny();

        return type.map(GraphQLScalarType.class::cast)
                   .map(scalar -> scalar.getName())
                   .orElse("String");
    }

    private Map<String, List<SelectedField>> getSelectedIndicators(DataFetchingEnvironment environment) {
        return environment.getSelectionSet().getFieldsGroupedByResultKey();
    }

    private RequestedIndicator createRequestedIndicators(GraphQLSchema schema, Map.Entry<String, List<SelectedField>> indicator) {
        final String indicatorName = indicator.getKey();
        List<SelectedField> selectedFields = indicator.getValue();
        final String namespace = selectedFields.stream()
                                               .map(selectedField -> selectedField.getFullyQualifiedName())
                                               .map(this::parseNamespace)
                                               .findAny()
                                               .orElse(indicatorName);
        return new RequestedIndicator(indicatorName, indicatorTypeLookup(schema, namespace, indicatorName));
    }

    private String parseNamespace(String fullyQualifiedName) {
        String[] parts = fullyQualifiedName.split("\\.");

        if (parts.length > 0) {
            String camelCaseNamespace = parts[0].replace("Indicators", "");
            return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelCaseNamespace);
        }

        return fullyQualifiedName;
    }

    private Optional<IndicatorsHttpRequest> createIndicatorsRequest(DataFetchingEnvironment environment) {
        // TODO: validate missing or invalid arguments

        List<RequestedIndicator> requestsIndicators = getRequestedIndicators(environment);
        Map<String, Object> requestedArguments = environment.getArguments();

        final Set<String> indicatorsToForward = requestsIndicators.stream()
                                                                  .map(RequestedIndicator::getIndicatorName)
                                                                  .collect(toSet());

        return ofNullable(IndicatorsHttpRequest.builder()
                                               .payload(buildIndicatorsRequest(requestedArguments, indicatorsToForward))
                                               .header(REQUEST_ID_HEADER, "tbd")
                                               .build());
    }

    private IndicatorsHttpRequest.Payload buildIndicatorsRequest(Map<String, Object> arguments, Set<String> indicators) {
        return IndicatorsHttpRequest.Payload.builder()
                                            .namespace(name)
                                            .arguments(arguments)
                                            .indicators(indicators)
                                            .build();
    }

}
