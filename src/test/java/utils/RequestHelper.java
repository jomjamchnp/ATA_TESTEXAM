package utils;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * Reusable HTTP request helper.
 * Provides GET, POST, PUT, DELETE methods with auth and content-type pre-configured.
 */
public class RequestHelper {

    // ----------------------------------------------------------------
    // Base request spec with auth header and content type
    // ----------------------------------------------------------------
    private static RequestSpecification baseRequest() {
        return given()
                .baseUri(ApiConfig.BASE_URL)
                .header("Authorization", "Bearer " + ApiConfig.BEARER_TOKEN)
                .header("Content-Type", ApiConfig.CONTENT_TYPE)
                .log().ifValidationFails();
    }

    // ----------------------------------------------------------------
    // GET — with path only (e.g. /users/123)
    // ----------------------------------------------------------------
    public static Response get(String path) {
        return baseRequest()
                .when()
                .get(path)
                .then()
                .log().ifValidationFails()
                .extract().response();
    }

    // ----------------------------------------------------------------
    // GET — with query parameters (e.g. /users?status=active)
    // ----------------------------------------------------------------
    public static Response getWithParams(String path, Map<String, Object> params) {
        return baseRequest()
                .queryParams(params)
                .when()
                .get(path)
                .then()
                .log().ifValidationFails()
                .extract().response();
    }

    // ----------------------------------------------------------------
    // POST — with request body
    // ----------------------------------------------------------------
    public static Response post(String path, Object body) {
        return baseRequest()
                .body(body)
                .when()
                .post(path)
                .then()
                .log().ifValidationFails()
                .extract().response();
    }

    // ----------------------------------------------------------------
    // PUT — with request body
    // ----------------------------------------------------------------
    public static Response put(String path, Object body) {
        return baseRequest()
                .body(body)
                .when()
                .put(path)
                .then()
                .log().ifValidationFails()
                .extract().response();
    }

    // ----------------------------------------------------------------
    // DELETE — with path only
    // ----------------------------------------------------------------
    public static Response delete(String path) {
        return baseRequest()
                .when()
                .delete(path)
                .then()
                .log().ifValidationFails()
                .extract().response();
    }
}
