package guru.qa.niffler.test.web.fake;

import guru.qa.niffler.jupiter.extension.UsersClientExtension;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.FullUserJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;
import java.util.UUID;

@ExtendWith(UsersClientExtension.class)
public class UserTest {

    private UsersClient usersClient;

    @ValueSource(strings = {
            "duck-11",
            "duck-22",
    })
    @ParameterizedTest
    void createUser(String username) throws InterruptedException {

        UserJson user = usersClient.createUser(
                username, "12345"
        );
    }

    @Test
    void removeUser() {
        usersClient.removeUser("duck-100");
    }

    @Test
    void updateUser() {
        UserJson updatedUser =
                new UserJson(
                        null,
                        "duck-200",
                        CurrencyValues.USD,
                        "murk",
                        null,
                        "angry catty",
                        null,
                        null,
                        null, null
                );
        usersClient.updateUser("duck-200", updatedUser);
    }

    @Test
    void findFullUserById() {
        UUID userId = UUID.fromString("eab13506-5ca7-11f0-acb5-0242ac110002");
        Optional<FullUserJson> user = usersClient.findUserByIdWithAuth(userId);

        System.out.println("FullUser найден: '" + user.get().username() + "' c ролями: " + String.join(", ", user.get().authorities()));
        System.out.println("FullUser найден: '" + user.get().username() + "' c именами: " + user.get().firstname() + ", " + user.get().surname() + ", " + user.get().fullname());
        System.out.println("FullUser найден: '" + user.get().username() + "' c основной валютой: " + user.get().currency());
    }

    @Test
    void findFullUserByUserName() {
        Optional<FullUserJson> user = usersClient.findFullUserByUsername("duck-200");

        if (user.isEmpty()) {
            System.out.println("Пользователь 'fox' не найден в базе.");
            return;
        }

        FullUserJson u = user.get();

        System.out.println("FullUser найден: '" + u.username() + "' c ролями: " + String.join(", ", u.authorities()));
        System.out.println("FullUser найден: '" + u.username() + "' c именами: " + u.firstname() + ", " + u.surname() + ", " + u.fullname());
        System.out.println("FullUser найден: '" + u.username() + "' c основной валютой: " + u.currency());
    }

    @Test
    void createOutcomeInvitations() {
        UserJson targetUser =
                new UserJson(
                        null,
                        "duck-200",
                        CurrencyValues.EUR,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null, null
                );
        usersClient.createOutcomeInvitations(targetUser, 2);
    }

    @Test
    void createIncomeInvitations() {
        UserJson targetUser =
                new UserJson(
                        null,
                        "duck-200",
                        CurrencyValues.EUR,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null, null
                );
        usersClient.createIncomeInvitations(targetUser, 1);
    }

    @Test
    void addFriends() {
        UserJson targetUser =
                new UserJson(
                        null,
                        "duck-200",
                        CurrencyValues.EUR,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null, null

                );
        usersClient.addFriends(targetUser, 2);
    }
}



