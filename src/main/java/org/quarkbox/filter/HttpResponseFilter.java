package org.quarkbox.filter;

import io.quarkus.logging.Log;

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
        Log.info(format("Call to %s with status %d and content length %s", uri, status, contentLength));
    }
}
