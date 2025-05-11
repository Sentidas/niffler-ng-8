package guru.qa.niffler.test.web.JDBC;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.auth.AuthorityJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersDbClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserTest {

    @Test
    void createUserSpringTx() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson user = usersDbClient.createUserSpringTx(
                new UserJson(
                        null,
                        "duck-1",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserSpringTxChained() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson user = usersDbClient.createUserSpringTxChained(
                new UserJson(
                        null,
                        "duck-2",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
    }

    @Test
    void createUserSpring() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson user = usersDbClient.createUserSpring(
                new UserJson(
                        null,
                        "duck-3",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
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

    @Test
    void deleteUserSpring() {
        UsersDbClient usersDbClient = new UsersDbClient();
        usersDbClient.deleteUserSpringTx("duck-4");
    }

    // JDBC TESTS
     @Test
     void createUserJDBCTx() {
         UsersDbClient usersDbClient = new UsersDbClient();
         UserJson user = usersDbClient.createUserJDBCTx(
                 new UserJson(
                         null,
                         "duck-4",
                         CurrencyValues.RUB,
                         null,
                         null,
                         null,
                         null,
                         null
                 )
         );
         System.out.println(user);
     }


    @Test
    void createUserJDBC() {
        UsersDbClient usersDbClient = new UsersDbClient();
        UserJson user = usersDbClient.createUserJDBC(
                new UserJson(
                        null,
                        "duck-5",
                        CurrencyValues.RUB,
                        null,
                        null,
                        null,
                        null,
                        null
                )
        );
        System.out.println(user);
    }


    @Test
    void deleteUserJdbc() {
        UsersDbClient usersDbClient = new UsersDbClient();
        usersDbClient.deleteUserJDBCTx("duck-3");
    }

    @Test
    void findUserByIdInUserdata() {
        UUID userId = UUID.fromString("78b327d0-78ed-4915-8df5-461d8a24a458");

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



