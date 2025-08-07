package guru.qa.niffler.model.userdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public record TestData(
        @JsonIgnore @Nonnull String password,
        @JsonIgnore @Nonnull List<CategoryJson> categories,
        @JsonIgnore @Nonnull List<SpendJson> spends,
        @JsonIgnore @Nonnull List<UserJson> friends,
        @JsonIgnore @Nonnull List<UserJson> incomeInvitations,
        @JsonIgnore @Nonnull List<UserJson> outcomeInvitations
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

    public TestData(@Nonnull String password) {
        this(password, new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
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
