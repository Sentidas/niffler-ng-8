package guru.qa.niffler.service;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import java.util.Optional;
import java.util.UUID;

public interface SpendClient {

    SpendJson createSpend(SpendJson spend);

    SpendJson updateSpend(SpendJson spend);

    CategoryJson createCategory(CategoryJson category);

    CategoryJson updateCategory(CategoryJson spend);

    Optional<SpendJson> findSpendByIdAndUsername(UUID id, String username);

    Optional<SpendJson> findByUsernameAndDescription(String username, String description);

    Optional<CategoryJson> findCategoryById(UUID id);

    Optional<CategoryJson> findCategoryByUsernameAndSpendName(String username, String name);

    void removeSpend(SpendJson spend);

    Optional<CategoryJson> removeCategory(CategoryJson category);

}
