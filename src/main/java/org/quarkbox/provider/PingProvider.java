package org.quarkbox.provider;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.quarkbox.filter.HttpResponseFilter;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@RegisterProvider(HttpResponseFilter.class)
@RegisterRestClient(configKey = "ping")
public interface PingProvider {

    @GET
    @Path("/ping")
    Uni<String> ping();

}
