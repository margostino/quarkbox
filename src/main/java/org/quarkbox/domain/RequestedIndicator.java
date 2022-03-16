package org.quarkbox.domain;

public class RequestedIndicator {
    private String indicatorName;
    private String indicatorType;

    public RequestedIndicator(String indicatorName, String indicatorType) {
        this.indicatorName = indicatorName;
        this.indicatorType = indicatorType;
    }

    public String getIndicatorName() {
        return indicatorName;
    }

    public String getIndicatorType() {
        return indicatorType;
    }

    @Override
    public String toString() {
        return "RequestedIndicatorDTO{" +
                "indicatorName='" + indicatorName + '\'' +
                ", indicatorType='" + indicatorType + '\'' +
                '}';
    }
}
