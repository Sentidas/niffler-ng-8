package guru.qa.niffler.test.web.JDBC;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class SpringJDBCTest {

    @Test
    void createCategory() {

        SpendDbClient dbClient = new SpendDbClient();

        CategoryJson createdCategory = dbClient.createCategorySpringJdbc(
                new CategoryJson(
                        null,
                        "song and dance",
                        "duck",
                        false
                )
        );
        System.out.println(createdCategory.name());
    }

    @Test
    void findCategoryById() {
        UUID categoryId = UUID.fromString("3ae7c675-6abd-4cf1-ac54-1d35cd34e300");
        SpendDbClient spendDbClient = new SpendDbClient();
        Optional<CategoryJson> category = spendDbClient.findCategoryByIdSpringJdbc(categoryId);

        category.ifPresentOrElse(
                categoryJson -> System.out.println("Категория найдена: " + categoryJson.name()),
                () -> System.out.println("Категория с таким Id не найдена"));
    }

    @Test
    void findCategoryByNameAndUserName() {

        SpendDbClient spendDbClient = new SpendDbClient();
        Optional<CategoryJson> category = spendDbClient.findCategoryByNameAndUserNameSpringJdbc("duck", "Такси_42");

        System.out.println("Категория найдена: " + category.get().name());
    }

    @Test
    void findAllCategoriesByUserName() {

        SpendDbClient spendDbClient = new SpendDbClient();
        List<CategoryJson> categories = spendDbClient.findAllCategoriesByUserNameSpringJdbc("duck");
        System.out.println("Список категорий: ");
        for (CategoryJson category : categories) {
            System.out.println(category.name());
        }
    }



    @Test
    void deleteCategory() {
        // UUID categoryId = UUID.fromString("91326cc6-2105-11f0-bc4d-0242ac110004");
        UUID categoryId = UUID.fromString("98b237f6-2425-11f0-9be4-0242ac110004");
        SpendDbClient spendDbClient = new SpendDbClient();
        spendDbClient.deleteCategorySpringJdbc(categoryId);
    }

    @Test
    void findAllCategories() {

        SpendDbClient spendDbClient = new SpendDbClient();
        List<CategoryJson> categories = spendDbClient.findAllCategoriesSpringJdbc();
        System.out.println("Общий список категорий: ");
        for (CategoryJson category : categories) {
            System.out.println(category.name());
        }
    }


    @Test
    void createSpend() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpendSpringJdbc(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "корм",
                                "duck",
                                false
                        ),
                        CurrencyValues.EUR,
                        1000.0,
                        "1111",
                        "duck"
                )
        );
        System.out.println(spend);
    }


    @Test
    void findSpend() {
        UUID spendId = UUID.fromString("e95d8a0a-22bf-11f0-8499-0242ac110004");
        // UUID spendId = UUID.fromString("e18b9e81-fe04-4cc8-a055-54c8ed5ffe33");

        SpendDbClient spendDbClient = new SpendDbClient();
        Optional<SpendJson> spend = spendDbClient.findSpendByIdSpringJdbc(spendId);

        spend.ifPresentOrElse(
                spendJson -> System.out.println("Spend найден: " + spend.get().description()),
                () -> System.out.println("Spend с таким Id не найден"));
    }

    @Test
    void findAllSpendsUser() {

        SpendDbClient spendDbClient = new SpendDbClient();
        List<SpendJson> spends = spendDbClient.findAllSpendByUserNameSpringJdbc("duck");
        System.out.println("Spends пользователя: ");
        for (SpendJson spend : spends) {
            System.out.println(spend.description() + " ");
        }
    }

    @Test
    void findAllSpends() {

        SpendDbClient spendDbClient = new SpendDbClient();
        List<SpendJson> spends = spendDbClient.findAllSpendsSpringJdbc();
        System.out.println("Общий список spends : ");
        for (SpendJson spend : spends) {
            System.out.println(spend.description() + " ");
        }
    }

    @Test
    void deleteSpend() {
        // UUID spendId = UUID.fromString("410177ba-2105-11f0-bfca-0242ac110004");
        UUID spendId = UUID.fromString("11a98d5e-2443-11f0-a707-0242ac110004");

        SpendDbClient spendDbClient = new SpendDbClient();
        spendDbClient.deleteSpendSpringJdbc(spendId);
    }
}
