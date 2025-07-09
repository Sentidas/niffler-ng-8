package guru.qa.niffler.test.web.ServiceTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.TestData;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UserApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

public class UserApiTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void updateUser() throws IOException {
        UsersClient usersApiClient = new UserApiClient();
        UserJson updatedUser =
                new UserJson(
                        null,
                        "ferret55",
                        null,
                        null,
                        null,
                        "angry ferret9",
                        RandomDataUtils.randomPhoto(),
                        null,
                        null,
                        null
                );
        usersApiClient.updateUser("ferret55", updatedUser);
    }

    @Test
    void findUserByUserName() {
        UsersClient usersApiClient = new UserApiClient();
        Optional<UserJson> user = usersApiClient.findUserByUsername("ferret55");

        if (user.isEmpty()) {
            System.out.println("Пользователь не найден в базе.");
            return;
        }

        UserJson u = user.get();
        System.out.println("User найден: '" + u.username() + "' c основной валютой: " + u.currency());
    }

    @Test
    void createOutcomeInvitations() {
        UsersClient usersApiClient = new UserApiClient();
        UserJson targetUser =
                new UserJson(
                        null,
                        "ferret55",
                        CurrencyValues.USD,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        new TestData(
                                "12345",
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                );
        usersApiClient.createOutcomeInvitations(targetUser, 2);
    }

    @Test
    void createIncomeInvitations() {
        UsersClient usersApiClient = new UserApiClient();
        UserJson targetUser =
                new UserJson(
                        null,
                        "ferret55",
                        CurrencyValues.EUR,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        new TestData(
                                "12345",
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )
                );
        usersApiClient.createIncomeInvitations(targetUser, 1);
    }

    @Test
    void addFriends() {
        UsersClient usersApiClient = new UserApiClient();
        UserJson targetUser =
                new UserJson(
                        null,
                        "ferret55",
                        CurrencyValues.EUR,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        new TestData(
                                "12345",
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>(),
                                new ArrayList<>()
                        )

                );
        usersApiClient.addFriends(targetUser, 2);
    }
}



