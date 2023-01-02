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
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.junit.jupiter.api.DisplayName;
import lib.ApiCoreRequests;

@Epic("Authorisation cases")
@Feature("Authorisation")
public class UserAuthTest extends BaseTestCase {

    String cookie;
    String header;
    int userIdOnAuth;

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @BeforeEach
    public void loginUser(){
        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequests("https://playground.learnqa.ru/api/user/login", authData);

        this.cookie = this.getCookie(responseGetAuth,"auth_sid");
        this.header = this.getHeader(responseGetAuth,"x-csrf-token");
        this.userIdOnAuth = this.getIntFromJson(responseGetAuth,"user_id");
    }

    @Test
    @Description("This test successfully authorize user by email and password")
    @DisplayName("Test positive auth user")
    public void testAuthUser(){
        Response responseCheckAuth = apiCoreRequests
                .makeGetRequests("https://playground.learnqa.ru/api/user/auth",
                        this.header,
                        this.cookie);

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

    @Description("This test checks authorisation status w/o sending auth cookie or token ")
    @DisplayName("Test negative auth user")
    @ParameterizedTest
    @ValueSource(strings = {"cookie", "headers"})
    public void testNegativeAuthUser(String condition){

        if(condition.equals("cookie")){
            Response responseForCheck = apiCoreRequests.makeGetRequestsWithCookie(
                    "https://playground.learnqa.ru/api/user/auth",
                    this.cookie);
            Assertions.assertJsonByName(responseForCheck, "user_id", 0);
        }else if(condition.equals("headers")){
            Response responseForCheck = apiCoreRequests.makeGetRequestsWithToken("https://playground.learnqa.ru/api/user/auth",
                    this.header);
            Assertions.assertJsonByName(responseForCheck,"user_id", 0);
        }else{
            throw new IllegalArgumentException("Condition value is known: " + condition);
        }

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
