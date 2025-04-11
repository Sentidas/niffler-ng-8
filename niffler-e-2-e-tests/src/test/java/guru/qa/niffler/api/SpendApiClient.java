package guru.qa.niffler.api;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.CategoryJson;
import guru.qa.niffler.model.CurrencyValues;
import guru.qa.niffler.model.SpendJson;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.io.IOException;
import java.util.List;

public class SpendApiClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);

    public SpendJson createSpend(SpendJson spend) {
        Response<SpendJson> response = executeWithHandling(spendApi.addSpend(spend));
        checkResponse(response, 201);
        return response.body();
    }

    public SpendJson editSpend(SpendJson spend) {
        Response<SpendJson> response = executeWithHandling(spendApi.editSpend(spend));
        checkResponse(response, 200);
        return response.body();
    }

    public SpendJson getSpend(String id, String username) {
        Response<SpendJson> response = executeWithHandling(spendApi.getSpend(id, username));
        checkResponse(response, 200);
        return response.body();
    }

    public List<SpendJson> getSpends(String username, CurrencyValues filterCurrency, String fromDate, String toDate) {
        Response<List<SpendJson>> response = executeWithHandling(spendApi.getSpends(username, filterCurrency, fromDate, toDate));
        checkResponse(response, 200);
        return response.body();
    }

    public List<SpendJson> getSpends(String username) {
        Response<List<SpendJson>> response = executeWithHandling(spendApi.getSpends(username, null, null, null));
        checkResponse(response, 200);
        return response.body();
    }

    public void removeSpends(String username, List<String> ids) {
        Response<Void> response = executeWithHandling(spendApi.deleteSpends(username, ids));
        checkResponse(response, 202);
    }

    public CategoryJson createCategory(CategoryJson category) {
        Response<CategoryJson> response = executeWithHandling(spendApi.addCategory(category));
        checkResponse(response, 200);
        return response.body();
    }

    public CategoryJson editCategory(CategoryJson category) {
        Response<CategoryJson> response = executeWithHandling(spendApi.updateCategory(category));
        checkResponse(response, 200);
        return response.body();
    }

    public List<CategoryJson> getCategories(String username, Boolean excludeArchived) {
        Response<List<CategoryJson>> response = executeWithHandling(spendApi.getCategories(username, excludeArchived));
        checkResponse(response, 200);
        return response.body();
    }

    public List<CategoryJson> getCategories(String username) {
        Response<List<CategoryJson>> response = executeWithHandling(spendApi.getCategories(username, null));
        checkResponse(response, 200);
        return response.body();
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
