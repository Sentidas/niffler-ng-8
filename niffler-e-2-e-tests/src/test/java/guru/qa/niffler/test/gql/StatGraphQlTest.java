package guru.qa.niffler.test.gql;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.StatQuery;
import guru.qa.niffler.jupiter.annotation.*;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.utils.UtilsStat;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static guru.qa.niffler.utils.UtilsStat.calculateCategorySums;
import static guru.qa.type.CurrencyValues.USD;
import static org.junit.jupiter.api.Assertions.*;

public class StatGraphQlTest extends BaseGraphQlTest {

    @User
    @Test
    @ApiLogin
    void shouldReturnZeroWhenNoSpendingsExist(@Token String bearerToken) {

        final ApolloCall<StatQuery.Data> stat = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .filterPeriod(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(stat).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();
        final StatQuery.Stat result = data.stat;

        assertEquals(0.0, result.total);
    }

    @User(
            categories = {
                    @Category(name = "Обучение"),
                    @Category(name = "Ремонт", archived = true),
                    @Category(name = "Путешествие на Алтай"),
                    @Category(name = "Здоровье", archived = true),

            },
            spendings = {
                    @Spend(category = "Обучение", description = "Дизайнер курс", amount = 950, currency = CurrencyValues.EUR),
                    @Spend(category = "Ремонт", description = "Модная ванная", amount = 50000),
                    @Spend(category = "Здоровье", description = "Стоматолог", amount = 100000),
                    @Spend(category = "Путешествие на Алтай", description = "Корм для нерп", amount = 2000, currency = CurrencyValues.RUB)
            }
    )
    @Test
    @ApiLogin
    void shouldGroupArchivedCategoriesAndReturnCorrectTotalSum(@Token String bearerToken, UserJson user) {

        Map<String, Double> expectedCategoriesWithSum = calculateCategorySums(user);

        final ApolloCall<StatQuery.Data> stat = apolloClient.query(StatQuery.builder()
                        .filterCurrency(null)
                        .statCurrency(null)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(stat).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();

        List<String> actualCategoryNames = data.stat.statByCategories.stream()
                .map(statByCategory -> statByCategory.categoryName)
                .toList();

        final StatQuery.StatByCategory actualArchivedStat = data.stat.statByCategories.stream()
                .filter(c -> c.categoryName.equals("Archived"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Archived category not found in stats"));

        assertAll("archived categories",
                () -> assertFalse(actualCategoryNames.contains("Ремонт"), "Archived categories should be grouped under 'Archived'"),
                () -> assertFalse(actualCategoryNames.contains("Здоровье"), "Archived categories should be grouped under 'Archived'"),
                () -> assertEquals(expectedCategoriesWithSum.get("Archived"), actualArchivedStat.sum));
    }


    @User(
            spendings = {
                    @Spend(category = "Обучение", description = "Дизайнер курс", amount = 950, currency = CurrencyValues.EUR),
                    @Spend(category = "Обучение", description = "Китайский язык", amount = 200, currency = CurrencyValues.USD),
                    @Spend(category = "Ремонт", description = "Модная ванная", amount = 500, currency = CurrencyValues.EUR),
                    @Spend(category = "Здоровье", description = "Стоматолог", amount = 100000, currency = CurrencyValues.RUB),
                    @Spend(category = "Здоровье", description = "Спа в горах", amount = 1000, currency = CurrencyValues.USD),
                    @Spend(category = "Здоровье", description = "Спортзал абонемент", amount = 300, currency = CurrencyValues.USD),
                    @Spend(category = "Путешествие на Алтай", description = "Корм для нерп", amount = 2000, currency = CurrencyValues.RUB)
            }
    )
    @Test
    @ApiLogin
    void shouldReturnedStatInSelectedCurrencyWhenFilteredByCurrency(@Token String bearerToken, UserJson user) {
        double expectedTotalUsdSum = UtilsStat.calculateTotalByCurrency(user, CurrencyValues.USD);

        Map<String, Double> expectedCategoriesWithSumByCurrency = UtilsStat.calculateCategorySumsByCurrency(user, CurrencyValues.USD);

        final ApolloCall<StatQuery.Data> stat = apolloClient.query(StatQuery.builder()
                        .filterCurrency(USD)
                        .statCurrency(USD)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(stat).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();

        final StatQuery.Stat actualTotalStat = data.stat;
        final List<StatQuery.StatByCategory> actualCategories = data.stat.statByCategories;

        assertAll("stat totals and categories",

                // check total stat in USD
                () -> assertEquals(expectedTotalUsdSum, actualTotalStat.total),
                () -> assertEquals(USD, actualTotalStat.currency),

                // check categories in USD
                () -> assertEquals(Set.of("Здоровье", "Обучение"),
                        actualCategories.stream()
                                .map(c -> c.categoryName)
                                .collect(Collectors.toSet())),
                () -> assertTrue(actualCategories.stream()
                        .allMatch(c -> c.currency == USD)),

                // check sum of categories in USD
                () -> assertEquals(expectedCategoriesWithSumByCurrency.get("Здоровье"), actualCategories.stream()
                        .filter(c -> c.categoryName.equals("Здоровье"))
                        .findFirst()
                        .orElseThrow().sum),
                () -> assertEquals(expectedCategoriesWithSumByCurrency.get("Обучение"), actualCategories.stream()
                        .filter(c -> c.categoryName.equals("Обучение"))
                        .findFirst()
                        .orElseThrow().sum)
        );
    }


    @User(categories = {@Category(name = "Здоровье", archived = true)},
            spendings = {
                    @Spend(category = "Обучение", description = "Дизайнер курс", amount = 950, currency = CurrencyValues.EUR),
                    @Spend(category = "Обучение", description = "Китайский язык", amount = 200, currency = CurrencyValues.USD),
                    @Spend(category = "Ремонт", description = "Модная ванная", amount = 500, currency = CurrencyValues.EUR),
                    @Spend(category = "Здоровье", description = "Стоматолог", amount = 100000, currency = CurrencyValues.RUB),
                    @Spend(category = "Здоровье", description = "Спа в горах", amount = 1000, currency = CurrencyValues.USD),
                    @Spend(category = "Здоровье", description = "Спортзал абонемент", amount = 300, currency = CurrencyValues.USD),
                    @Spend(category = "Путешествие на Алтай", description = "Корм для нерп", amount = 2000, currency = CurrencyValues.RUB)
            }
    )
    @Test
    @ApiLogin
    void shouldReturnedStatInSelectedCurrencyIncludingArchivedCategories(@Token String bearerToken, UserJson user) {
        double expectedTotalUsdSum = UtilsStat.calculateTotalByCurrency(user, CurrencyValues.USD);

        Map<String, Double> expectedCategoriesWithSumByCurrency = UtilsStat.calculateCategorySumsByCurrency(user, CurrencyValues.USD);

        final ApolloCall<StatQuery.Data> stat = apolloClient.query(StatQuery.builder()
                        .filterCurrency(USD)
                        .statCurrency(USD)
                        .build())
                .addHttpHeader("authorization", bearerToken);

        final ApolloResponse<StatQuery.Data> response = Rx2Apollo.single(stat).blockingGet();
        final StatQuery.Data data = response.dataOrThrow();

        final StatQuery.Stat actualTotalStat = data.stat;
        final List<StatQuery.StatByCategory> actualCategories = data.stat.statByCategories;

        assertAll("stat totals and categories",

                // check total stat in USD
                () -> assertEquals(expectedTotalUsdSum, actualTotalStat.total),
                () -> assertEquals(USD, actualTotalStat.currency),

                // check categories with Archived in USD
                () -> assertEquals(Set.of("Обучение", "Archived"),
                        actualCategories.stream()
                                .map(c -> c.categoryName)
                                .collect(Collectors.toSet())),
                () -> assertTrue(actualCategories.stream()
                        .allMatch(c -> c.currency == USD)),

                // // check sum of categories with Archived in USD
                () -> assertEquals(expectedCategoriesWithSumByCurrency.get("Обучение"), actualCategories.stream()
                        .filter(c -> c.categoryName.equals("Обучение"))
                        .findFirst()
                        .orElseThrow().sum),
                () -> assertEquals(expectedCategoriesWithSumByCurrency.get("Archived"), actualCategories.stream()
                        .filter(c -> c.categoryName.equals("Archived"))
                        .findFirst()
                        .orElseThrow().sum)
        );
    }
}