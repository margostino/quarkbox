package org.quarkbox.provider;

public class IndicatorError {
    public final String namespace;
    public final String providerName;
    public final String indicatorName;
    public final String errorMessage;

    public IndicatorError(final String namespace, final String retrieverName, final String indicatorName, final String errorMessage) {
        this.namespace = namespace;
        this.providerName = retrieverName;
        this.indicatorName = indicatorName;
        this.errorMessage = errorMessage;
    }
}