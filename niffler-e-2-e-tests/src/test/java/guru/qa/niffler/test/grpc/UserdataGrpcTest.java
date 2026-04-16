package guru.qa.niffler.test.grpc;

import guru.qa.niffler.grpc.*;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.UserJson;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.util.List;

import static guru.qa.niffler.grpc.CurrencyValues.KZT;
import static guru.qa.niffler.grpc.CurrencyValues.RUB;
import static guru.qa.niffler.grpc.FriendshipStatus.*;
import static org.junit.jupiter.api.Assertions.*;

public class UserdataGrpcTest extends BaseGrpcTest {
    /*  rpc CurrentUser (UsernameRequest) returns (UserResponse) {}
        rpc AllUsers (UserSearchRequest) returns (UsersResponse) {}
        rpc AllUsersPage (UserPageRequest) returns (UserPageResponse) {}
        rpc UpdateUser (UserRequest) returns (UserResponse) {}
        rpc AllFriends (UserSearchRequest) returns (UsersResponse) {}
        rpc AllFriendsPage (UserPageRequest) returns (UserPageResponse) {}
        rpc RemoveFriend (FriendshipRequest) returns (google.protobuf.Empty) {}
        rpc SendInvitation (FriendshipRequest) returns (UserResponse) {}
        rpc AcceptInvitation (FriendshipRequest) returns (UserResponse) {}
        rpc DeclineInvitation (FriendshipRequest) returns (UserResponse) {}
        */

    @User
    void currentUser_shouldReturnExistingUserWithId(UserJson user) {
        final String username = user.username();

        final UsernameRequest request = UsernameRequest.newBuilder()
                .setUsername(username)
                .build();

        final UserResponse response = userdataBlockingStub.currentUser(request);
        assertAll(
                () -> assertEquals(username, response.getUsername(),
                        "Current user must match the requested username"),
                () -> assertFalse(response.getId().isEmpty(),
                        "Existing user must have a non-empty ID")
        );
    }

    @Test
    void currentUser_shouldReturnNonExistingUserWithoutId() {
        final String username = "__no_such_user__";
        final UsernameRequest request = UsernameRequest.newBuilder()
                .setUsername(username)
                .build();

        final UserResponse response = userdataBlockingStub.currentUser(request);

        assertAll(
                () -> assertEquals(username, response.getUsername(),
                        "Response must echo the requested username even for a non-existing user"),
                () -> assertTrue(response.getId().isEmpty(),
                        "Non-existing user must have an empty ID")
        );
    }

    @User
    void updateUser_shouldUpdateFullnameAndCurrency(UserJson user) {
        final String username = user.username();
        final String userId = user.id().toString();
        final String updateFullname = username + " fullname";

        // get current user
        final UsernameRequest currentUserRequest = UsernameRequest.newBuilder()
                .setUsername(username)
                .build();

        final UserResponse before = userdataBlockingStub.currentUser(currentUserRequest);

        // update user
        final UserRequest updateRequest = UserRequest.newBuilder()
                .setId(userId)
                .setUsername(username)
                .setFullname(updateFullname)
                .setCurrency(KZT)
                .build();

        final UserResponse after = userdataBlockingStub.updateUser(updateRequest);

        assertAll(
                // before
                () -> assertEquals(userId, before.getId(),
                        "Before update: ID must match"),
                () -> assertEquals(username, before.getUsername(),
                        "Before update: username must match"),
                () -> assertTrue(before.getFullname().isEmpty(),
                        "Before update: fullname must be empty"),
                () -> assertEquals(RUB, before.getCurrency(),
                        "Before update: default currency must be RUB"),
                // after
                () -> assertEquals(userId, after.getId(),
                        "After update: ID must remain the same"),
                () -> assertEquals(username, after.getUsername(),
                        "After update: username must remain the same"),
                () -> assertEquals(updateFullname, after.getFullname(),
                        "After update: fullname must be updated"),
                () -> assertEquals(KZT, after.getCurrency(),
                        "After update: currency must be updated to KZT")
        );
    }

    @User(outcomeInvitation = 3)
    void allUsers_shouldContainOutgoingInvitationsWithStatusInviteSent(UserJson user) {

        final List<String> outcomeInvitationUsers = user.testData().outcomeInvitationsUsernames();
        final List<String> statusInviteSendInAllUsers = findInviteSentUsernameInAllUsers(user.username());

        assertTrue(statusInviteSendInAllUsers.containsAll(outcomeInvitationUsers),
                "All outgoing invitation recipients must appear in AllUsers with INVITE_SENT status");
    }

    @User(friends = 5)
    void allFriendsPage_shouldPaginateAllFriendsConsistently(UserJson user) {
        final String username = user.username();
        final List<String> expectedFriends = user.testData().friendsUsernames().stream().sorted().toList();
        final int size = 3;
        final int page0 = 0, page1 = 1;
        final int expectedTotal = expectedFriends.size();
        final int expectedTotalPages = (int) Math.ceil(expectedTotal / (double) size);
        final int expectedSizePage0 = Math.min(size, Math.max(0, expectedTotal - page0 * size));
        final int expectedSizePage1 = Math.min(size, Math.max(0, expectedTotal - page1 * size));

        final UserPageRequest requestPage0 = UserPageRequest.newBuilder()
                .setUsername(username)
                .setPage(page0)
                .setSize(size)
                .build();

        final UserPageRequest requestPage1 = UserPageRequest.newBuilder()
                .setUsername(username)
                .setPage(page1)
                .setSize(size)
                .build();

        final UserPageResponse responsePage0 = userdataBlockingStub.allFriendsPage(requestPage0);
        final UserPageResponse responsePage1 = userdataBlockingStub.allFriendsPage(requestPage1);

        final List<String> actualFriends = mergeAndSortUsernameFromPages(responsePage0, responsePage1);

        assertAll(
                //  page 0 meta
                () -> assertEquals(expectedTotalPages, responsePage0.getTotalPages(),
                        "Page 0: totalPages is incorrect"),
                () -> assertEquals(expectedTotal, responsePage0.getTotalElements(),
                        "Page 0: totalElements is incorrect"),
                () -> assertEquals(expectedSizePage0, responsePage0.getContentList().size(),
                        "Page 0: page size (content count) is incorrect"),
                () -> assertTrue(responsePage0.getFirst(),
                        "Page 0: must be marked as first"),

                // page 1 meta
                () -> assertEquals(expectedTotalPages, responsePage1.getTotalPages(),
                        "Page 1: totalPages is incorrect"),
                () -> assertEquals(expectedTotal, responsePage1.getTotalElements(),
                        "Page 1: totalElements is incorrect"),
                () -> assertEquals(expectedSizePage1, responsePage1.getContentList().size(),
                        "Page 1: page size (content count) is incorrect"),
                () -> assertTrue(responsePage1.getLast(),
                        "Page 1: must be marked as last"),

                // combined results
                () -> assertEquals(expectedFriends, actualFriends,
                        "Combined usernames from page 0 and 1 must match expected sorted friends list"),
                () -> assertTrue(
                        java.util.stream.Stream.of(responsePage0, responsePage1)
                                .flatMap(p -> p.getContentList().stream())
                                .allMatch(u -> u.getFriendshipStatus() == FRIEND),
                        "All users returned by allFriendsPage must have FRIEND status")
        );
    }

    @User(usernameFriends = {
            @User.Friend(username = "unique_friend_for_grpc_test")
    })
    void allFriendsPage_shouldReturnSingleResultBySearchUsername(UserJson user) {
        final String username = user.username();
        final List<String> expectedFriends = user.testData().friendsUsernames().stream().sorted().toList();
        final int size = 3;
        final int page = 0;
        final int expectedTotal = expectedFriends.size();
        final String searchFriendName = "unique_friend_for_grpc_test";

        final UserPageRequest pageRequest = UserPageRequest.newBuilder()
                .setUsername(username)
                .setSearchQuery(searchFriendName)
                .setPage(page)
                .setSize(size)
                .build();

        final UserPageResponse responsePage = userdataBlockingStub.allFriendsPage(pageRequest);

        assertAll(
                // page 0 meta
                () -> assertEquals(1, responsePage.getTotalPages()),
                () -> assertEquals(expectedTotal, responsePage.getTotalElements()),
                () -> assertEquals(expectedTotal, responsePage.getContentList().size()),
                // results
                () -> assertEquals(searchFriendName, responsePage.getContentList().getFirst().getUsername()),
                () -> assertEquals(FRIEND, responsePage.getContentList().getFirst().getFriendshipStatus())
        );
    }

    @User(usernameFriends = {
            @User.Friend(username = "duck"),
            @User.Friend(username = "duck14"),
            @User.Friend(username = "DUCK"),
            @User.Friend(username = "Duck")
    })
    void allFriendsPage_shouldReturnMultipleResultsForCaseInsensitiveSearch(UserJson user) {
        final List<String> required = List.of("duck", "DUCK", "Duck", "duck14");

        for (String searchFriendName : List.of("duck", "DUCK", "Duck")) {
            final UserSearchRequest request = UserSearchRequest.newBuilder()
                    .setUsername(user.username())
                    .setSearchQuery(searchFriendName)
                    .build();

            final UsersResponse response = userdataBlockingStub.allFriends(request);
            final List<String> actualFriends = response.getUsersList().stream().map(UserResponse::getUsername).toList();

            assertAll(
                    () -> assertTrue(actualFriends.containsAll(required),
                            "Search '" + searchFriendName + "': response must contain all required variants"),
                    () -> assertTrue(actualFriends.size() >= required.size(),
                            "Search '" + searchFriendName + "': response must contain at least " + required.size() + " items"),
                    () -> assertTrue(actualFriends.stream().allMatch(n -> n.toLowerCase().contains("duck")),
                            "Search '" + searchFriendName + "': all usernames must include 'duck' case-insensitively")
            );
        }
    }

    @User(incomeInvitation = 1)
    void acceptIncomingInvitation_shouldMakeUserMutualFriend(UserJson user) {
        final String username = user.username();
        final String inviter = user.testData().incomeInvitationsUsernames().getFirst();

        // get friends before accept
        final List<String> friendsBeforeAccept = listFriends(username);

        // accept invitation
        final FriendshipRequest acceptRequest = FriendshipRequest.newBuilder()
                .setUsername(username)
                .setTargetUsername(inviter)
                .build();

        final UserResponse responseAccept = userdataBlockingStub.acceptInvitation(acceptRequest);

        // get friends after accept
        final List<String> friendsAfterAccept = listFriends(username);

        assertAll(
                //  accept invitation
                () -> assertEquals(inviter, responseAccept.getUsername(),
                        "Accept response: username must be inviter"),
                () -> assertEquals(FRIEND, responseAccept.getFriendshipStatus(),
                        "Accept response: status must be FRIEND"),

                // after accept
                () -> assertTrue(friendsAfterAccept.contains(inviter),
                        "After accept: inviter must appear in the recipient's friends list"),
                () -> assertEquals(friendsBeforeAccept.size() + 1, friendsAfterAccept.size(),
                        "After accept: friends count must increase by 1"),
                () -> assertTrue(listFriends(inviter).contains(username),
                        "After accept: recipient must appear in the inviter's friends list")
        );
    }

    @User(incomeInvitation = 1)
    void declineIncomingInvitation_shouldNotAddInviterToFriends(UserJson user) {
        final String username = user.username();
        final String inviterUsername = user.testData().incomeInvitationsUsernames().getFirst();

        final FriendshipRequest declineRequest = FriendshipRequest.newBuilder()
                .setUsername(username)
                .setTargetUsername(inviterUsername)
                .build();

        final UserResponse declineResponse = userdataBlockingStub.declineInvitation(declineRequest);

        final List<String> friendsAfterDecline = listFriends(username);

        assertAll(
                () -> assertEquals(inviterUsername, declineResponse.getUsername(),
                        "Decline response: username must be inviter"),
                () -> assertEquals(UNDEFINED, declineResponse.getFriendshipStatus(),
                        "Decline response: status must be UNDEFINED"),
                () -> assertFalse(friendsAfterDecline.contains(inviterUsername),
                        "After decline: inviter must NOT appear in the recipient's friends list")
        );
    }

    @User
    void sendInvitation_shouldCreateNewInvitation(UserJson user) {
        final String sender = user.username();
        final String target = findNonFriendsInAllUsers(sender).getFirst();

        final FriendshipRequest sendRequest = FriendshipRequest.newBuilder()
                .setUsername(sender)
                .setTargetUsername(target)
                .build();

        final UserResponse sendResponse = userdataBlockingStub.sendInvitation(sendRequest);

        final UserSearchRequest allUserRequest = UserSearchRequest.newBuilder()
                .setUsername(sender)
                .setSearchQuery(target)
                .build();

        final UsersResponse senderAllUsers = userdataBlockingStub.allUsers(allUserRequest);
        final List<String> recipientInviteReceived = findInviteReceivedInFriends(target);

        assertAll(
                () -> assertEquals(target, sendResponse.getUsername(),
                        "Send invitation: response username must equal target"),
                () -> assertEquals(INVITE_SENT, sendResponse.getFriendshipStatus(),
                        "Send invitation: response status must be INVITE_SENT"),

                () -> assertEquals(target, senderAllUsers.getUsersList().getFirst().getUsername(),
                        "AllUsers(sender): first item must be the target user"),
                () -> assertEquals(INVITE_SENT, senderAllUsers.getUsersList().getFirst().getFriendshipStatus(),
                        "AllUsers(sender): target user must have INVITE_SENT status"),

                () -> assertTrue(recipientInviteReceived.contains(user.username()),
                        "Friends(target): recipient must see sender with INVITE_RECEIVED status")
        );
    }

    @User(friends = 2)
    void removeFriend_shouldDecreaseFriendsCountByOneAndRemoveFriendship(UserJson user) {
        final String username = user.username();
        final String friendToRemove = user.testData().friendsUsernames().getFirst();

        final List<String> friendsBefore = listFriends(username);

        final FriendshipRequest removeRequest = FriendshipRequest.newBuilder()
                .setUsername(username)
                .setTargetUsername(friendToRemove)
                .build();

        userdataBlockingStub.removeFriend(removeRequest);

        final List<String> friendsAfter = listFriends(username);

        assertAll(
                () -> assertTrue(friendsBefore.contains(friendToRemove),
                        "Precondition: friend to remove must be present in the original friends list"),
                () -> assertFalse(friendsAfter.contains(friendToRemove),
                        "After removal: friend must not be present in the friends list"),
                () -> assertEquals(friendsBefore.size() - 1, friendsAfter.size(),
                        "After removal: friends count must decrease by exactly 1")
        );
    }

    @NotNull
    private static List<String> mergeAndSortUsernameFromPages(UserPageResponse responsePage1, UserPageResponse responsePage2) {
        return java.util.stream.Stream.concat(
                responsePage1.getContentList().stream().map(UserResponse::getUsername),
                responsePage2.getContentList().stream().map(UserResponse::getUsername)
        ).sorted().toList();
    }

    private List<String> listFriends(String username) {
        final UserSearchRequest request = UserSearchRequest.newBuilder()
                .setUsername(username)
                .build();

        final List<UserResponse> friends = userdataBlockingStub.allFriends(request).getUsersList();

        return friends.stream()
                .filter(u -> u.getFriendshipStatus() == FRIEND)
                .map(UserResponse::getUsername)
                .toList();
    }

    @NotNull
    private List<String> findInviteReceivedInFriends(String username) {
        final UserSearchRequest request = UserSearchRequest.newBuilder()
                .setUsername(username)
                .build();

        List<UserResponse> friends = userdataBlockingStub.allFriends(request).getUsersList();

        return friends.stream()
                .filter(u -> u.getFriendshipStatus() == INVITE_RECEIVED)
                .map(UserResponse::getUsername)
                .toList();
    }

    private List<String> findInviteSentUsernameInAllUsers(String username) {
        final UserSearchRequest request = UserSearchRequest.newBuilder()
                .setUsername(username)
                .build();

        List<UserResponse> friends = userdataBlockingStub.allUsers(request).getUsersList();

        return friends.stream()
                .filter(u -> u.getFriendshipStatus() == INVITE_SENT)
                .map(UserResponse::getUsername)
                .toList();
    }

    private List<String> findNonFriendsInAllUsers(String username) {
        final UserSearchRequest request = UserSearchRequest.newBuilder()
                .setUsername(username)
                .build();

        List<UserResponse> friends = userdataBlockingStub.allUsers(request).getUsersList();

        return friends.stream()
                .filter(u -> u.getFriendshipStatus() == UNDEFINED)
                .map(UserResponse::getUsername)
                .toList();
    }
}