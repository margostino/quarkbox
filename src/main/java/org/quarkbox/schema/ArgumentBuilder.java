package org.quarkbox.schema;

import graphql.language.FieldDefinition;
import graphql.language.InputValueDefinition;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLScalarType;

import java.util.ArrayList;
import java.util.List;

import static org.quarkbox.schema.TypeBuilder.getScalarType;

public class ArgumentBuilder {

    protected static List<GraphQLArgument> build(FieldDefinition fieldDefinition) {
        List<GraphQLArgument> arguments = new ArrayList<>();

        for (InputValueDefinition input : fieldDefinition.getInputValueDefinitions()) {
            GraphQLScalarType argumentType = getScalarType(input.getType());
            GraphQLArgument argument = GraphQLArgument.newArgument()
                                                      .name(input.getName())
                                                      .type(argumentType)
                                                      .build();
            arguments.add(argument);
        }
        return arguments;
    }

}
