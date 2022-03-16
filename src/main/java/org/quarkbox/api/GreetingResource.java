package org.quarkbox.api;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.quarkbox.configuration.GreetingConfig;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import static java.lang.String.format;

@Path("/")
public class GreetingResource {

    @ConfigProperty(name = "quarkbox.quote")
    String quote;

    @Inject
    GreetingConfig greetingConfig;

    @GET
    @Path("/greeting")
    @Produces(MediaType.TEXT_PLAIN)
    public String greeting() {
        final String message = greetingConfig.message() != null ? greetingConfig.message() : "Hello";
        return format("%s %s", message, "Quarkbox!");
    }

    @GET
    @Path("/hello/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloName(@PathParam("name") String name, @QueryParam("showQuote") boolean showQuote) {
        final String greeting = format("Hello %s!", name);
        return showQuote ? format("%s\n%s.", greeting, quote) : greeting;
    }

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello World!";
    }
}