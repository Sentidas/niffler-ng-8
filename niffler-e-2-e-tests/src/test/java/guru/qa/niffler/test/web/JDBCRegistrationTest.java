package guru.qa.niffler.test.web;

import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;

import guru.qa.niffler.service.service.UsersDbClient;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class JDBCRegistrationTest {

    @Disabled
    @Test
    void createUser1() {
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
    @Disabled
    @Test
    void createUser() {

        UsersDbClient us = new UsersDbClient();

        UserJson userJson =
                new UserJson(
                        null,
                        "mouse",
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
    @Test
    void createUserWithSpring() {

        UsersDbClient us = new UsersDbClient();


        UserJson userJson = us.createUserSpringJdbc(
                new UserJson(
                        null,
                        "maximusic-1",
                        CurrencyValues.USD,
                        null,
                        null,
                        null,
                        null,
                        null

                ));
        UserJson user = us.createUserSpringJdbc(userJson);

        System.out.println(user);
    }
}



