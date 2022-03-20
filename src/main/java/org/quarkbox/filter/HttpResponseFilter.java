package org.quarkbox.filter;

import io.quarkus.logging.Log;
import io.smallrye.metrics.MetricRegistries;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.Tag;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import java.io.IOException;

import static java.lang.String.format;

public class HttpResponseFilter implements ClientResponseFilter {

    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {
        final String uri = requestContext.getUri().toString();
        final int status = responseContext.getStatus();
        final String contentLength = responseContext.getHeaderString("Content-Length");
//        MetricRegistry metricRegistry = MetricRegistries.get(MetricRegistry.Type.APPLICATION);
//        metricRegistry.getMetric(new MetricID("provider_timer").addTag(new Tag("sarlanga", "fruta"))
        Log.info(format("Call to %s with status %d and content length %s", uri, status, contentLength));
    }

}
