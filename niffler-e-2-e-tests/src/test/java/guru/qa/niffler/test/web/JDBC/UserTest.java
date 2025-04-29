package guru.qa.niffler.test.web.JDBC;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.auth.AuthorityJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserTest {

    // SPRING JDBC TESTS

    @Test
    void createUserWithSpring() {

        UsersDbClient us = new UsersDbClient();


        UserJson userJson = us.createUserSpringJdbc(
                new UserJson(
                        null,
                        "mark",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null

                ));

        System.out.println(userJson);
    }


    @Test
    void findAllUsersInUserDataSpringJDBC() {

        UsersDbClient usersDbClient = new UsersDbClient();
        List<UserJson> users = usersDbClient.findAllUdUsersSpringJdbc();
        System.out.println("Общий список пользователей из Userdata: ");
        for (UserJson user : users) {
            System.out.println(user.username());
        }
    }

    @Test
    void findAllUsersInAuthSpringJDBC() {

        UsersDbClient usersDbClient = new UsersDbClient();
        List<AuthUserJson> users = usersDbClient.findAllAuthUsersSpringJdbc();
        System.out.println("Общий список пользователей из Auth: ");
        for (AuthUserJson user : users) {
            System.out.println(user.username());
        }
    }

    @Test
    void findAllAuthoritiesInAuthSpringJDBC() {

        UsersDbClient usersDbClient = new UsersDbClient();
        List<AuthorityJson> authorities = usersDbClient.findAllAuthoritiesSpringJdbc();
        System.out.println("Общий список id всех прав всех пользователей из Auth: ");
        for (AuthorityJson authority : authorities) {
            System.out.println(authority.id());
        }
    }


    // JDBC TESTS

    @Test
    void createUserInUserdata() {

        UsersDbClient us = new UsersDbClient();

        UserJson userJson =
                new UserJson(
                        null,
                        "murka",
                        CurrencyValues.USD,
                        null,
                        null,
                        null,
                        null,
                        null

                );
        UserJson user = us.createUser(userJson);

        System.out.println(user);
    }

    @Disabled
    @Test
    void createUserInAuth() {
        UsersDbClient authDbClient = new UsersDbClient();

        AuthUserJson user = authDbClient.createUser(
                new AuthUserJson(
                        null,
                        "alexa",
                        "12345",
                        true,
                        true,
                        true,
                        true
                )
        );
        System.out.println(user);
    }


    @Test
    void findUserByIdInUserdata() {
        UUID userId = UUID.fromString("a18b5ba5-1f73-4164-87f7-ebfbe8dc4585");

        UsersDbClient us = new UsersDbClient();
        Optional<UserJson> user = us.findUserById(userId);

        System.out.println("User найден: " + user.get().username());
    }

    @Test
    void findUserByUsernameInUserData() {

        UsersDbClient us = new UsersDbClient();
        Optional<UserJson> user = us.findUserByUsername("duck");

        System.out.println("User найден: " + user.get().id());
        // a18b5ba5-1f73-4164-87f7-ebfbe8dc4585
    }

    @Test
    void deleteUserInUserdata() {
        UUID userId = UUID.fromString("df82e1fe-24c9-11f0-877e-0242ac110004");

        UsersDbClient us = new UsersDbClient();
        us.deleteUser(userId);
    }

    @Test
    void findAllUsersInUserData() {

        UsersDbClient usersDbClient = new UsersDbClient();
        List<UserJson> users = usersDbClient.findAllUdUsers();
        System.out.println("Общий список пользователей из Userdata: ");
        for (UserJson user : users) {
            System.out.println(user.username());
        }
    }

    @Test
    void findAllUsersInAuth() {

        UsersDbClient usersDbClient = new UsersDbClient();
        List<AuthUserJson> users = usersDbClient.findAllAuthUsers();
        System.out.println("Общий список пользователей из Auth: ");
        for (AuthUserJson user : users) {
            System.out.println(user.username());
        }
    }

    @Test
    void findAllAuthoritiesInAuth() {

        UsersDbClient usersDbClient = new UsersDbClient();
        List<AuthorityJson> authorities = usersDbClient.findAllAuthorities();
        System.out.println("Общий список id всех прав всех пользователей из Auth: ");
        for (AuthorityJson authority : authorities) {
            System.out.println(authority.id());
        }
    }
}



