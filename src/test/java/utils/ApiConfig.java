package utils;
import io.github.cdimascio.dotenv.Dotenv;

/**
 * Central configuration for GoRest API.
 * Replace BEARER_TOKEN with your actual GoRest API token.
 */
public class ApiConfig {

    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

    public static final String BASE_URL          = "https://gorest.co.in/public/v2";
    public static final String BEARER_TOKEN      = dotenv.get("GOREST_TOKEN", System.getenv("GOREST_TOKEN"));
    public static final String CONTENT_TYPE   = "application/json";

    private ApiConfig() {
        // Utility class — no instantiation
    }
}
