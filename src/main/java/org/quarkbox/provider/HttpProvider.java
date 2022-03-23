package org.quarkbox.provider;

import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.quarkbox.filter.HttpResponseFilter;

import javax.ws.rs.POST;

@RegisterProvider(HttpResponseFilter.class)
@RegisterRestClient
public interface HttpProvider {

    @POST
    @Counted(value = "provider_counter", description = "How many primality checks have been performed.")
    @Timed(value = "provider_timer", description = "A measure of how long it takes to perform the primality test.")
    Uni<HttpResponse> query(IndicatorsHttpRequest.Payload request);
}
