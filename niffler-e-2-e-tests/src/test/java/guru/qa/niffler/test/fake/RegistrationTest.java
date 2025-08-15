package guru.qa.niffler.test.fake;

import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

public class RegistrationTest {

    @Test
    void registrationApiFakeTest() throws InterruptedException {
        UsersApiClient userApiClient = new UsersApiClient();
        String username = RandomDataUtils.randomUsername();
        System.out.println("Сгенерированное имя: " + username);

        userApiClient.createUser(username, "12345");
    }

}
