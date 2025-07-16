package guru.qa.niffler.page.usercontext;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.model.CategoryEdit;
import guru.qa.niffler.page.model.SpendEdit;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;


public class ExpectedUserContext {
    @Getter
    private UserJson expectedUser;

    public void setInitialUser(UserJson initialUser) {
        this.expectedUser = initialUser;
    }

    public UserJson get() {
        return expectedUser;
    }

    public void applySpendEdit(SpendEdit command) {
        List<SpendJson> updatedSpends = expectedUser.testData().spends().stream()
                .map(spend -> {
                    if (!spend.description().equals(command.originalDescription())) {
                        return spend;
                    }

                    return new SpendJson(
                            spend.id(),
                            spend.spendDate(),
                            command.newCategory() != null ? command.newCategory() : spend.category(),
                            command.newCurrency() != null ? command.newCurrency() : spend.currency(),
                            command.newAmount() != null ? command.newAmount() : spend.amount(),
                            command.newDescription() != null ? command.newDescription() : spend.description(),
                            spend.username()
                    );
                })
                .toList();

        expectedUser = expectedUser.withTestData(
                expectedUser.testData().withUpdatedSpends(updatedSpends)
        );
    }

    public void applySpendDelete(String category, String description) {
        List<SpendJson> updatedSpends = new ArrayList<>();
        boolean deleted = false;

        for (SpendJson spend : expectedUser.testData().spends()) {
            if (!deleted && spend.description().equals(description) && spend.category().name().equals(category)) {
                deleted = true;
                continue;
            }
            updatedSpends.add(spend);
        }

        expectedUser = expectedUser.withTestData(
                expectedUser.testData().withUpdatedSpends(updatedSpends)
        );
    }

    public void applyCategoryEdit(CategoryEdit command) {
        List<CategoryJson> updatedCategories = expectedUser.testData().categories().stream()
                .map(cat -> cat.name().equals(command.categoryName())
                        ? cat.withArchived(command.archived())
                        : cat)
                .toList();

        List<SpendJson> updatedSpends = expectedUser.testData().spends().stream()
                .map(spend -> {
                    if (spend.category().name().equals(command.categoryName())) {
                        return new SpendJson(
                                spend.id(),
                                spend.spendDate(),
                                spend.category().withArchived(command.archived()),
                                spend.currency(),
                                spend.amount(),
                                spend.description(),
                                spend.username()
                        );
                    }
                    return spend;
                })
                .toList();

        expectedUser = expectedUser.withTestData(
                expectedUser.testData()
                        .withUpdatedCategories(updatedCategories)
                        .withUpdatedSpends(updatedSpends)
        );
    }
}