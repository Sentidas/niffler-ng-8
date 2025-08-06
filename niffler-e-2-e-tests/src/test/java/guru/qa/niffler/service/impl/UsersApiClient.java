package guru.qa.niffler.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.base.Stopwatch;
import guru.qa.niffler.api.AuthApi;
import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.userdata.FullUserJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.RestClient;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static guru.qa.niffler.utils.OauthUtils.generateCodeChallenge;
import static guru.qa.niffler.utils.OauthUtils.generateCodeVerifier;

@ParametersAreNonnullByDefault
public class UsersApiClient extends BaseApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final String defaultPassword = "12345";

    private final AuthApi authApi = new RestClient.DefaultRestClient(CFG.authUrl(), true).create(AuthApi.class);
    private final UserdataApi userdataApi = new RestClient.DefaultRestClient(CFG.userdataUrl()).create(UserdataApi.class);


    @Nonnull
    @Override
    @Step("Create user with username '{0}' using API")
    public UserJson createUser(String username, String password) throws InterruptedException {

        executeWithoutBody(authApi.requestRegisterForm());
        executeWithoutBody(authApi.register(username,
                password,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")));

        long maxWaitTime = 5000L;
        Stopwatch sw = Stopwatch.createStarted();

        while (sw.elapsed(TimeUnit.MILLISECONDS) < maxWaitTime) {
            UserJson userJson = execute(userdataApi.currentUser(username));
            if (userJson.id() != null) {
                return userJson;
            } else Thread.sleep(100);
        }
        // Если пользователь не был создан в течение maxWaitTime
        throw new RuntimeException("Failed to create user with username: " + username + " within " + maxWaitTime + " ms");
    }

    @Nonnull
    @Step("Login user with username '{0}' using API")
    public String login(String username, String password) throws IOException {

        String codeVerifier = generateCodeVerifier();
        String codeChallenge = generateCodeChallenge(codeVerifier);


        Response<Void> responseAuth = authApi.authorize(
                "code",
                "client",
                "openid",
                "http://127.0.0.1:3000/authorized",
                codeChallenge,
                "S256"
        ).execute();


        Response<Void> responseLogin = authApi.login(
                username,
                password,
                ThreadSafeCookieStore.INSTANCE.cookieValue("XSRF-TOKEN")
        ).execute();

        String finalUrlResponseLogin = responseLogin.raw().request().url().toString();

        String authCode = finalUrlResponseLogin.split("code=")[1];
        System.out.println("code -> " + authCode);

        Response<JsonNode> tokenResponse = authApi.token(
                authCode,
                "http://127.0.0.1:3000/authorized",
                codeVerifier,
                "authorization_code",
                "client").execute();

        return tokenResponse.body().get("id_token").asText();

    }

    @Override
    @Step("Update user using API")


    public UserJson updateUser(String username, UserJson user) {
        return execute(userdataApi.updateUserInfo(user));
    }

    @Override
    public void removeUser(String username) {
        throw new RuntimeException("NYI method removeUser");

    }

    @Override
    @Nonnull
    @Step("Create {1} income invitation using API")
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
        List<UserJson> incomeInvitations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserJson addressee = new UsersDbClient().createUser(RandomDataUtils.randomUsername(), defaultPassword);
            execute(userdataApi.sendInvitation(addressee.username(), targetUser.username()));
            incomeInvitations.add(addressee);
        }
        targetUser.testData().incomeInvitations().addAll(incomeInvitations);
        return incomeInvitations;
    }

    @Override
    @Nonnull
    @Step("Create {1} outcome invitation using API")
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        List<UserJson> outcomeInvitations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserJson addressee = new UsersDbClient().createUser(RandomDataUtils.randomUsername(), defaultPassword);
            execute(userdataApi.sendInvitation(targetUser.username(), addressee.username()));
            outcomeInvitations.add(addressee);
        }
        targetUser.testData().outcomeInvitations().addAll(outcomeInvitations);
        return outcomeInvitations;
    }

    @Override
    @Nonnull
    @Step("Add {1} friends using API")
    public List<UserJson> addFriends(UserJson targetUser, int count) {
        List<UserJson> friends = new ArrayList<>();
        UserJson addressee;
        for (int i = 0; i < count; i++) {
            addressee = new UsersDbClient().createUser(RandomDataUtils.randomUsername(), defaultPassword);
            execute(userdataApi.sendInvitation(targetUser.username(), addressee.username()));
            execute(userdataApi.acceptInvitation(addressee.username(), targetUser.username()));
            friends.add(addressee);
            addressee.testData().friends().add(targetUser);
        }
        targetUser.testData().friends().addAll(friends);

        return friends;
    }

    @Override
    @Nonnull
    @Step("Get user '{0}' using API")
    public Optional<UserJson> findUserByUsername(String username) {
        return Optional.ofNullable(execute(userdataApi.currentUser(username)));
    }

    @Override
    public Optional<FullUserJson> findUserByIdWithAuth(UUID userId) {
        throw new RuntimeException("NYI method findUserByIdWithAuth");
    }

    @Override
    public Optional<FullUserJson> findFullUserByUsername(String username) {
        throw new RuntimeException("NYI method findFullUserByUsername");
    }
}
