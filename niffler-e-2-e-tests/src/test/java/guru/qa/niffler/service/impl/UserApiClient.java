package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.UserdataApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.userdata.FullUserJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class UserApiClient extends BaseApiClient implements UsersClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .addNetworkInterceptor(new AllureOkHttp3()
                    .setRequestTemplate("http-request.ftl")
                    .setResponseTemplate("http-response.ftl"))
            .build();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.userdataUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final UserdataApi userApi = retrofit.create(UserdataApi.class);
    private static final String defaultPassword = "12345";


    @Nonnull
    @Override
    @Step("Create user using API")
    public UserJson createUser(String username, String password) {
        throw new RuntimeException("NYI method createUser");
    }

    @Override
    public UserJson updateUser(String username, UserJson user) {
        return execute(userApi.updateUserInfo(user));
    }

    @Override
    public void removeUser(String username) {
        throw new RuntimeException("NYI method removeUser");

    }

    @Override
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
        List<UserJson> incomeInvitations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserJson addressee = new UsersDbClient().createUser(RandomDataUtils.randomUsername(), defaultPassword);
            execute(userApi.sendInvitation(addressee.username(), targetUser.username()));
            incomeInvitations.add(addressee);
        }
        targetUser.testData().incomeInvitations().addAll(incomeInvitations);
        return incomeInvitations;
    }

    @Override
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        List<UserJson> outcomeInvitations = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            UserJson addressee = new UsersDbClient().createUser(RandomDataUtils.randomUsername(), defaultPassword);
            execute(userApi.sendInvitation(targetUser.username(), addressee.username()));
            outcomeInvitations.add(addressee);
        }
        targetUser.testData().outcomeInvitations().addAll(outcomeInvitations);
        return outcomeInvitations;
    }

    @Override
    public List<UserJson> addFriends(UserJson targetUser, int count) {
        List<UserJson> friends = new ArrayList<>();
        UserJson addressee;
        for (int i = 0; i < count; i++) {
            addressee = new UsersDbClient().createUser(RandomDataUtils.randomUsername(), defaultPassword);
            execute(userApi.sendInvitation(targetUser.username(), addressee.username()));
            execute(userApi.acceptInvitation(addressee.username(), targetUser.username()));
            friends.add(addressee);
            addressee.testData().friends().add(targetUser);
        }
        targetUser.testData().friends().addAll(friends);

        return friends;
    }

    @Override
    public Optional<UserJson> findUserByUsername(String username) {
        return Optional.ofNullable(execute(userApi.currentUser(username)));
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
