package org.quarkbox.api;

import io.smallrye.mutiny.Uni;
import org.quarkbox.model.Task;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/")
public class TaskResource {

    @POST
    @Path("/task")
    @Produces(MediaType.APPLICATION_JSON)
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