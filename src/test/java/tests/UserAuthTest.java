package tests;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lib.BaseTestCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import lib.Assertions;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;

    @BeforeEach
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");
    }

    @Test
    public void testAuthUser(){
        Response responseCheckAuth = RestAssured
                .given()
                .header("x-csrf-token",this.header)
                .cookie("auth_sid", this.cookie)
                .get("https://playground.learnqa.ru/api/user/auth")
                .andReturn();

       Assertions.assertJsonByName(responseCheckAuth, "user_id", this.userIdOnAuth);
    }

    @Test
    public void testHomeworkCookie(){
        Response responseCheckCookie = RestAssured
                .get("https://playground.learnqa.ru/api/homework_cookie")
                .andReturn();

        System.out.println(responseCheckCookie.getCookies());

        Assertions.assertCookieByName(responseCheckCookie,"HomeWork","hw_value");
    }

    @Test
    public void testHomeworkHeader(){
        Response responseCheckHeader = RestAssured
                .get("https://playground.learnqa.ru/api/homework_header")
                .andReturn();

        System.out.println(responseCheckHeader.getHeaders());
        Assertions.assertHeaderByName(responseCheckHeader,"x-secret-homework-header","Some secret value");
    }


    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition){
        RequestSpecification spec = RestAssured.given();
        spec.baseUri("https://playground.learnqa.ru/api/user/auth");

        if(condition.equals("cookie")){
            spec.cookie("auth_sid", this.cookie);
        }else if(condition.equals("headers")){
            spec.header("x-csrf-token", this.header);
        }else {
            throw new IllegalArgumentException("Condition value is known: " + condition);
        }

        Response responseForCheck = spec.get().andReturn();
       Assertions.assertJsonByName(responseForCheck,"user_id", 0);
    }

    @ParameterizedTest
    @ValueSource(strings = {"badstring10", "goodstringforgoodtest23", "charsfortest_15", "charsfortest__16"})
    public void testStringLength(String string){

        Assertions.assertStringByLenght(string,15);

    }

    @ParameterizedTest
    @ArgumentsSource(testDataProvider.class)
    public void testUserAgent(String userAgent, String platform, String browser, String device){

        Header header = new Header("user-agent",userAgent);
        RequestSpecification spec = RestAssured.given();
        spec.header(header);
        spec.baseUri("https://playground.learnqa.ru/ajax/api/user_agent_check");
        Response responseForCheck = spec.get().andReturn();

        responseForCheck.prettyPrint();

        Assertions.assertUserAgentByParams(responseForCheck, platform,browser, device);
    }



}
