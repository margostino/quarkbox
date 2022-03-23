package org.quarkbox.configuration;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Tag;
import io.micrometer.core.instrument.config.MeterFilter;
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.quarkus.logging.Log;
import io.quarkus.micrometer.runtime.MeterFilterConstraint;

import javax.enterprise.event.Observes;
import javax.inject.Singleton;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import java.util.Arrays;

@Singleton
public class CustomMetricConfiguration implements MeterFilter {

    //@ConfigProperty(name = "deployment.env")
    //String deploymentEnv;

    @Consumes
    @Override
    public Meter.Id map(Meter.Id id) {
        if (id.getName().startsWith("http.client")) {
            Log.info("add some custom tag if condition");
            return id;
        }
        return id;
    }

    /**
     * Define common tags that apply only to a Prometheus Registry
     */
    @Produces
    @Singleton
    @MeterFilterConstraint(applyTo = PrometheusMeterRegistry.class)
    public MeterFilter configurePrometheusRegistries() {
        return MeterFilter.commonTags(Arrays.asList(
                Tag.of("registry", "prometheus")));
    }

    /**
     * Define common tags that apply globally
     */
    @Produces
    @Singleton
    public MeterFilter configureAllRegistries() {
        return MeterFilter.commonTags(Arrays.asList(
                Tag.of("env", "dev")));
    }

    public Meter.Id ss(@Observes Meter.Id id) {
        return id;
    }

    /**
     * Enable histogram buckets for a specific timer
     */
    @Produces
    @Singleton
    public MeterFilter enableHistogram() {
        return new MeterFilter() {
            @Override
            public DistributionStatisticConfig configure(Meter.Id id, DistributionStatisticConfig config) {
                if (id.getName().startsWith("something")) {
                    return DistributionStatisticConfig.builder()
                                                      .percentiles(0.5, 0.95)     // median and 95th percentile, not aggregable
                                                      .percentilesHistogram(true) // histogram buckets (e.g. prometheus histogram_quantile)
                                                      .build()
                                                      .merge(config);
                }
                return config;
            }
        };
    }
}