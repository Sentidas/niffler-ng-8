package guru.qa.niffler.test.web;

import guru.qa.niffler.service.impl.UsersApiClient;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OAuthTest {

    private UsersApiClient authApiClient = new UsersApiClient();


    @Test
    void oauthTest() throws IOException {

        UsersApiClient usersApiClient = new UsersApiClient();

        String token = usersApiClient.login("duck", "12345");
        System.out.println("token -> " + token);
        assertNotNull(token);
    }
}
