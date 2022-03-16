package org.quarkbox.schema;

import graphql.Directives;
import graphql.introspection.Introspection;
import graphql.language.Argument;
import graphql.language.Directive;
import graphql.language.DirectiveDefinition;
import graphql.language.DirectiveLocation;
import graphql.language.InputValueDefinition;
import graphql.schema.GraphQLArgument;
import graphql.schema.GraphQLDirective;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static graphql.Scalars.GraphQLString;
import static org.quarkbox.schema.TypeBuilder.getListType;

public class DirectiveBuilder {

    protected static Set<GraphQLDirective> build(TypeDefinitionRegistry schemaDefinition) {
        Set<GraphQLDirective> directives = new HashSet<>();
        Map<String, DirectiveDefinition> directiveDefinitions = schemaDefinition.getDirectiveDefinitions();

        for (Map.Entry<String, DirectiveDefinition> directiveDefinition : directiveDefinitions.entrySet()) {
            List<InputValueDefinition> inputValueDefinitions = directiveDefinition.getValue().getInputValueDefinitions();
            List<GraphQLArgument> directiveArguments = new ArrayList<>();
            for (InputValueDefinition input : inputValueDefinitions) {
                GraphQLInputType argumentType = getListType(input.getType());

                GraphQLArgument argument = GraphQLArgument.newArgument()
                                                          .name(input.getName())
                                                          .type(argumentType)
                                                          .defaultValueLiteral(input.getDefaultValue())
                                                          .build();

                directiveArguments.add(argument);
            }

            List<DirectiveLocation> directiveDefinitionLocations = directiveDefinition.getValue().getDirectiveLocations();
            List<Introspection.DirectiveLocation> directiveLocations = new ArrayList<>();
            for (DirectiveLocation directiveLocation : directiveDefinitionLocations) {
                directiveLocations.add(Introspection.DirectiveLocation.valueOf(directiveLocation.getName()));
            }

            GraphQLDirective directive = GraphQLDirective.newDirective()
                                                         .name(directiveDefinition.getKey())
                                                         .replaceArguments(directiveArguments)
                                                         .definition(directiveDefinition.getValue())
                                                         .validLocations(directiveLocations.toArray(new Introspection.DirectiveLocation[0]))
                                                         .build();
            directives.add(directive);

        }

        directives.add(Directives.DeprecatedDirective);

        return directives;
    }

    protected static List<GraphQLDirective> build(List<Directive> directives, Set<GraphQLDirective> queryDirectives) {
        List<GraphQLDirective> indicatorDirectives = new ArrayList<>();

        for (Directive directive : directives) {

            Optional<GraphQLDirective> schemaDirective = getSchemaDirective(queryDirectives, directive.getName());

            List<Argument> directiveArguments = directive.getArguments();
            List<GraphQLArgument> indicatorDirectiveArguments = new ArrayList<>();

            for (Argument directiveArgument : directiveArguments) {
                GraphQLArgument argument = GraphQLArgument.newArgument()
                                                          .name(directiveArgument.getName())
                                                          .type(GraphQLList.list(GraphQLString))
                                                          .valueLiteral(directiveArgument.getValue())
                                                          .build();
                indicatorDirectiveArguments.add(argument);
            }

            GraphQLDirective indicatorDirective = GraphQLDirective.newDirective()
                                                                  .name(directive.getName())
                                                                  .definition(schemaDirective.get().getDefinition())
                                                                  .replaceArguments(indicatorDirectiveArguments)
                                                                  .build();

            indicatorDirectives.add(indicatorDirective);

        }

        return indicatorDirectives;
    }

    private static Optional<GraphQLDirective> getSchemaDirective(Set<GraphQLDirective> queryDirectives, String fieldDirective) {
        return queryDirectives.stream()
                              .filter(directive -> directive.getName().equalsIgnoreCase(fieldDirective))
                              .findFirst();
    }

}
