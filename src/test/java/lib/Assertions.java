package lib;

import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Assertions {

    public static void assertJsonByName(Response Response, String  name, int expectedValue){
        Response.then().assertThat().body("$", hasKey(name));
        int value = Response.jsonPath().getInt(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertJsonByName(Response Response, String  name, String expectedValue){
        Response.then().assertThat().body("$", hasKey(name));
        String value = Response.jsonPath().getString(name);
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    public static void assertCookieByName(Response Response, String  name, String expectedValue){
        //в принципе можно не делать первую проверку, в assertEquals тест упадет, т.к. при отсутствии куки и значение будет null
        //но мы сделаем
        assertTrue(Response.cookies().containsKey(name), "Cookie "+name+" is not present");

        String value = Response.getCookie(name);
        assertEquals(expectedValue, value, "Cookie value is not equal to expected value");

    }

    public static void assertHeaderByName(Response Response, String  name, String expectedValue){
        //в принципе можно не делать первую проверку, в assertEquals тест упадет, т.к. при отсутствии куки и значение будет null
        //но мы сделаем
        assertTrue(Response.headers().hasHeaderWithName(name), "Header "+name+" is not present");

        String value = Response.getHeader(name);
        assertEquals(expectedValue, value, "Header value is not equal to expected value");

    }

    public static void assertStringByLenght(String string, int length)
    {
        assertTrue(string.length()>length,"String length is more than " + length);
    }

    public static void assertUserAgentByParams(Response response, String platform, String browser, String device) {
        response.then().assertThat().body("$", hasKey("platform"));
        response.then().assertThat().body("$", hasKey("browser"));
        response.then().assertThat().body("$", hasKey("device"));
        String platformForCheck = response.jsonPath().getString("platform");
        String browserForCheck = response.jsonPath().getString("browser");
        String deviceForCheck = response.jsonPath().getString("device");

        assertEquals(platform, platformForCheck, "Platform value is not equal to expected value");
        assertEquals(browser, browserForCheck, "Browser value is not equal to expected value");
        assertEquals(device, deviceForCheck, "Device value is not equal to expected value");
    }

    public static void assertResponseTextEquals(Response Response, String expectedAnswer){
        assertEquals(
                expectedAnswer,
                Response.asString(),
                "Response text is not as expected"
        );
    }

    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode){
        assertEquals(
                expectedStatusCode,
                Response.statusCode(),
                "Response status code is not as expected"
        );
    }

    public static void assertJsonHasFields(Response Response, String[] expectedFieldNames){
        for(String expectedFieldName : expectedFieldNames) {
            Assertions.assertJsonHasField(Response, expectedFieldName);
        }
    }

    public static void assertJsonHasNotFields(Response Response, String[] expectedFieldNames){
        for(String expectedFieldName : expectedFieldNames) {
            Assertions.assertJsonHasNotField(Response, expectedFieldName);
        }
    }

    public static void assertJsonHasField(Response Response, String expectedFieldName){
        Response.then().assertThat().body("$", hasKey(expectedFieldName));
    }

    public static void assertJsonHasNotField(Response Response, String unexpectedFieldName){
        Response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
    }
}
