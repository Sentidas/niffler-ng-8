package guru.qa.niffler.test.web.ServiceTest;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendClient;
import guru.qa.niffler.service.impl.SpendApiClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

public class CategoryAndSpendApiTest {

    @Test
    void createSpendWithExistsCategory() {
        SpendClient spendApiClient = new SpendApiClient();

        SpendJson spend = spendApiClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "морские забавы",
                                "duck",
                                false,
                                null
                        ),
                        CurrencyValues.USD,
                        977.0,
                        "new spend" + new Random().nextInt(12),
                        "duck"
                )
        );
        System.out.println(spend);
    }

    @Test
    void createSpendWithNewCategory() {
        SpendClient spendApiClient = new SpendApiClient();

        SpendJson spend = spendApiClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                RandomDataUtils.randomCategoryName(),
                                "duck",
                                false,
                                null
                        ),
                        CurrencyValues.EUR,
                        999.0,
                        "new spend" + new Random().nextInt(12),
                        "duck"
                )
        );
        System.out.println(spend);
    }


    @Test
    void updateSpend() {
        SpendClient spendApiClient = new SpendApiClient();
        UUID spendId = UUID.fromString("4043f861-8e23-4b87-a17f-f114384561b4");

        SpendJson spend = spendApiClient.updateSpend(
                new SpendJson(
                        spendId,
                        new Date(),
                        new CategoryJson(
                                null,
                                "my new category",
                                "duck",
                                false,
                                null
                        ),
                        CurrencyValues.USD,
                        111.00,
                        "капибару заведи",
                        "duck"
                )
        );
        System.out.println(spend);
    }

    @Test
    void findSpendByIdAndUsername() {
        UUID spendId = UUID.fromString("4043f861-8e23-4b87-a17f-f114384561b4");
        String username = "duck";

        SpendClient spendApiClient = new SpendApiClient();
        Optional<SpendJson> spend = spendApiClient.findSpendByIdAndUsername(spendId, username);

        spend.ifPresentOrElse(
                spendJson -> {
                    System.out.println("Spend найден: " + spend.get().description());
                    System.out.println("Spend категории: " + spend.get().category().name());
                },
                () -> System.out.println("Spend с таким Id не найден"));
    }

    @Test
    void findSpendByUsernameAndDescription() {
        SpendClient spendApiClient = new SpendApiClient();
        Optional<SpendJson> spend = spendApiClient.findByUsernameAndDescription("duck", "капибару заведи");

        spend.ifPresentOrElse(
                spendJson -> {
                    System.out.println("Spend найден: " + spend.get().description());
                    System.out.println("Spend категории: " + spend.get().category().name());
                },
                () -> System.out.println("Spend с таким Id не найден"));
    }


    @Test
    void deleteSpend() {
        SpendClient spendApiClient = new SpendApiClient();
        UUID spendId = UUID.fromString("4145ad12-ae23-49ef-b467-7dd02eb31663");
        SpendJson spend =
                new SpendJson(
                        spendId,
                        null,
                       null,
                        null,
                        null,
                        null,
                        "panda"
                );

        spendApiClient.removeSpend(spend);
    }


    @Test
    void createCategory() {
        SpendClient spendApiClient = new SpendApiClient();
        CategoryJson category = spendApiClient.createCategory(
                new CategoryJson(
                        null,
                        "dance15",
                        "panda",
                        false,
                        null
                )
        );
        System.out.println(category);
    }

    @Test
    void errorCreateSpendWithNewCategoryWithLimitCreateCategory() {
        SpendClient spendApiClient = new SpendApiClient();

        CategoryJson category = spendApiClient.createCategory(
                new CategoryJson(
                        null,
                        "dance15",
                        "duck",
                        false,
                        null
                )
        );
        System.out.println(category);
    }

    @Test
    void updateCategory() {
        SpendClient spendApiClient = new SpendApiClient();
        UUID categoryId = UUID.fromString("2f87face-fcfe-44ed-81b7-911e8fa6cab9");

        CategoryJson category = spendApiClient.updateCategory(
                new CategoryJson(
                        categoryId,
                        "морские забавы666",
                        "panda",
                        true,
                        null
                )
        );
        System.out.println(category);
    }


    @Test
    void findCategoryByNameAndUserName() {

        SpendClient spendApiClient = new SpendApiClient();
        Optional<CategoryJson> category = spendApiClient.findCategoryByUsernameAndSpendName("duck", "dance");

        category.ifPresentOrElse(
                categoryJson -> {
                    System.out.println("Категория найдена: " + categoryJson.name());
                    System.out.println("Spends категории:" + categoryJson.spends());
                },
                () -> System.out.println("Категория с таким Id не найдена"));
    }
}



