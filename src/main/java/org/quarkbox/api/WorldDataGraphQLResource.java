package org.quarkbox.api;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;

@GraphQLApi
public class WorldDataGraphQLResource {

    @Query
    @Description("placeholder")
    public String ping() {
        return "pong";
    }

}
