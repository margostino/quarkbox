package org.quarkbox.configuration;

import io.quarkus.runtime.annotations.StaticInitSafe;
import io.smallrye.config.ConfigMapping;

import java.util.Map;

@StaticInitSafe
@ConfigMapping(prefix = "namespace")
public interface NamespaceMapping {

    Map<String, DataProviderMapping> mappings();

}
