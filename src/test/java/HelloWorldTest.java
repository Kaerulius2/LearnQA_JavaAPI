import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HelloWorldTest {


    @Test
    public void testRestAssuredEx7(){
        int statusCode;
        String path = "https://playground.learnqa.ru/api/long_redirect";
        do {
            Response response = RestAssured
                    .given()
                    .redirects()
                    .follow(false)
                    .get(path)
                    .andReturn();
            statusCode = response.getStatusCode();
            path = response.getHeader("location");
            System.out.println(path);
        } while (statusCode!=200);



    }
}
