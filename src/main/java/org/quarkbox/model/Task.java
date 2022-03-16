package org.quarkbox.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.AllArgsConstructor;
import lombok.Builder;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

@Builder
@AllArgsConstructor
@JsonInclude(NON_EMPTY)
@JsonDeserialize(builder = Task.TaskBuilder.class)
public class Task {

    public String name;
    public String description;

    @JsonPOJOBuilder(withPrefix = "")
    public static class TaskBuilder {
    }

}
