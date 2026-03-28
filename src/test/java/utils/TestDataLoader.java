package utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;

/**
 * Loads test data from src/test/resources/testData.json
 */
public class TestDataLoader {

    private static final JsonNode root;

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = TestDataLoader.class
                    .getClassLoader()
                    .getResourceAsStream("testData.json");
            root = mapper.readTree(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load testData.json", e);
        }
    }

    public static UserPayload getCreateUserPayload(String email) {
        JsonNode node = root.get("createUser");
        return new UserPayload(
                node.get("name").asText(),
                email,
                node.get("gender").asText(),
                node.get("status").asText()
        );
    }

    public static UserPayload getUpdateUserPayload(String email) {
        JsonNode node = root.get("updateUser");
        return new UserPayload(
                node.get("name").asText(),
                email,
                node.get("gender").asText(),
                node.get("status").asText()
        );
    }
}
