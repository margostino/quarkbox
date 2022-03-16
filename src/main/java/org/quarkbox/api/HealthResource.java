package org.quarkbox.api;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quarkbox.provider.HealthCheckHttpProvider;
import org.quarkbox.provider.PingProvider;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static java.lang.String.format;

@Path("/")
public class HealthResource {

    @Inject
    @RestClient
    PingProvider pingProvider;

    @Inject
    @RestClient
    HealthCheckHttpProvider healthCheckProvider;

    @GET
    @Path("/ping")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> ping() {
        return pingProvider.ping();
    }

    @GET
    @Path("/healthcheck")
    @Produces(MediaType.TEXT_PLAIN)
    public Uni<String> healthcheck() {
        return healthCheckProvider.healthCheck();
    }

}