package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.service.gateway.GatewayApiClient;
import guru.qa.niffler.service.impl.UsersApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.List;

import static guru.qa.niffler.model.userdata.FriendshipStatus.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RestTest
public class FriendsRestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();
    UsersClient usersApi = new UsersApiClient();

    @User(friends = 2)
    @ApiLogin
    void removeFriend_shouldRemoveFriendFromFriendsListAndUpdateStatus(UserJson userJson, @Token String bearerToken) throws InterruptedException {

        final String friendName = userJson.testData().friendsUsernames().getFirst();

        final List<UserJson> checkAbsentFriendBeforeRemoval = gatewayApiClient.allUsers(
                bearerToken,
                friendName
        );
        assertEquals(0, checkAbsentFriendBeforeRemoval.size());


        final List<UserJson> checkPresentFriendBeforeRemoval = gatewayApiClient.allFriends(
                bearerToken,
                friendName
        );
        assertEquals(1, checkPresentFriendBeforeRemoval.size());
        assertEquals(FRIEND, checkPresentFriendBeforeRemoval.getFirst().friendshipStatus());

        gatewayApiClient.removeFriend(
                bearerToken,
                friendName
        );

        final List<UserJson> checkAbsentFriendAfterRemoval = gatewayApiClient.allUsers(bearerToken, friendName);
        assertEquals(1, checkAbsentFriendAfterRemoval.size());
        assertNull(checkAbsentFriendAfterRemoval.getFirst().friendshipStatus());

        final List<UserJson> checkPresentFriendAfterRemoval = gatewayApiClient.allFriends(
                bearerToken,
                friendName
        );
        assertEquals(0, checkPresentFriendAfterRemoval.size());
    }

    @User
    @ApiLogin
    void sendInvitation_shouldReturnSuccessWhenInvitationSent(@Token String bearerToken) throws InterruptedException {
        final String friendName = RandomDataUtils.randomUsername();
        usersApi.createUser(friendName, "12345");

        final UserJson responseSendInvitation = gatewayApiClient.sendInvitation(
                bearerToken,
                friendName
        );
        assertEquals(friendName, responseSendInvitation.username());
        assertEquals(INVITE_SENT, responseSendInvitation.friendshipStatus());
    }


    @User(incomeInvitation = 1)
    @ApiLogin
    void acceptInvitation_shouldAddToFriendsListWhenAccepted(UserJson user, @Token String bearerToken) {

        final String friendName = user.testData().incomeInvitations().getFirst().username();

        final UserJson responseAcceptInvitation = gatewayApiClient.acceptInvitation(
                bearerToken,
                friendName
        );
        assertEquals(friendName, responseAcceptInvitation.username());
        assertEquals(FRIEND, responseAcceptInvitation.friendshipStatus());

        final List<UserJson> checkFriendAfterAccept = gatewayApiClient.allFriends(
                bearerToken,
                friendName
        );
        assertEquals(1, checkFriendAfterAccept.size());
        assertEquals(friendName, checkFriendAfterAccept.getFirst().username());
        assertEquals(FRIEND, checkFriendAfterAccept.getFirst().friendshipStatus());
    }

    @User(incomeInvitation = 1)
    @ApiLogin
    void declineInvitation_shouldNotAddToFriendsListWhenDeclined(UserJson user, @Token String bearerToken) {
        final String friendName = user.testData().incomeInvitations().getFirst().username();

        final UserJson responseDeclineInvitation = gatewayApiClient.declineInvitation(
                bearerToken,
                friendName
        );
        assertEquals(friendName, responseDeclineInvitation.username());
        assertNull(responseDeclineInvitation.friendshipStatus());


        final List<UserJson> checkFriendAfterDecline = gatewayApiClient.allFriends(
                bearerToken,
                friendName
        );
        assertEquals(0, checkFriendAfterDecline.size());
    }

    @User
    @ApiLogin
    void sendInvitation_shouldCreateIncomeAndOutcomeInvitations(UserJson user, @Token String bearerToken) throws InterruptedException {
        final String friendName = RandomDataUtils.randomUsername();
        usersApi.createUser(friendName, "12345");

        gatewayApiClient.sendInvitation(bearerToken, friendName);

        final List<UserJson> incomeInvitations = usersApi.getIncomeInvitation(
                friendName
        );
        assertEquals(INVITE_RECEIVED, incomeInvitations.getFirst().friendshipStatus());
        assertEquals(user.username(), incomeInvitations.getFirst().username());

        final List<UserJson> outcomeInvitations = usersApi.getOutcomeInvitation(
                user.username()
        );
        assertEquals(INVITE_SENT, outcomeInvitations.getFirst().friendshipStatus());
        assertEquals(friendName, outcomeInvitations.getFirst().username());
    }


    @User(friends = 1, incomeInvitation = 2)
    @ApiLogin
    void allFriends_shouldReturnFriendsAndIncomeInvitations(@Token String bearerToken) {
        final List<UserJson> responseAllFriends = gatewayApiClient.allFriends(
                bearerToken,
                null
        );
        assertEquals(3, responseAllFriends.size());
    }
}
