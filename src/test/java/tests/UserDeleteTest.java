package tests;

import io.qameta.allure.*;
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

@Epic("Registration cases")
@Feature("Registration")
public class UserDeleteTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test negative delete user blocked to delete")
    @DisplayName("Test negative DELETE - lock user")
    @Severity(SeverityLevel.BLOCKER)
    @Link("https://JIRA.ru/123456")
    @Test
    public void deleteLockUserTest(){
        //LOGIN
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login/",
                authData
        );

        //DELETE
        Response responseDelete = apiCoreRequests.makeDeleteRequests(
                "https://playground.learnqa.ru/api/user/2",
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        //System.out.println(responseDelete.asString());

        //GET DELETED USER
        Response responseGetDeleted = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/2",
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        String[] fields = {"id", "username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseGetDeleted,fields);
    }

    @Description("This test success delete user")
    @DisplayName("Test positive DELETE")
    @Severity(SeverityLevel.MINOR)
    @Link("https://JIRA.ru/123456")
    @Test
    public void deleteNewUserSuccessfully(){
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

        //DELETE
        Response responseDelete = apiCoreRequests.makeDeleteRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );


        //GET DELETED USER
        Response responseGetDeleted = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );

        Assertions.assertResponseTextEquals(responseGetDeleted,"User not found");
    }

    @Description("This test negative delete user - wrong auth data from othet user")
    @DisplayName("Test negative DELETE - wrong auth")
    @Severity(SeverityLevel.NORMAL)
    @Link("https://JIRA.ru/223456")
    @Test
    public void deleteNewUserFromOtherAuth(){
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests.makePostRequestsJSON(
                "https://playground.learnqa.ru/api/user/",
                userData

        );

        String userId = responseCreateAuth.getString("id");

        //LOGIN new user
        Map<String, String> authDataUser = new HashMap<>();
        authDataUser.put("email", userData.get("email"));
        authDataUser.put("password", userData.get("password"));

        Response responseGetAuthUser = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login/",
                authDataUser
        );

        //LOGIN other user
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login/",
                authData
        );

        //DELETE
        Response responseDelete = apiCoreRequests.makeDeleteRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuth, "x-csrf-token"),
                this.getCookie(responseGetAuth, "auth_sid")
        );


        //GET DELETED USER
        Response responseGetDeleted = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/"+userId,
                this.getHeader(responseGetAuthUser, "x-csrf-token"),
                this.getCookie(responseGetAuthUser, "auth_sid")
        );

        String[] fields = {"id", "username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseGetDeleted,fields);
    }
}
