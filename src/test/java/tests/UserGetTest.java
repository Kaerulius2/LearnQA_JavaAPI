package tests;

import groovyjarjarantlr4.v4.codegen.model.SrcOp;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    public void testGetUserDataNotAuth() {

        Response responseUserData = apiCoreRequests.makeGetRequestOnly("https://playground.learnqa.ru/api/user/2");

        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstname");
        Assertions.assertJsonHasNotField(responseUserData, "lastname");
        Assertions.assertJsonHasNotField(responseUserData, "email");

    }

    @Test
    public void testGetUserDetailsAuthAsSameUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );


        Response responseUserData = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/2",
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));


        String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasFields(responseUserData, expectedFields);

    }

    @Test
    public void testGetUserDetailsAuthAsOtherUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/login",
                authData
        );


        Response responseUserData = apiCoreRequests.makeGetRequests(
                "https://playground.learnqa.ru/api/user/1",
                this.getHeader(responseGetAuth,"x-csrf-token"),
                this.getCookie(responseGetAuth,"auth_sid"));


        String[] expectedFields = {"id", "firstName", "lastName", "email"};
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotFields(responseUserData, expectedFields);

    }


}
