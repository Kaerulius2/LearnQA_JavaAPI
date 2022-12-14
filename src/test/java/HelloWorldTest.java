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

        JsonPath response = RestAssured
              .get("https://playground.learnqa.ru/api/get_json_homework")
              .jsonPath();

        List messages = response.getList("messages.message");
        System.out.println(messages.get(1));

    }
}
