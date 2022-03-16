package org.quarkbox.provider;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.quarkbox.filter.HttpResponseFilter;

import javax.ws.rs.POST;

@RegisterProvider(HttpResponseFilter.class)
public interface HttpProvider {

    @POST
    Uni<HttpResponse> query(IndicatorsHttpRequest.Payload request);

}
