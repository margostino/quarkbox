package org.quarkbox.provider;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import graphql.com.google.common.collect.Sets;
import org.quarkbox.serializer.InstantDeserializer;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class HttpResponse {

    public final String status;
    public final Map<String, Object> indicators;
    public final Map<String, IndicatorError> errors;
    @JsonProperty("timestamp")
    @JsonDeserialize(using = InstantDeserializer.class)
    public Instant timestamp;

    public HttpResponse() {
        this.status = null;
        this.indicators = Collections.emptyMap();
        this.errors = Collections.emptyMap();
        this.timestamp = null;
    }

    public HttpResponse(final Map<String, Object> indicators, String status) {
        this.status = status;
        this.indicators = indicators;
        this.errors = Collections.emptyMap();
        this.timestamp = null;
    }

    public HttpResponse(final Map<String, Object> indicators, String status, Instant timestamp) {
        this.status = status;
        this.indicators = indicators;
        this.errors = Collections.emptyMap();
        this.timestamp = timestamp;
    }

    public HttpResponse(final Map<String, Object> indicators, String status, Instant timestamp, final Map<String, IndicatorError> errors) {
        this.status = status;
        this.indicators = Collections.unmodifiableMap(indicators);
        this.timestamp = timestamp;
        this.errors = Collections.unmodifiableMap(errors);
    }

    public static HttpResponse emptyResult() {
        return new HttpResponse();
    }

    public boolean isEmpty() {
        return this.indicators.isEmpty() && this.errors.isEmpty();
    }

    public Set<String> indicatorNames() {
        return Sets.union(this.indicators.keySet(), this.errors.keySet());
    }

    @Override
    public String toString() {
        return String.format("Status: %s - Indicators %d", status, indicators.keySet().size());
    }
}
