package org.quarkbox.api;

import org.eclipse.microprofile.graphql.Description;
import org.eclipse.microprofile.graphql.GraphQLApi;
import org.eclipse.microprofile.graphql.Query;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.quarkbox.provider.HttpProvider;

import javax.inject.Inject;

@GraphQLApi
public class WorldDataGraphQLResource {

    @Query
    @Description("placeholder")
    public String ping() {
        return "pong";
    }

}
