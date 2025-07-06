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
                        "duck-500",
                        CurrencyValues.RUB,
                        "murk",
                        null,
                        "angry catty",
                        null,
                        null,
                        null
                );
        usersDbClient.updateUser("duck-200", updatedUser);
    }

    @Test
    void findFullUserById() {
        UsersDbClient us = new UsersDbClient();
        UUID userId = UUID.fromString("c98a52fe-cbdc-4dd9-a379-01025cf2c59d");
        Optional<FullUserJson> user = us.findFullUserByById(userId);

        System.out.println("FullUser найден: '" + user.get().username() + "' c ролями: " + String.join(", ", user.get().authorities()));
        System.out.println("FullUser найден: '" + user.get().username() + "' c именами: " + user.get().firstname() + ", " + user.get().surname() + ", " + user.get().fullname());
        System.out.println("FullUser найден: '" + user.get().username() + "' c основной валютой: " + user.get().currency());
    }

    @Test
    void findFullUserByUserName() {
        UsersDbClient us = new UsersDbClient();
        Optional<FullUserJson> user = us.findFullUserByUsername("fox");

        if (user.isEmpty()) {
            System.out.println("Пользователь 'fox' не найден в базе.");
            return;
        }

        FullUserJson u = user.get(); // теперь безопасно

        System.out.println("FullUser найден: '" + u.username() + "' c ролями: " + String.join(", ", u.authorities()));
        System.out.println("FullUser найден: '" + u.username() + "' c именами: " + u.firstname() + ", " + u.surname() + ", " + u.fullname());
        System.out.println("FullUser найден: '" + u.username() + "' c основной валютой: " + u.currency());
    }

    @Test
    void createOutcomeInvitations() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson targetUser =
                new UserJson(
                        null,
                        "panda",
                        CurrencyValues.EUR,
                        null,
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
                        "panda",
                        CurrencyValues.EUR,
                        null,
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
                        "horse",
                        CurrencyValues.EUR,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null

                );
        usersDbClient.addFriends(targetUser, 2);
    }
}



