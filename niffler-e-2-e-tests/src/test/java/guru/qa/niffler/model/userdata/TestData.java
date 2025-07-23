package guru.qa.niffler.model.userdata;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import java.util.List;

public record TestData(String password,
                       List<CategoryJson> categories,
                       List<SpendJson> spends,
                       List<UserJson> friends,
                       List<UserJson> incomeInvitations,
                       List<UserJson> outcomeInvitations
) {

    public List<String> friendsUsernames() {
        return extractUsernames(friends);
    }

    public List<String> incomeInvitationsUsernames() {
        return extractUsernames(incomeInvitations);
    }

    public List<String> outcomeInvitationsUsernames() {
        return extractUsernames(outcomeInvitations);
    }

    private List<String> extractUsernames(List<UserJson> users) {
        return users.stream().map(UserJson::username).toList();
    }

    public TestData withUpdatedSpends(List<SpendJson> updatedSpends) {
        return new TestData(
                password,
                categories,
                updatedSpends,
                friends,
                incomeInvitations,
                outcomeInvitations
        );
    }

    public TestData withUpdatedCategories(List<CategoryJson> updatedCategories) {
        return new TestData(
                password,
                updatedCategories,
                spends,
                friends,
                incomeInvitations,
                outcomeInvitations
        );
    }
}
