package guru.qa.niffler.service.gateway;

import guru.qa.niffler.api.GetawayV2Api;
import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.RestClient;
import io.qameta.allure.Step;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ParametersAreNonnullByDefault
public class GatewayV2ApiClient extends RestClient {

    private final GetawayV2Api getawayV2Api;

    public GatewayV2ApiClient() {
        super(CFG.gatewayUrl());
        getawayV2Api = retrofit.create(GetawayV2Api.class);
    }

    @Step("Get all friends and income invitation using /api/v2/friends/all")
    @Nonnull
    public RestResponsePage<UserJson> allFriends(String bearerToken,
                                                 int page,
                                                 int size,
                                                 @Nullable String searchQuery) {
        final Response<RestResponsePage<UserJson>> response;
        try {
            response = getawayV2Api.allFriends(bearerToken, page, size, searchQuery)
                    .execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
        assertEquals(200, response.code());
        return response.body();
    }
}
