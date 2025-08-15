package guru.qa.niffler.service.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.niffler.api.GetawayApi;
import guru.qa.niffler.model.friend.FriendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.model.userdata.UserJsonError;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class GatewayApiClient extends RestClient {

    private final GetawayApi getawayApi;

    public GatewayApiClient() {
        super(CFG.gatewayUrl());
        getawayApi = retrofit.create(GetawayApi.class);
    }

    @Step("Get all users using /api/users/all")
    @Nonnull
    public List<UserJson> allUsers(String bearerToken, @Nullable String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = getawayApi.allUsers("Bearer " + bearerToken, searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }


    @Step("Get all friends and income invitation using /api/friends/all")
    @Nonnull
    public List<UserJson> allFriends(String bearerToken, @Nullable String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = getawayApi.allFriends("Bearer " + bearerToken, searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Remove friend using 'DELETE /api/friends/remove'")
    public void removeFriend(String bearerToken, String username) {
        final Response<Void> response;
        try {
            response = getawayApi.removeFriend("Bearer " + bearerToken, username).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
    }

    @Step("Send invitation to user using 'POST /api/invitations/send'")
    public UserJson sendInvitation(String bearerToken, String toUsername) {
        FriendJson friendJson = new FriendJson(toUsername);
        final Response<UserJson> response;
        try {
            response = getawayApi.sendInvitation("Bearer " + bearerToken, friendJson).execute();
        } catch (IOException e) {
            throw new RuntimeException("Failed to send invitation", e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Send invitation to user using 'POST /api/invitations/send' with error statusCode '{2}'")
    public UserJsonError sendInvitationError(String bearerToken, String toUsername, int expectedStatus) {
        FriendJson friendJson = new FriendJson(toUsername);

        try {
            Response<UserJson> response = getawayApi.sendInvitation(
                            "Bearer " + bearerToken,
                            friendJson)
                    .execute();

            assertEquals(expectedStatus, response.code());

            if (response.isSuccessful()) {
                throw new AssertionError("Unexpected response code:" + response.code());
            }
            assert response.errorBody() != null;
            String errorBody = response.errorBody().string();

            return new ObjectMapper().readValue(errorBody, UserJsonError.class);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Step("Accept invitation from user using 'POST /api/invitations/accept'")
    public UserJson acceptInvitation(String bearerToken, String fromUsername) {
        FriendJson friendJson = new FriendJson(fromUsername);

        final Response<UserJson> response;
        try {
            response = getawayApi.acceptInvitation("Bearer " + bearerToken, friendJson).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }

    @Step("Decline invitation from user using 'POST /api/invitations/decline'")
    public UserJson declineInvitation(String bearerToken, String fromUsername) {
        FriendJson friendJson = new FriendJson(fromUsername);

        final Response<UserJson> response;
        try {
            response = getawayApi.declineInvitation("Bearer " + bearerToken, friendJson).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }
}
