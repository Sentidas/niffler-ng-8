package guru.qa.niffler.service;

import guru.qa.niffler.api.UserdataSoapApi;
import guru.qa.niffler.api.core.converter.SoapConverterFactory;
import guru.qa.niffler.config.Config;
import io.qameta.allure.Step;
import jaxb.userdata.*;
import okhttp3.logging.HttpLoggingInterceptor;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class UserdataSoapClient extends RestClient {

    private static final Config CFG = Config.getInstance();
    private final UserdataSoapApi userdataSoapApi;

    public UserdataSoapClient() {
        super(CFG.userdataUrl(),
                false,
                SoapConverterFactory.create("niffler-userdata"),
                HttpLoggingInterceptor.Level.BODY);
        this.userdataSoapApi = create(UserdataSoapApi.class);
    }

    @Nonnull
    @Step("Get current user '{username}' via SOAP")
    public UserResponse currentUser(String username) {
        CurrentUserRequest request = new CurrentUserRequest();
        request.setUsername(username);
        return execute(userdataSoapApi.currentUser(request));
    }

    @Nonnull
    @Step("Get all users for '{username}' via SOAP")
    public UsersResponse allUsers(String username) {
        AllUsersRequest request = new AllUsersRequest();
        request.setUsername(username);
        return execute(userdataSoapApi.allUsers(request));
    }

    @Nonnull
    @Step("Search users for '{username}' with query '{searchUsername}' via SOAP")
    public UsersResponse allUsers(String username, String searchUsername) {
        AllUsersRequest request = new AllUsersRequest();
        request.setUsername(username);
        request.setSearchQuery(searchUsername);
        return execute(userdataSoapApi.allUsers(request));
    }

    @Nonnull
    @Step("Get friends for '{username}' via SOAP")
    public UsersResponse friends(String username) {
        FriendsRequest request = new FriendsRequest();
        request.setUsername(username);
        return execute(userdataSoapApi.friends(request));
    }

    @Nonnull
    @Step("Get friends page {page}/{size} for '{username}' via SOAP")
    public UsersResponse friendsPage(String username, int page, int size) {
        FriendsPageRequest request = new FriendsPageRequest();
        request.setUsername(username);
        PageInfo pageInfo = new PageInfo();
        pageInfo.setPage(page);
        pageInfo.setSize(size);
        request.setPageInfo(pageInfo);
        return execute(userdataSoapApi.friendsPageable(request));
    }

    @Nonnull
    @Step("Search friends for '{username}' with query '{search}' via SOAP")
    public UsersResponse friendsFilterByUsername(String username, String search) {
        FriendsRequest request = new FriendsRequest();
        request.setUsername(username);
        request.setSearchQuery(search);
        return execute(userdataSoapApi.friends(request));
    }

    @Nonnull
    @Step("Accept incoming invitation for '{username}' from '{friendToBeAddedUsername}' via SOAP")
    public UserResponse acceptIncomeInvitation(String username, String friendToBeAddedUsername) {
        AcceptInvitationRequest request = new AcceptInvitationRequest();
        request.setUsername(username);
        request.setFriendToBeAdded(friendToBeAddedUsername);
        return execute(userdataSoapApi.acceptInvitation(request));
    }

    @Nonnull
    @Step("Decline incoming invitation for '{username}' from '{friendToBeDeclinedUsername}' via SOAP")
    public UserResponse declineIncomeInvitation(String username, String friendToBeDeclinedUsername) {
        DeclineInvitationRequest request = new DeclineInvitationRequest();
        request.setUsername(username);
        request.setInvitationToBeDeclined(friendToBeDeclinedUsername);
        return execute(userdataSoapApi.declineInvitation(request));
    }

    @Nonnull
    @Step("Send invitation from '{username}' to '{targetName}' via SOAP")
    public UserResponse sendInvitation(String username, String targetName) {
        SendInvitationRequest request = new SendInvitationRequest();
        request.setUsername(username);
        request.setFriendToBeRequested(targetName);
        return execute(userdataSoapApi.sendInvitation(request));
    }

    @Step("Remove friend '{friendToRemove}' for '{username}' via SOAP")
    public void removeFriend(String username, String friendToRemove) {
        RemoveFriendRequest request = new RemoveFriendRequest();
        request.setUsername(username);
        request.setFriendToBeRemoved(friendToRemove);
        executeWithoutBody(userdataSoapApi.removeFriend(request));
    }
}