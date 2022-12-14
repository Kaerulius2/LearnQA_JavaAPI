import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloWorldTest {


    @Test
    public void testRestAssuredEx5(){

        Response response = RestAssured
                .given()
                .redirects()
                .follow(false)
              .get("https://playground.learnqa.ru/api/long_redirect")
              .andReturn();

        System.out.println(response.getHeader("location"));
    }
}
