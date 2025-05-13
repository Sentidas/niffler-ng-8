package guru.qa.niffler.test.web.JDBC;

import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

public class FriendTest {


    // JDBC
    @Test
    void addOutcomeInvitationRepository() {
        UsersDbClient usersDbClient = new UsersDbClient();
        usersDbClient.addOutcomeInvitationRepositoryJdbc("duck", "fox");
    }

    @Test
    void addFriendRepository() {
        UsersDbClient usersDbClient = new UsersDbClient();
        usersDbClient.addFriendRepositoryJdbc("duck", "giraffe");
    }

    @Test
    void addIncomeInvitationRepository() {
        UsersDbClient usersDbClient = new UsersDbClient();
        usersDbClient.addOutcomeInvitationRepositoryJdbc("fox", "duck");
    }



    // SPRING JDBC


    @Test
    void addFriendRepositorySpring() {
        UsersDbClient usersDbClient = new UsersDbClient();
        usersDbClient.addFriendRepositorySpring("duck", "gorilla");
    }
}
