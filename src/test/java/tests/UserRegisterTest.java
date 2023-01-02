package tests;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;

@Epic("Registration cases")
@Feature("Registration")
public class UserRegisterTest extends BaseTestCase {

    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("This test negative register user by existing email")
    @DisplayName("Test negative register - ex.email")
    @Test
    public void testCreateUserWithExistingEmail(){
        String email = "vinkotov@example.com";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Users with email '" + email + "' already exists");
    }



    @Description("This test negative register user without one field")
    @DisplayName("Test negative register - less fields")
    @ParameterizedTest
    @ValueSource(strings = {"email", "password", "firstName", "lastName", "username"})
    public void testNegativeAuthUserWithoutOneField(String field){
        Map<String, String> userData = new HashMap<>();
        userData = DataGenerator.getRegistrationDataWithoutField(field);

        Response responseCreateAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The following required params are missed: "+field);

    }

    @Description("This test negative register user by wrong email format")
    @DisplayName("Test negative register - wrong email")
    @Test
    public void testCreateUserWithWrongEmail(){
        String email = "agolubkovtest.ru";

        Map<String, String> userData = new HashMap<>();
        userData.put("email", email);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"Invalid email format");
    }

    @Description("This test negative register user by very short username")
    @DisplayName("Test negative register - short username")
    @Test
    public void testCreateUserWithShortUsername(){
        String username = "a";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'username' field is too short");
    }

    @Description("This test negative register user by very long username")
    @DisplayName("Test negative register - long username")
    @Test
    public void testCreateUserWithLongUsername(){
        String username = "WjfBjBVoQPGrBjaHucDrRnkmLwHkqqBYWhCAGFacSxjDgRSaekJcIKaypRZspqeNvncbpdokBUMswuweiMkVqpHpxWSkVQJTMUQcawcKCTjDCgkfBdYo" +
                "HHINMINBcBhhjWfllHQBqcijPEdqwwPDzMyxmumHPFYJFEarbhOZKVffCoYTYGFXGAKpOwiZLmcynqNYtZyokiykWUCqTQcVMEqsMkcZUxLzaQkmlGYDTkJDxTMiAphKvwAYGtX";

        Map<String, String> userData = new HashMap<>();
        userData.put("username", username);
        userData = DataGenerator.getRegistrationData(userData);

        Response responseCreateAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth,400);
        Assertions.assertResponseTextEquals(responseCreateAuth,"The value of 'username' field is too long");
    }

    @Description("This test successfully register user by random email")
    @DisplayName("Test POSITIVE register user")
    @Test
    public void testCreateUserSuccessfully(){

        Map<String, String> userData = DataGenerator.getRegistrationData();

        Response responseCreateAuth = apiCoreRequests.makePostRequests(
                "https://playground.learnqa.ru/api/user/",
                userData
        );

        Assertions.assertResponseCodeEquals(responseCreateAuth,200);
        Assertions.assertJsonHasField(responseCreateAuth, "id");
        System.out.println(responseCreateAuth.asString());
    }

}
