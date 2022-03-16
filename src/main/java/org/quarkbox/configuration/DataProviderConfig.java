package org.quarkbox.configuration;

import io.smallrye.config.WithName;

public interface DataProviderConfig {

    String name();

    @WithName("url")
    String url();

    String type();

}
