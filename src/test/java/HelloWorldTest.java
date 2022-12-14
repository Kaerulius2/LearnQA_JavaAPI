import io.restassured.path.json.JsonPath;
import org.apache.http.util.Asserts;
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
    public void testRestAssuredEx7() throws InterruptedException {


            JsonPath response = RestAssured
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();

            String token = response.get("token");
            int seconds = response.get("seconds");


            JsonPath response2 = RestAssured
                    .given()
                    .queryParam("token",token)
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();

            String status = response2.get("status");

            assert status.equals("Job is NOT ready");

            Thread.sleep(seconds*1000);

            JsonPath response3 = RestAssured
                    .given()
                    .queryParam("token",token)
                    .get("https://playground.learnqa.ru/ajax/api/longtime_job")
                    .jsonPath();

            status = response3.get("status");

            assert status.equals("Job is ready");

            String result = response3.get("result");

            assert !result.equals(null);
    }
}
