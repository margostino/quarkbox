package org.quarkbox.provider;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.quarkbox.filter.HttpResponseFilter;

import javax.ws.rs.POST;

@RegisterProvider(HttpResponseFilter.class)
public interface HttpProvider {

    @POST
    @Counted(name = "provider_counter", description = "How many primality checks have been performed.")
    @Timed(name = "provider_timer", description = "A measure of how long it takes to perform the primality test.", unit = MetricUnits.MILLISECONDS, absolute = true)
    Uni<HttpResponse> query(IndicatorsHttpRequest.Payload request);

}
