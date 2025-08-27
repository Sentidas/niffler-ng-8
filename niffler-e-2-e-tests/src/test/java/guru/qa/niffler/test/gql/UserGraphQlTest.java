package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Error;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.UserCategoriesWithFriendsCategoriesQuery;
import guru.qa.UserWithNestedFriendsQuery;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Category;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.userdata.UserJson;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserGraphQlTest extends BaseGraphQlTest {

    @User(
            categories = {
                    @Category(name = "Обучение"),
                    @Category(name = "Ремонт"),
                    @Category(name = "Путешествие на Алтай"),
            }, friends = 1)
    @ApiLogin
    void userCategoriesShouldBeReturned(@Token String bearerToken, UserJson user) {

        final ApolloCall<UserCategoriesWithFriendsCategoriesQuery.Data> userCall = apolloClient.query(UserCategoriesWithFriendsCategoriesQuery.builder()
                        .page(0)
                        .size(12)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<UserCategoriesWithFriendsCategoriesQuery.Data> categoriesCall = Rx2Apollo.single(userCall).blockingGet();

        final UserCategoriesWithFriendsCategoriesQuery.Data responseData = categoriesCall.dataOrThrow();

        List<String> actualCategories = responseData.user.categories.stream()
                .map(category -> category.name).toList();

        List<String> expectedCategories = user.testData().categories().stream()
                .map(CategoryJson::name).toList();

        assertEquals(3, actualCategories.size());
        assertTrue(
                actualCategories.containsAll(expectedCategories) &&
                        expectedCategories.containsAll(actualCategories),
                "Categories do not match expected set");
    }


    @User(friends = 1)
    @ApiLogin
    void friendCategoriesShouldNotBeReturned(@Token String bearerToken, UserJson user) {

        final ApolloCall<UserCategoriesWithFriendsCategoriesQuery.Data> userCall = apolloClient.query(UserCategoriesWithFriendsCategoriesQuery.builder()
                        .page(0)
                        .size(12)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<UserCategoriesWithFriendsCategoriesQuery.Data> response = Rx2Apollo.single(userCall).blockingGet();
        final List<Error> errors = response.errors;

        assertEquals(
                "Can`t query categories for another user",
                errors.getFirst().getMessage()
        );
    }


    @User(friends = 1)
    @ApiLogin
    void nestedFriendsShouldNotBeReturned(@Token String bearerToken, UserJson user) {

        final ApolloCall<UserWithNestedFriendsQuery.Data> userCall = apolloClient.query(UserWithNestedFriendsQuery.builder()
                        .page(0)
                        .size(12)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<UserWithNestedFriendsQuery.Data> response = Rx2Apollo.single(userCall).blockingGet();
        final List<Error> errors = response.errors;

        assertEquals(
                "Can`t fetch over 2 friends sub-queries",
                errors.getFirst().getMessage()
        );
    }
}