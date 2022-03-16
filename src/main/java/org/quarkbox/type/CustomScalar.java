package org.quarkbox.type;

import graphql.scalar.GraphqlIntCoercing;
import graphql.scalar.GraphqlStringCoercing;
import graphql.schema.GraphQLScalarType;

public class CustomScalar {

    public static GraphQLScalarType GraphQLLong = GraphQLScalarType.newScalar()
                                                                   .name("Long")
                                                                   .description("Custom Built-in Float")
                                                                   .coercing(new GraphqlIntCoercing())
                                                                   .build();

    public static GraphQLScalarType GraphQLInvalid = GraphQLScalarType.newScalar()
                                                                      .name("Invalid")
                                                                      .description("Invalid Scalar")
                                                                      .coercing(new GraphqlStringCoercing())
                                                                      .build();

}
