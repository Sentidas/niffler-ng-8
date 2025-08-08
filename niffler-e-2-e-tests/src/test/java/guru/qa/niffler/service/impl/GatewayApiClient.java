package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.core.GetawayApi;
import guru.qa.niffler.model.userdata.UserJson;
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

    @Step("Get all friends and income invitation spend using /api/friends/all endpoint")
    @Nonnull
    public List<UserJson> allFriends(String bearerToken, @Nullable String searchQuery) {
        final Response<List<UserJson>> response;
        try {
            response = getawayApi.allFriends(bearerToken, searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }
}
