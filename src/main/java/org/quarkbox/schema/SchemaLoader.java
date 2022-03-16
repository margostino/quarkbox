package org.quarkbox.schema;

import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class SchemaLoader {

    public static TypeDefinitionRegistry load(String schemaSource) {
        String schema = readSchema(schemaSource);
        return new SchemaParser().parse(schema);
    }

    private static String readSchema(String schemaSource) {
        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(schemaSource);
            String data = readFromInputStream(inputStream);
            return data;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String readFromInputStream(InputStream inputStream) throws IOException {
        StringBuilder resultStringBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = br.readLine()) != null) {
                resultStringBuilder.append(line).append("\n");
            }
        }
        return resultStringBuilder.toString();
    }

}
