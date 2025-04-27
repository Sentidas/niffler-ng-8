package guru.qa.niffler.test.web;

import guru.qa.niffler.model.auth.UserJson;
import guru.qa.niffler.service.service.AuthDbClient;
import org.junit.jupiter.api.Test;

public class JDBCRegistrationTest {

    @Test
    void createSpend() {
        AuthDbClient authDbClient = new AuthDbClient();

        UserJson user = authDbClient.createUser(
                new UserJson(
                        null,
                        "alex",
                        "12345",
                        true,
                        true,
                        true,
                        true
                )
        );
        System.out.println(user);
    }
}



