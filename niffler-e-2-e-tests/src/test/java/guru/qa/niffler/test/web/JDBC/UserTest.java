package guru.qa.niffler.test.web.JDBC;

import guru.qa.niffler.model.FullUserJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.UsersDbClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.UUID;

public class UserTest {

    static UsersDbClient usersDbClient = new UsersDbClient();

    @ValueSource(strings = {
            "duck-100",
            "duck-200",
    })
    @ParameterizedTest
    void createUser(String username) {

        UserJson user = usersDbClient.createUser(
                username, "12345"
        );
    }

    @Test
    void removeUser() {
        UsersDbClient usersDbClient = new UsersDbClient();
        usersDbClient.removeUser("duck-100");
    }

    @Test
    void updateUser() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson updatedUser =
                new UserJson(
                        null,
                        "cat",
                        CurrencyValues.RUB,
                        "murk",
                        null,
                        "angry catty",
                        null,
                        null
                );
        usersDbClient.updateUser("catty", updatedUser);
    }

    @Test
    void findFullUserById() {
        UsersDbClient us = new UsersDbClient();
        UUID userId = UUID.fromString("c193778e-3fd2-48c4-9a54-90736987ea89");
        Optional<FullUserJson> user = us.findFullUserByById(userId);

        System.out.println("FullUser найден: '" + user.get().username() + "' c ролями: " + String.join(", ", user.get().authorities()));
        System.out.println("FullUser найден: '" + user.get().username() + "' c именами: " + user.get().firstname() + ", " + user.get().surname() + ", " + user.get().fullname());
        System.out.println("FullUser найден: '" + user.get().username() + "' c основной валютой: " + user.get().currency());
    }

    @Test
    void findFullUserByUserName() {
        UsersDbClient us = new UsersDbClient();
        Optional<FullUserJson> user = us.findFullUserByUsername("fox");

        System.out.println("FullUser найден: '" + user.get().username() + "' c ролями: " + String.join(", ", user.get().authorities()));
        System.out.println("FullUser найден: '" + user.get().username() + "' c именами: " + user.get().firstname() + ", " + user.get().surname() + ", " + user.get().fullname());
        System.out.println("FullUser найден: '" + user.get().username() + "' c основной валютой: " + user.get().currency());
    }

    @Test
    void createOutcomeInvitations() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson targetUser =
                new UserJson(
                        null,
                        "fox",
                        CurrencyValues.EUR,
                        null,
                        null,
                        null,
                        null,
                        null
                );
        usersDbClient.createOutcomeInvitations(targetUser, 2);
    }

@Test
    void createIncomeInvitations() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson targetUser =
                new UserJson(
                        null,
                        "fox",
                        CurrencyValues.EUR,
                        null,
                        null,
                        null,
                        null,
                        null
                );
        usersDbClient.createIncomeInvitations(targetUser, 1);
    }

    @Test
    void addFriends() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson targetUser =
                new UserJson(
                        null,
                        "fox",
                        CurrencyValues.EUR,
                        null,
                        null,
                        null,
                        null,
                        null
                );
        usersDbClient.addFriends(targetUser, 2);
    }
}



