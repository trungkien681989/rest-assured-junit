import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class LoginTest {

  @BeforeAll
  public static void initialize() {
    RestAssured.baseURI = "http://myapp.k8s.stg.test.com";
  }

  @Test
  @Description("TC_004 Test invalid password (e.g., wrong password for a valid username)")
  @Feature("Login")
  @Owner("Kevin")
  public void loginFailTest() {
    String validUsername = "QaAdminUser";
    String invalidPassword = "invalidPassword";

    String loginFailPayload = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", validUsername, invalidPassword);

    Response response = given()
            .header("Content-Type", "application/json")
            .body(loginFailPayload)
            .when()
            .post("/myapp/login")
            .then()
            .extract().response();

    assertEquals(401, response.statusCode(), "Expected status code 401 for invalid credentials.");

    // Validate the error message (assuming the API returns an error message)
    String errorMessage = response.jsonPath().getString("message");  // Assuming the error message is in the 'message' field
    assertEquals("Invalid credentials", errorMessage, "Expected error message for invalid credentials.");
  }

  @Test
  @Description("TC_005 Test valid username and password for successful login")
  @Feature("Login")
  @Owner("Kevin")
  public void loginSuccessTest() {
    String validUsername = "QaAdminUser";
    String validPassword = "myPassword";

    String loginSuccessPayload = String.format("{\"username\": \"%s\", \"password\": \"%s\"}", validUsername, validPassword);

    Response response = given()
            .header("Content-Type", "application/json")
            .body(loginSuccessPayload)
            .when()
            .post("/myapp/login")
            .then()
            .extract().response();

    assertEquals(200, response.statusCode(), "Expected status code 200 for successful login.");

    String authToken = response.jsonPath().getString("auth_token");  // Assuming the token is returned as 'auth_token'
    assertNotNull(authToken, "Expected auth_token to be present in the response.");

    String userId = response.jsonPath().getString("user_id");  // Assuming the user ID is returned as 'user_id'
    assertNotNull(userId, "Expected user_id to be present in the response.");
  }
}
