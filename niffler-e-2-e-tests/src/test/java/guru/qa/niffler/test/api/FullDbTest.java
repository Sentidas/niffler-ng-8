package guru.qa.niffler.test.api;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.impl.UserApiClient;
import org.junit.jupiter.api.Order;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;

@Order(Integer.MAX_VALUE)
public class FullDbTest {

    UserApiClient usersClient = new UserApiClient();

    @User
    void getAllUsersShouldReturnEmptyResult(UserJson user) {

        List<UserJson> allUsers = usersClient.getAllUsers(user.username());
        assertFalse(allUsers.isEmpty());
    }
}
