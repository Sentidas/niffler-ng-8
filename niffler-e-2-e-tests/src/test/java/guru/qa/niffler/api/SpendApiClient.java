package guru.qa.niffler.api;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.RestClient;
import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

@ParametersAreNonnullByDefault
public class SpendApiClient extends RestClient {

    private final SpendApi spendApi;

    public SpendApiClient() {
        super(CFG.spendUrl());
        this.spendApi = retrofit.create(SpendApi.class);
    }

    public @Nullable SpendJson createSpend(SpendJson spend) {
        Response<SpendJson> response = executeWithHandling(spendApi.addSpend(spend));
        checkResponse(response, 201);
        return response.body();
    }

    public @Nullable SpendJson editSpend(SpendJson spend) {
        Response<SpendJson> response = executeWithHandling(spendApi.editSpend(spend));
        checkResponse(response, 200);
        return response.body();
    }

    public @Nullable SpendJson getSpend(String id, String username) {
        Response<SpendJson> response = executeWithHandling(spendApi.getSpend(id, username));
        checkResponse(response, 200);
        return response.body();
    }

    public @Nonnull List<SpendJson> getSpends(String username,
                                              @Nullable CurrencyValues filterCurrency,
                                              @Nullable String fromDate,
                                              @Nullable String toDate) {
        Response<List<SpendJson>> response = executeWithHandling(spendApi.getSpends(username, filterCurrency, fromDate, toDate));
        checkResponse(response, 200);
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }

    public @Nonnull List<SpendJson> getSpends(String username) {
        Response<List<SpendJson>> response = executeWithHandling(spendApi.getSpends(username, null, null, null));
        checkResponse(response, 200);
        return response.body();
    }

    public void removeSpends(String username, List<String> ids) {
        Response<Void> response = executeWithHandling(spendApi.deleteSpends(username, ids));
        checkResponse(response, 202);
    }

    public @Nullable CategoryJson createCategory(CategoryJson category) {
        Response<CategoryJson> response = executeWithHandling(spendApi.addCategory(category));
        checkResponse(response, 200);
        return response.body();
    }

    public @Nullable CategoryJson editCategory(CategoryJson category) {
        Response<CategoryJson> response = executeWithHandling(spendApi.updateCategory(category));
        checkResponse(response, 200);
        return response.body();
    }

    public @Nonnull List<CategoryJson> getCategories(String username, Boolean excludeArchived) {
        Response<List<CategoryJson>> response = executeWithHandling(spendApi.getCategories(username, excludeArchived));
        checkResponse(response, 200);
        return response.body();
    }

    public @Nonnull List<CategoryJson> getCategories(String username) {
        Response<List<CategoryJson>> response = executeWithHandling(spendApi.getCategories(username, null));
        checkResponse(response, 200);
        return response.body() != null
                ? response.body()
                : Collections.emptyList();
    }


    private <T> void checkResponse(Response<T> response, int... expectedCodes) {
        int actualCode = response.code();

        for (int code : expectedCodes) {
            if (actualCode == code) {
                return;
            }
        }
        throw new AssertionError("Unexpected API response code: " + actualCode);
    }

    private <T> Response<T> executeWithHandling(Call<T> call) {
        try {
            return call.execute();
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }
}
