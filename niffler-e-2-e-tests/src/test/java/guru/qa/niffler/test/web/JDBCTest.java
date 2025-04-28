package guru.qa.niffler.test.web;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.SpendDbClient;
import guru.qa.niffler.service.UsersDbClient;
import org.junit.jupiter.api.Test;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JDBCTest {

    @Test
    void createSpend() {
        SpendDbClient spendDbClient = new SpendDbClient();

        SpendJson spend = spendDbClient.createSpend(
                new SpendJson(
                        null,
                        new Date(),
                        new CategoryJson(
                                null,
                                "корм для собаки",
                                "duck",
                                false
                        ),
                        CurrencyValues.EUR,
                        1000.0,
                        "100000 dog",
                        "duck"
                )
        );
        System.out.println(spend);
    }


    @Test
    void findSpend() {
        UUID spendId = UUID.fromString("8e400fe8-db3c-4126-b87d-2e776e82de91");
        // UUID spendId = UUID.fromString("e18b9e81-fe04-4cc8-a055-54c8ed5ffe33");

        SpendDbClient spendDbClient = new SpendDbClient();
        Optional<SpendJson> spend = spendDbClient.findSpendById(spendId);

        spend.ifPresentOrElse(
                categoryJson -> System.out.println("Spend найден: " + spend.get().description()),
                () -> System.out.println("Spend с таким Id не найден"));
    }

    @Test
    void findAllSpend() {

        SpendDbClient spendDbClient = new SpendDbClient();
        List<SpendJson> spends = spendDbClient.findAllByUsername("duck");
        System.out.println("Spends пользователя: ");
        for (SpendJson spend : spends) {
            System.out.println(spend.description() + " ");
        }
    }

    @Test
    void deleteSpend() {
        // UUID spendId = UUID.fromString("410177ba-2105-11f0-bfca-0242ac110004");
        UUID spendId = UUID.fromString("5ecb6f18-200a-11f0-9410-0242ac110004");

        SpendDbClient spendDbClient = new SpendDbClient();
        spendDbClient.deleteSpend(spendId);
    }

    @Test
    void createCategory() {
        SpendDbClient spendDbClient = new SpendDbClient();

        CategoryJson category = spendDbClient.createCategory(
                new CategoryJson(
                        null,
                        "hobby",
                        "duck",
                        true
                )
        );
        System.out.println(category);
    }


    @Test
    void findCategoryById() {
        UUID categoryId = UUID.fromString("3ae7c675-6abd-4cf1-ac54-1d35cd34e300");
        SpendDbClient spendDbClient = new SpendDbClient();
        Optional<CategoryJson> category = spendDbClient.findCategoryById(categoryId);

        category.ifPresentOrElse(
                categoryJson -> System.out.println("Категория найдена: " + categoryJson.name()),
                () -> System.out.println("Категория с таким Id не найдена"));
    }

    @Test
    void findCategoryByNameAndUserName() {

        SpendDbClient spendDbClient = new SpendDbClient();
        Optional<CategoryJson> category = spendDbClient.findCategoryByNameAndUserName("duck", "Такси_42");

        System.out.println("Категория найдена: " + category.get().name());
    }

    @Test
    void findAllCategoryByUserName() {

        SpendDbClient spendDbClient = new SpendDbClient();
        List<CategoryJson> categories = spendDbClient.findAllCategoryByUserName("duck");
        System.out.println("Список категорий: ");
        for (CategoryJson category : categories) {
            System.out.println(category.name());
        }
    }

    @Test
    void deleteCategory() {
        // UUID categoryId = UUID.fromString("91326cc6-2105-11f0-bc4d-0242ac110004");
        UUID categoryId = UUID.fromString("16416d06-1ff1-11f0-a99a-0242ac110004");
        SpendDbClient spendDbClient = new SpendDbClient();
        spendDbClient.deleteCategory(categoryId);
    }


    @Test
    void findUserById() {
        UUID userId = UUID.fromString("a18b5ba5-1f73-4164-87f7-ebfbe8dc4585");

        UsersDbClient us = new UsersDbClient();
        Optional<UserJson> user = us.findUserById(userId);

        System.out.println("User найден: " + user.get().username());

    }

    @Test
    void findUserByUsername() {

        UsersDbClient us = new UsersDbClient();
        Optional<UserJson> user = us.findUserByUsername("duck");

        System.out.println("User найден: " + user.get().id());
        // a18b5ba5-1f73-4164-87f7-ebfbe8dc4585

    }

    @Test
    void deleteUser() {
        UUID userId = UUID.fromString("cd6aec3c-236b-11f0-9601-0242ac110004");

        UsersDbClient us = new UsersDbClient();
        us.deleteUser(userId);
    }

    @Test
    void createUser() {

        UsersDbClient us = new UsersDbClient();

        UserJson userJson =
                new UserJson(
                        null,
                        "minni",
                        CurrencyValues.USD,
                        null,
                        null,
                        null,
                        null,
                        null

                );
        UserJson user = us.createUser(userJson);

        System.out.println(user);
    }
}



