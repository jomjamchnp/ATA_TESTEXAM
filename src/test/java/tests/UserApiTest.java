package tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.*;
import utils.RequestHelper;
import utils.TestDataLoader;
import utils.UserPayload;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * GoRest API Automation Tests
 * Covers: GET / POST / PUT / DELETE for /users endpoint
 *
 * Test order is fixed so that:
 *   1. GET  — reads an existing user & counts active users
 *   2. POST — creates a new user 
 *   3. PUT  — updates that user to active, compares active-user count
 *   4. DELETE — deletes the user, verifies 404
 */
@Epic("GoRest API")
@Feature("User Resource")
public class UserApiTest {

    // ----------------------------------------------------------------
    // Shared state across tests (set by POST, used by PUT & DELETE)
    // ----------------------------------------------------------------
    private static int    createdUserId;
    private static String createdUserEmail;
    private static int    activeUserCountBefore; // captured in GET test
    
    // Loaded from testData.json
    private static final int EXISTING_USER_ID = TestDataLoader.getExistingUserId();

    // ----------------------------------------------------------------
    // Helper — generate unique email to avoid GoRest duplicate errors
    // ----------------------------------------------------------------
    private String uniqueEmail() {
        return "testuser_" + UUID.randomUUID().toString().substring(0, 8) + "@example.com";
    }

    // ================================================================
    // TEST 1 — GET /users/{id}
    // ================================================================
    @Test(priority = 1)
    @Story("GET User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Fetch a specific user by ID. Validate status 200 and required fields. Count ALL active users via X-Pagination-Total header.")
    public void testGetUserById() {

        Response response = RequestHelper.get("/users/" + EXISTING_USER_ID);
        //Verify the status code is 200.
        Assert.assertEquals(response.getStatusCode(), 200,
                "Expected status code 200 for GET /users/{id}");

        int    id    = response.jsonPath().getInt("id");
        String name  = response.jsonPath().getString("name");
        String email = response.jsonPath().getString("email");
        // Validate that the response body contains the user ID, name, and email.
        Assert.assertNotNull(id,    "Response body must contain 'id'");
        Assert.assertNotNull(name,  "Response body must contain 'name'");
        Assert.assertNotNull(email, "Response body must contain 'email'");

        System.out.println("✅ GET user — id: " + id + " | name: " + name + " | email: " + email);

        // Validate how many user which status = active
        Map<String, Object> params = new HashMap<>();
        params.put("status", "active");
        params.put("per_page", 1); 
 
        Response activeResponse = RequestHelper.getWithParams("/users", params);
 
        Assert.assertEquals(activeResponse.getStatusCode(), 200,
                "Expected status code 200 when fetching active users");
 
        String totalHeader = activeResponse.getHeader("X-Pagination-Total");
 
        Assert.assertNotNull(totalHeader,
                "X-Pagination-Total header must be present in response");
 
        activeUserCountBefore = Integer.parseInt(totalHeader);
 
        System.out.println("✅ Total active users: "+ activeUserCountBefore); 
    }

    // ================================================================
    // TEST 2 — POST /users
    // ================================================================
    @Test(priority = 2)
    @Story("POST User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Create a new user with sample payload. Validate 201 and all payload fields. Confirm with GET.")
    public void testCreateUser() {

        createdUserEmail = uniqueEmail();

        UserPayload payload = TestDataLoader.getCreateUserPayload(createdUserEmail);

        Response postResponse = RequestHelper.post("/users", payload);

        Assert.assertEquals(postResponse.getStatusCode(), 201,
                "Expected status code 201 for POST /users");
        //Verify the status code is 201.
        createdUserId = postResponse.jsonPath().getInt("id");

        Assert.assertEquals(postResponse.jsonPath().getString("name"),   payload.getName(),
                "Response name must match payload");
        Assert.assertEquals(postResponse.jsonPath().getString("email"),  payload.getEmail(),
                "Response email must match payload");
        Assert.assertEquals(postResponse.jsonPath().getString("gender"), payload.getGender(),
                "Response gender must match payload");
        // NOTE: testData.json uses "status": "active" to match the assignment sample payload,
        // but the requirement states the POST response must be "inactive".
        // The assertion reflects the requirement — if the API returns "active", this test will
        // intentionally fail, proving the validation correctly catches the mismatch.
        Assert.assertEquals(postResponse.jsonPath().getString("status"), "inactive",
                "Response status must be 'inactive'");

        System.out.println("✅ POST user created — id: " + createdUserId + " | email: " + createdUserEmail);

        //Validate the new user is valid by using GET request reponse compare with POST reponse body
        Response getResponse = RequestHelper.get("/users/" + createdUserId);
        //Validate the response body matches the payload values name, email,gender, status (status must be inactive)
        Assert.assertEquals(getResponse.getStatusCode(), 200,
                "GET after POST must return 200");
        Assert.assertEquals(getResponse.jsonPath().getInt("id"),          createdUserId,
                "GET id must match POST id");
        Assert.assertEquals(getResponse.jsonPath().getString("name"),     payload.getName(),
                "GET name must match POST payload");
        Assert.assertEquals(getResponse.jsonPath().getString("email"),    payload.getEmail(),
                "GET email must match POST payload");
        Assert.assertEquals(getResponse.jsonPath().getString("gender"),   payload.getGender(),
                "GET gender must match POST payload");
        Assert.assertEquals(getResponse.jsonPath().getString("status"),   "inactive",
                "GET status must be 'inactive' (see POST assertion note above)");

        System.out.println("✅ GET after POST confirmed — user is valid and consistent");
    }

    // ================================================================
    // TEST 3 — PUT /users/{id}
    // ================================================================
    @Test(priority = 3)
    @Story("PUT User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update user name, email. Validate response and compare active user count.")
    public void testUpdateUser() {

        String updatedEmail = uniqueEmail();

        UserPayload updatePayload = TestDataLoader.getUpdateUserPayload(updatedEmail);

        // ---- 3a. PUT request ----
        Response putResponse = RequestHelper.put("/users/" + createdUserId, updatePayload);

        Assert.assertEquals(putResponse.getStatusCode(), 200,
                "Expected status code 200 for PUT /users/{id}");

        Assert.assertEquals(putResponse.jsonPath().getString("name"),   updatePayload.getName(),
                "Updated name must match payload");
        Assert.assertEquals(putResponse.jsonPath().getString("email"),  updatePayload.getEmail(),
                "Updated email must match payload");
        Assert.assertEquals(putResponse.jsonPath().getString("status"), "active",
                "Updated status must be 'active'");

        System.out.println("✅ PUT user updated — id: " + createdUserId + " | status: active");

        Map<String, Object> params = new HashMap<>();
        params.put("status", "active");

        Response activeResponse = RequestHelper.getWithParams("/users", params);

        String totalHeader = activeResponse.getHeader("X-Pagination-Total");

        Assert.assertNotNull(totalHeader,"X-Pagination-Total header must be present in response");
 
        int activeUserCountAfter = Integer.parseInt(totalHeader);
        
        Assert.assertEquals(activeResponse.getStatusCode(), 200,
                "Expected status 200 when fetching active users after PUT");

        System.out.println("✅ Active users count (after PUT): " + activeUserCountAfter
                + " | before: " + activeUserCountBefore);

        // compare number of user from 1.       
        Assert.assertTrue(activeUserCountAfter > activeUserCountBefore,
                "Active user count should increase after updating user status to active. "
                + "Before: " + activeUserCountBefore + " | After: " + activeUserCountAfter);

        // check new user match the payload — query by specific ID to avoid pagination issues
        Map<String, Object> userParams = new HashMap<>();
        userParams.put("status", "active");
        userParams.put("id", createdUserId);

        Response userInActiveResponse = RequestHelper.getWithParams("/users", userParams);

        List<Map<String, Object>> matchedUsers = userInActiveResponse.jsonPath().getList("$");

        Assert.assertFalse(matchedUsers.isEmpty(),
                "Updated user (id: " + createdUserId + ") must appear in GET /users?status=active list");

        Map<String, Object> activeUser = matchedUsers.get(0);
        Assert.assertEquals(activeUser.get("name"), updatePayload.getName(),
                "User name in active list must match updated payload");
        Assert.assertEquals(activeUser.get("email"), updatePayload.getEmail(),
                "User email in active list must match updated payload");

        System.out.println("✅ Updated user confirmed in active users list with matching name and email");
    }

    // ================================================================
    // TEST 4 — DELETE /users/{id}
    // ================================================================
    @Test(priority = 4)
    @Story("DELETE User")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Delete the created user. Validate 204. Then GET the user and expect 404.")
    public void testDeleteUser() {

        Response deleteResponse = RequestHelper.delete("/users/" + createdUserId);
        //Verify the status code is 204.
        Assert.assertEquals(deleteResponse.getStatusCode(), 204,
                "Expected status code 204 for DELETE /users/{id}");

        System.out.println("✅ DELETE user — id: " + createdUserId + " | status: 204 No Content");

        Response getResponse = RequestHelper.get("/users/" + createdUserId);
        //Attempt to fetch the user again and verify the status code is 404.
        Assert.assertEquals(getResponse.getStatusCode(), 404,
                "GET after DELETE must return 404 — user should no longer exist");

        System.out.println("✅ GET after DELETE returned 404 — user successfully removed");
    }
}