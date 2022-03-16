package org.quarkbox.provider;

import lombok.Builder;
import lombok.Getter;
import lombok.Singular;

import java.util.List;
import java.util.Map;

@Getter
public class IndicatorsHttpRequest {
    private final Payload payload;
    private final Map<String, String> headers;

    @Builder
    public IndicatorsHttpRequest(Payload payload, @Singular Map<String, String> headers) {
        this.payload = payload;
        this.headers = headers;
    }

    @Getter
    public static class Payload {
        private final String namespace;
        private final List<String> indicators;
        private final Map<String, Object> arguments;


        @Builder
        public Payload(String namespace, @Singular List<String> indicators, @Singular Map<String, Object> arguments) {
            this.namespace = namespace;
            this.indicators = indicators;
            this.arguments = arguments;
        }
    }
}
