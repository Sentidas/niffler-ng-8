package guru.qa.niffler.test.web.JDBC;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.impl.SpendDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class CategoryAndSpendTest {

    @Test
    void createSpend() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.create(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "корм для капибары",
                                "duck",
                                false,
                                null
                        ),
                        CurrencyValues.EUR,
                        1000.0,
                        "Школа122",
                        "duck"
                )
        );
        System.out.println(spend);
    }


    @Test
    void updateSpend() {
        SpendDbClient spendDbClient = new SpendDbClient();
        UUID spendId = UUID.fromString("f0275470-3551-11f0-ab5b-0242ac110004");

        SpendJson spend = spendDbClient.update(
                new SpendJson(
                        spendId,
                        new Date(),
                        null,
                        CurrencyValues.USD,
                        111.00,
                        "корм для капибары - spend3",
                        "duck"
                )
        );
        System.out.println(spend);
    }

    @Test
    void updateSpend2() {
        SpendDbClient spendDbClient = new SpendDbClient();
        UUID spendId = UUID.fromString("f0275470-3551-11f0-ab5b-0242ac110004");

        SpendJson spend = spendDbClient.update(
                new SpendJson(
                        spendId,
                        null,
                        new CategoryJson(
                                UUID.fromString("4c33c61c-2dab-11f0-b440-0242ac110004"),
                                null,
                                null,
                                null,
                                null
                        ),
                        CurrencyValues.USD,
                        777.0,
                        null,
                        "mouse"
                )
        );
        System.out.println(spend);
    }


    @Test
    void findSpendById() {
        UUID spendId = UUID.fromString("f0240338-3551-11f0-ab5b-0242ac110004");
        // UUID spendId = UUID.fromString("e18b9e81-fe04-4cc8-a055-54c8ed5ffe33");

        SpendDbClient spendDbClient = new SpendDbClient();
        Optional<SpendJson> spend = spendDbClient.findById(spendId);

        spend.ifPresentOrElse(
                spendJson -> {
                    System.out.println("Spend найден: " + spend.get().description());
                    System.out.println("Spend категории: " + spend.get().category().name());
                },
                () -> System.out.println("Spend с таким Id не найден"));
    }

    @Test
    void findSpendByUsernameAndDescription() {
        SpendDbClient spendDbClient = new SpendDbClient();
        Optional<SpendJson> spend = spendDbClient.findByUsernameAndDescription("duck", "Школа_62");

        spend.ifPresentOrElse(
                spendJson -> {
                    System.out.println("Spend найден: " + spend.get().description());
                    System.out.println("Spend категории: " + spend.get().category().name());
                },
                () -> System.out.println("Spend с таким Id не найден"));
    }


    @Test
    void deleteSpend() {
        SpendDbClient spendDbClient = new SpendDbClient();
        UUID spendId = UUID.fromString("4d371614-4837-43ea-abe2-59bc16bf664e");
        SpendJson spend =
                new SpendJson(
                        spendId,
                        null,
                       null,
                        null,
                        null,
                        null,
                        null
                );

        spendDbClient.remove(spend);
    }


    @Test
    void createCategory() {
        SpendDbClient spendDbClient = new SpendDbClient();
        CategoryJson category = spendDbClient.createCategory(
                new CategoryJson(
                        null,
                        "dance999",
                        "duck",
                        false,
                        null
                )
        );
        System.out.println(category);
    }

    @Test
    void updateCategory() {
        SpendDbClient spendDbClient = new SpendDbClient();
        UUID categoryId = UUID.fromString("864b87ca-3613-11f0-a4a5-0242ac110004");

        CategoryJson category = spendDbClient.updateCategory(
                new CategoryJson(
                        categoryId,
                        "морские забавы9",
                        "duck",
                        false,
                        null
                )
        );
        System.out.println(category);
    }


    @Test
    void findCategoryById() {
        UUID categoryId = UUID.fromString("f0240338-3551-11f0-ab5b-0242ac110004");
        SpendDbClient spendDbClient = new SpendDbClient();
        Optional<CategoryJson> category = spendDbClient.findCategoryById(categoryId);

        category.ifPresentOrElse(
                categoryJson -> {
                    System.out.println("Категория найдена: " + categoryJson.name());
                    System.out.println("Spends категории:" + categoryJson.spends());
                },
                () -> System.out.println("Категория с таким Id не найдена"));
    }

    @Test
    void findCategoryByNameAndUserName() {

        SpendDbClient spendDbClient = new SpendDbClient();
        Optional<CategoryJson> category = spendDbClient.findCategoryByUsernameAndSpendName("duck", "корм для капибары");

        category.ifPresentOrElse(
                categoryJson -> {
                    System.out.println("Категория найдена: " + categoryJson.name());
                    System.out.println("Spends категории:" + categoryJson.spends());
                },
                () -> System.out.println("Категория с таким Id не найдена"));
    }


    @Test
    void deleteCategory() {
        UUID categoryId = UUID.fromString("4c33c61c-2dab-11f0-b440-0242ac110004");
        CategoryJson category =  new CategoryJson(
                        categoryId,
                        null,
                        null,
                        false,
                        null
                );

        SpendDbClient spendDbClient = new SpendDbClient();
        spendDbClient.removeCategory(category);
    }
}



