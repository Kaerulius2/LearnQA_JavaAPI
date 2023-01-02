package tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("User edit cases")
@Feature("Edit User Info")
public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();
    @Description("This test successfully edit user. New FirstName.")
    @DisplayName("Test positive edit user - PUT")
    @Severity(SeverityLevel.NORMAL)
    @Link("https://JIRA.ru/333556")
    @Test
    public void testEditJustCreatedTest(){
    //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login/")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/"+userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/"+userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Description("This negative test to edit user. Firstname is the same")
    @DisplayName("Test negative edit user - no auth")
    @Severity(SeverityLevel.BLOCKER)
    @Link("https://JIRA.ru/333456")
    @Test
    public void testEditWithoutAuth(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestsJSON(
                "https://playground.learnqa.ru/api/user/",
                userData

        );

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login/",
                authData
        );

        //GET to have a oldName
        Response responseUserData;
        responseUserData = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        String oldName = responseUserData.jsonPath().getString("firstName");

        //EDIT without auth
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                editData
        );


        //GET after edit
        responseUserData = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        Assertions.assertJsonByName(responseUserData, "firstName", oldName);

    }

    @Description("This negative test to edit user. Firstname is the same")
    @DisplayName("Test negative edit user - auth from other user")
    @Severity(SeverityLevel.BLOCKER)
    @Link("https://JIRA.ru/333457")
    @Test
    public void testEditWithOtherUserAuth(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestsJSON(
                "https://playground.learnqa.ru/api/user/",
                userData

        );

        String userId = responseCreateAuth.getString("id");

        //LOGIN in new User
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login/",
                authData
        );

        //GET to have a oldName
        Response responseUserData;
        responseUserData = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );
        String oldName = responseUserData.jsonPath().getString("firstName");

        //LOGIN in old user to have authdata
        Map<String, String> authDataOld = new HashMap<>();
        authDataOld.put("email", "vinkotov@example.com");
        authDataOld.put("password", "1234");
        Response responseGetAuthOld = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login/",
                authDataOld
        );

        //EDIT with illegal auth
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests.makePutRequestsWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/"+userId,
                editData,
                responseGetAuthOld.getHeader("x-csrf-token"),
                responseGetAuthOld.getCookie("auth_sid")
          );


        //GET after edit
        responseUserData = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );
        //assert that no change in firstName
        Assertions.assertJsonByName(responseUserData, "firstName", oldName);

    }

    @Description("This negative test to edit user. Email without @")
    @DisplayName("Test negative edit user - bad email")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://JIRA.ru/333459")
    @Test
    public void testEditWithBadEmail(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestsJSON(
                "https://playground.learnqa.ru/api/user/",
                userData

        );

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login/",
                authData
        );

        //GET to have a oldName
        Response responseUserData;
        responseUserData = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        String oldEmail = responseUserData.jsonPath().getString("email");

        //EDIT with bad email
        String newEmail = "agolubkov.ru";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        Response responseEditUser = apiCoreRequests.makePutRequestsWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/"+userId,
                editData,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );


        //GET after edit
        responseUserData = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        Assertions.assertJsonByName(responseUserData, "email", oldEmail);

    }

    @Description("This negative test to edit user. Firstname is too short")
    @DisplayName("Test negative edit user - bad firstname")
    @Severity(SeverityLevel.CRITICAL)
    @Link("https://JIRA.ru/3334779")
    @Test
    public void testEditWithBadFirstName(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestsJSON(
                "https://playground.learnqa.ru/api/user/",
                userData

        );

        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login/",
                authData
        );

        //GET to have a oldName
        Response responseUserData;
        responseUserData = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        String oldFirstName = responseUserData.jsonPath().getString("firstName");

        //EDIT with bad email
        String newFirstName = "a";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newFirstName);

        Response responseEditUser = apiCoreRequests.makePutRequestsWithTokenAndCookie(
                "https://playground.learnqa.ru/api/user/"+userId,
                editData,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );


        //GET after edit
        responseUserData = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        Assertions.assertJsonByName(responseUserData, "firstName", oldFirstName);

    }
}
