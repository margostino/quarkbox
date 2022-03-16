package org.quarkbox.provider;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.quarkbox.filter.HttpResponseFilter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@RegisterProvider(HttpResponseFilter.class)
@RegisterRestClient(configKey = "healthCheck")
public interface HealthCheckHttpProvider {

    @GET
    @Path("/healthcheck")
    @Timeout(value = 2000)
    @Retry(maxRetries = 1)
    @Fallback(HealthCheckFallback.class)
    Uni<String> healthCheck();

    class HealthCheckFallback implements FallbackHandler<Uni<String>> {

        public Uni<String> handle(ExecutionContext context) {
            Log.info("FALLBACK!");
            return Uni.createFrom().item("fallback");
        }

    }

}
