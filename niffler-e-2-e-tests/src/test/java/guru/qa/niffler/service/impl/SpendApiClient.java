package guru.qa.niffler.service.impl;

import guru.qa.niffler.api.SpendApi;
import guru.qa.niffler.config.Config;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendClient;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpendApiClient extends BaseApiClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder().build();
    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(CFG.spendUrl())
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();

    private final SpendApi spendApi = retrofit.create(SpendApi.class);


    @Override
    public SpendJson createSpend(SpendJson spend) {

        findCategoryByUsernameAndSpendName(spend.username(), spend.category().name())
                .orElseGet(() -> createCategory(spend.category()));

        return execute(spendApi.addSpend(spend));

    }


    private List<CategoryJson> existingCategories(String username) {
        return execute(spendApi.getCategories(username, false));

    }

    private List<SpendJson> existingSpends(String username) {
        return execute(spendApi.getSpends(username, null, null, null));

    }


    @Override
    public SpendJson updateSpend(SpendJson spend) {
        return execute(spendApi.editSpend(spend));
    }

    @Override
    public CategoryJson createCategory(CategoryJson category) {
        return execute(spendApi.addCategory(category));
    }

    @Override
    public CategoryJson updateCategory(CategoryJson category) {
        return execute(spendApi.updateCategory(category));

    }

    @Override
    public Optional<SpendJson> findSpendByIdAndUsername(UUID id, String username) {
        return Optional.ofNullable(execute(spendApi.getSpend(id.toString(), username)));
    }

    @Override
    public Optional<SpendJson> findByUsernameAndDescription(String username, String description) {
        List<SpendJson> existsSpends = existingSpends(username);
        return existsSpends.stream()
                .filter(spend -> spend.description().equals(description))
                .findFirst();
    }

    @Override
    public Optional<CategoryJson> findCategoryByUsernameAndSpendName(String username, String categoryName) {

        List<CategoryJson> existsCategories = existingCategories(username);

        return existsCategories.stream()
                .filter(category -> category.name().equals(categoryName))
                .findFirst();
    }

    @Override
    public void removeSpend(SpendJson spend) {

        UUID spendId = spend.id();
        String username = spend.username();

        Optional<SpendJson> findSpend = findSpendByIdAndUsername(spendId, username);
        if (findSpend.isPresent()) {

            executeWithoutBody(spendApi.deleteSpends(username, List.of(spendId.toString())));
        }
    }


    @Override
    public Optional<CategoryJson> findCategoryById(UUID id) {
        throw new RuntimeException("NYI method findCategoryById");
    }

    @Override
    public Optional<CategoryJson> removeCategory(CategoryJson category) {
        throw new UnsupportedOperationException("NYI method removeCategory");
    }

}
