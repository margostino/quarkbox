package org.quarkbox.api;

//import io.micrometer.core.instrument.MeterRegistry;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.quarkbox.model.Task;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class TaskResource {

//    private final MeterRegistry registry;
//
//    TaskResource(MeterRegistry registry) {
//        this.registry = registry;
//    }

    @POST
    @Path("/task")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Create new task",
               description = "Create new task with name and description.")
    @APIResponse(description = "A new task with ID",
                 content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Task.class)))
    @APIResponse(responseCode = "400", description = "We were unable to create a session with the provided data. Some field constraint was violated.")
    @APIResponse(responseCode = "403", description = "You were not authorized to execute this operation.")
    public Uni<Task> create(Task task) {
        return Uni.createFrom()
                  .item(new Task(task.name, task.description));
    }

//    @GET
//    @Path("/healthcheck")
//    @Produces(MediaType.TEXT_PLAIN)
//    public Uni<String> healthcheck() {
//        return healthCheckProvider.healthCheck();
//    }

}