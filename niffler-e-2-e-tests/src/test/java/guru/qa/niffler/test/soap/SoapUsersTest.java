package guru.qa.niffler.test.soap;

import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.SoapTest;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UserdataSoapClient;
import jaxb.userdata.UserResponse;
import jaxb.userdata.UsersResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static jaxb.userdata.FriendshipStatus.*;
import static org.junit.jupiter.api.Assertions.*;

@SoapTest
public class SoapUsersTest {

    private final UserdataSoapClient userdataSoapClient = new UserdataSoapClient();

    @User
    void getCurrentUserTest(UserJson user) {
        UserResponse response = userdataSoapClient.currentUser(user.username());

        assertEquals(
                user.username(),
                response.getUser().getUsername()
        );
    }

    @User(friends = 5)
    void getAllFriendsPageTest(UserJson user) {
        List<String> expectedFriends = user.testData().friendsUsernames().stream().sorted().toList();

        UsersResponse responsePage1 = userdataSoapClient.friendsPage(user.username(), 0, 3);
        UsersResponse responsePage2 = userdataSoapClient.friendsPage(user.username(), 1, 3);

        List<String> actualFriends = sortedFriendsNameFromPages(responsePage1, responsePage2);

        assertAll(
                // check page 1
                () -> assertEquals(2, responsePage1.getTotalPages()),
                () -> assertEquals(5, responsePage1.getTotalElements()),
                () -> assertEquals(3, responsePage1.getUser().size()),
                // check page 2
                () -> assertEquals(2, responsePage2.getTotalPages()),
                () -> assertEquals(5, responsePage2.getTotalElements()),
                () -> assertEquals(2, responsePage2.getUser().size()),
                // check all results on pages
                () -> assertEquals(expectedFriends, actualFriends)
        );
    }

    @User(usernameFriends = {
            @User.Friend(username = "unique_friend_for_soap_test")
    })
    void uniqueResultBySearchFriend(UserJson user) {
        String searchFriendName = "unique_friend_for_soap_test";


        List<jaxb.userdata.User> actualFriends = userdataSoapClient.friendsFilterByUsername(
                user.username(),
                searchFriendName).getUser();
        assertAll(
                () -> assertEquals(1, actualFriends.size()),
                () -> assertEquals(searchFriendName, actualFriends.getFirst().getUsername())
        );
    }


    @User(usernameFriends = {
            @User.Friend(username = "duck"),
            @User.Friend(username = "duck14"),
            @User.Friend(username = "DUCK"),
            @User.Friend(username = "Duck")
    })
    void multipleResultsBySearchFriend(UserJson user) {
        List<String> required = List.of("duck", "DUCK", "Duck", "duck14");

        for (String searchFriendName : List.of("duck", "DUCK", "Duck")) {
            List<String> actualFriends = userdataSoapClient.friendsFilterByUsername(
                            user.username(),
                            searchFriendName)
                    .getUser()
                    .stream()
                    .map(jaxb.userdata.User::getUsername)
                    .toList();
            assertAll(
                    () -> assertTrue(actualFriends.size() >= required.size()),
                    () -> assertTrue(actualFriends.containsAll(required)),
                    () -> assertTrue(actualFriends.stream().allMatch(n -> n.toLowerCase().contains("duck")))
            );
        }
    }

    @User(incomeInvitation = 1)
    void acceptIncomeInvitationTest(UserJson user) {
        String inviterUsername = user.testData().incomeInvitationsUsernames().getFirst();
        List<String> friendsBeforeAccept = getFriends(user.username());
        UserResponse responseAccept = userdataSoapClient.acceptIncomeInvitation(user.username(), inviterUsername);
        List<String> friendsAfterAccept = getFriends(user.username());

        assertAll(
                // check response accept invitation
                () -> assertEquals(inviterUsername, responseAccept.getUser().getUsername()),
                () -> assertEquals(FRIEND, responseAccept.getUser().getFriendshipStatus()),

                // check list friends before and after accept
                () -> assertTrue(friendsAfterAccept.contains(inviterUsername)),
                () -> assertEquals(friendsBeforeAccept.size() + 1, friendsAfterAccept.size())
        );
    }

    @User(incomeInvitation = 1)
    void declineIncomeInvitationTest(UserJson user) {
        String inviterUsername = user.testData().incomeInvitationsUsernames().getFirst();

        UserResponse responseDecline = userdataSoapClient.declineIncomeInvitation(user.username(), inviterUsername);
        List<String> friendsAfterAccept = getFriends(user.username());
        assertAll(
                // check response decline invitation
                () -> assertEquals(inviterUsername, responseDecline.getUser().getUsername()),
                () -> assertEquals(VOID, responseDecline.getUser().getFriendshipStatus()),

                // check list friends after accept
                () -> assertFalse(friendsAfterAccept.contains(inviterUsername))
        );
    }

    @User(incomeInvitation = 1)
    void sendInvitationTest(UserJson user) {
        String targetUsername = userdataSoapClient.allUsers(user.username()).getUser().getFirst().getUsername();

        UserResponse sendResponse = userdataSoapClient.sendInvitation(user.username(), targetUsername);

        UsersResponse senderLookup = userdataSoapClient.allUsers(user.username(), targetUsername);

        List<String> recipientInviteReceivedUsernames = getStatusInviteReceviedinFriendList(targetUsername);

        assertAll(
                // check response send invitation
                () -> assertEquals(targetUsername, sendResponse.getUser().getUsername()),
                () -> assertEquals(INVITE_SENT, sendResponse.getUser().getFriendshipStatus()),

                // check that sender has status INVITE_SENT in all users
                () -> assertEquals(INVITE_SENT, senderLookup.getUser().getFirst().getFriendshipStatus()),

                // check that recipient has sender in friends list with status INVITE_RECEIVED
                () -> assertTrue(  recipientInviteReceivedUsernames.contains(user.username()))
        );
    }

    @User
    void sendInvitation_self_shouldReturnSoapFault(UserJson user) {
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                userdataSoapClient.sendInvitation(user.username(), user.username())
        );

        assertTrue(
                ex.getMessage().contains("Can`t create friendship request for self user"),
                () -> "Unexpected error message: " + ex.getMessage()
        );
    }

    @User(friends = 1)
    void removeFriendTest(UserJson user) {
        String friendToRemove = user.testData().friendsUsernames().getFirst();
        List<String> friendsBeforeRemove = getFriends(user.username());
        userdataSoapClient.removeFriend(user.username(), friendToRemove);
        List<String> friendsAfterRemove = getFriends(user.username());
        assertAll(
                // check present friend before remove in list friends
                () -> assertTrue(friendsBeforeRemove.contains(friendToRemove)),

                // check list friends before and after remove
                () -> assertFalse(friendsAfterRemove.contains(friendToRemove)),
                () -> assertEquals(friendsBeforeRemove.size() - 1, friendsAfterRemove.size())

        );
    }

    @NotNull
    private List<String> getFriends(String username) {
        var resp = userdataSoapClient.friends(username);
        var users = resp.getUser();
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .filter(u -> u.getFriendshipStatus() == jaxb.userdata.FriendshipStatus.FRIEND)
                .map(jaxb.userdata.User::getUsername)
                .toList();
    }

    @NotNull
    private List<String> getStatusInviteReceviedinFriendList(String username) {
        var resp = userdataSoapClient.friends(username);
        var users = resp.getUser();
        if (users == null || users.isEmpty()) {
            return List.of();
        }
        return users.stream()
                .filter(u -> u.getFriendshipStatus() == INVITE_RECEIVED)
                .map(jaxb.userdata.User::getUsername)
                .toList();
    }

    @NotNull
    private static List<String> sortedFriendsNameFromPages(UsersResponse responsePage1, UsersResponse responsePage2) {
        return java.util.stream.Stream.concat(
                responsePage1.getUser().stream().map(jaxb.userdata.User::getUsername),
                responsePage2.getUser().stream().map(jaxb.userdata.User::getUsername)
        ).sorted().toList();
    }
}
