package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;




public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    private final SpendDao spendDao = new guru.qa.niffler.data.dao.impl.SpendDaoJdbc();
    private final CategoryDao categoryDao = new guru.qa.niffler.data.dao.impl.CategoryDaoJdbc();
    // CATEGORY SPRING JDBC

    public CategoryJson createCategorySpringJdbc(CategoryJson category) {
        return CategoryJson.fromEntity(
                categoryDao
                        .create(
                                CategoryEntity.fromJson(category)
                        )
        );
    }

    public void updateCategorySpringJdbc(CategoryJson category) {
        CategoryJson.fromEntity(
                categoryDao
                        .update(
                                CategoryEntity.fromJson(category)
                        )
        );
    }

    public void deleteCategorySpringJdbc(UUID categoryId) {



        Optional<CategoryEntity> category = categoryDao
                .findCategoryById(categoryId);

        if (category.isPresent()) {
            categoryDao.deleteCategory(category.get());
        } else {
            throw new IllegalArgumentException("Категория для удаления не найдена: " + categoryId);
        }
    }

    public Optional<CategoryJson> findCategoryByNameAndUserNameSpringJdbc(String username, String categoryName) {

        Optional<CategoryEntity> category = categoryDao
                .findCategoryByUsernameAndCategoryName(username, categoryName);

        // return category.map(CategoryJson::fromEntity);

        if (category.isPresent()) {
            return Optional.of(CategoryJson.fromEntity(category.get()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<CategoryJson> findCategoryByIdSpringJdbc(UUID categoryId) {

        Optional<CategoryEntity> category = categoryDao
                .findCategoryById(categoryId);

        if (category.isPresent()) {
            return Optional.of(CategoryJson.fromEntity(category.get()));
        } else {
            return Optional.empty();
        }
    }

    public List<CategoryJson> findAllCategoriesByUserNameSpringJdbc(String username) {

        List<CategoryEntity> categories = categoryDao
                .findAllByUsername(username);

        return categories.stream()
                .map(CategoryJson::fromEntity)
                .toList();
    }

//    public List<CategoryJson> findAllCategoriesSpringJdbc() {
//        List<CategoryEntity> categories = new categoryDao
//                .findAll();
//
//        return categories.stream()
//                .map(CategoryJson::fromEntity)
//                .toList();
//
//    }

    // SPEND SPRING JDBC

    public SpendJson createSpendSpringJdbc(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);

        if (spendEntity.getCategory().getId() == null) {

            Optional<CategoryEntity> existingCategory = categoryDao
                    .findCategoryByUsernameAndCategoryName(spend.category().username(), spend.category().name());
            if (existingCategory.isPresent()) {
                spendEntity.setCategory(existingCategory.get());
            } else {
                CategoryEntity categoryEntity = categoryDao
                        .create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
        }
        return SpendJson.fromEntity(
                spendDao
                        .create(spendEntity)
        );
    }

    public void deleteSpendSpringJdbc(UUID spendId) {

       // SpendDao spendDao = new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl()));

        Optional<SpendEntity> spend = spendDao
                .findSpendById(spendId);

        if (spend.isPresent()) {
            spendDao.deleteSpend(spend.get());
        } else {
            throw new IllegalArgumentException("Spend для удаления не найден: " + spendId);
        }
    }


    public Optional<SpendJson> findSpendByIdSpringJdbc(UUID spendId) {

        Optional<SpendEntity> spend = spendDao
                .findSpendById(spendId);

        if (spend.isPresent()) {
            return Optional.of(SpendJson.fromEntity(spend.get()));
        } else {
            return Optional.empty();
        }
    }


    public List<SpendJson> findAllSpendByUserNameSpringJdbc(String username) {
        List<SpendEntity> spendsEntity = spendDao
                .findAllByUsername(username);

        return spendsEntity.stream()
                .map(SpendJson::fromEntity)
                .toList();

    }

    public List<SpendJson> findAllSpendsSpringJdbc() {
        List<SpendEntity> spendsEntity = spendDao
                .findAll();


        return spendsEntity.stream()
                .map(SpendJson::fromEntity)
                .toList();

    }


//    // CATEGORY JDBC
//
//    public CategoryJson createCategory(CategoryJson category) {
//        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
//
//                    return CategoryJson.fromEntity(
//                            new CategoryDaoJdbc(connection).create(categoryEntity)
//                    );
//                },
//                CFG.spendJdbcUrl()
//        );
//    }
//
//    public CategoryJson updateCategory(CategoryJson category) {
//        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);
//
//                    return CategoryJson.fromEntity(
//                            new CategoryDaoJdbc(connection).update(categoryEntity)
//                    );
//                },
//                CFG.spendJdbcUrl()
//        );
//    }
//
//    public void deleteCategory(UUID categoryId) {
//        transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    CategoryDao categoryDao = new CategoryDaoJdbc(connection);
//                    categoryDao.findCategoryById(categoryId)
//                            .ifPresentOrElse(category ->
//                                            categoryDao.deleteCategory(category),
//                                    () -> {
//                                        throw new IllegalArgumentException("Категория не найдена: " + categoryId);
//                                    });
//                    return null;
//                },
//                CFG.spendJdbcUrl());
//    }
//
//
//    public Optional<CategoryJson> findCategoryById(UUID categoryId) {
//        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    Optional<CategoryEntity> category =
//                            new CategoryDaoJdbc(connection).findCategoryById(categoryId);
//
//                    if (category.isPresent()) {
//                        return Optional.of(CategoryJson.fromEntity(category.get()));
//                    } else {
//                        return Optional.empty();
//                    }
//                },
//                CFG.spendJdbcUrl()
//        );
//    }
//
//    public Optional<CategoryJson> findCategoryByNameAndUserName(String username, String categoryName) {
//        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    Optional<CategoryEntity> category =
//                            new CategoryDaoJdbc(connection).findCategoryByUsernameAndCategoryName(username, categoryName);
//
//                    if (category.isPresent()) {
//                        return Optional.of(CategoryJson.fromEntity(category.get()));
//                    } else {
//                        return Optional.empty();
//                    }
//                },
//                CFG.spendJdbcUrl()
//        );
//    }
//
//    public List<CategoryJson> findAllCategoriesByUserName(String username) {
//        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    List<CategoryEntity> categoriesEntity =
//                            new CategoryDaoJdbc(connection).findAllByUsername(username);
//
//                    return categoriesEntity.stream()
//                            .map(CategoryJson::fromEntity)
//                            .toList();
//                },
//                CFG.spendJdbcUrl()
//        );
//    }
//
//    public List<CategoryJson> findAllCategory() {
//        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    List<CategoryEntity> categoriesEntity =
//                            new CategoryDaoJdbc(connection).findAll();
//
//                    return categoriesEntity.stream()
//                            .map(CategoryJson::fromEntity)
//                            .toList();
//                },
//                CFG.spendJdbcUrl()
//        );
//    }
//
//    // SPEND JDBC
//
//    public SpendJson createSpend(SpendJson spend) {
//        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    SpendEntity spendEntity = SpendEntity.fromJson(spend);
//
//                    if (spendEntity.getCategory().getId() == null) {
//
//                        Optional<CategoryEntity> existingCategory = new CategoryDaoJdbc(connection)
//                                .findCategoryByUsernameAndCategoryName(spend.category().username(), spend.category().name());
//                        if (existingCategory.isPresent()) {
//                            spendEntity.setCategory(existingCategory.get());
//                        } else {
//                            CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
//                                    .create(spendEntity.getCategory());
//                            spendEntity.setCategory(categoryEntity);
//                        }
//                    }
//                    return SpendJson.fromEntity(
//                            new SpendDaoJdbc(connection).create(spendEntity)
//                    );
//                },
//                CFG.spendJdbcUrl()
//        );
//    }
//
//
//    public void deleteSpend(UUID spendId) {
//        transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    SpendDao spendDao = new SpendDaoJdbc(connection);
//                    spendDao.findSpendById(spendId)
//                            .ifPresentOrElse(spend ->
//                                            spendDao.deleteSpend(spend),
//                                    () -> {
//                                        throw new IllegalArgumentException("Spend не найден: " + spendId);
//                                    });
//                    return null;
//                },
//                CFG.spendJdbcUrl());
//    }
//
//    public Optional<SpendJson> findSpendById(UUID spendId) {
//        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    Optional<SpendEntity> spend = new SpendDaoJdbc(connection).findSpendById(spendId);
//
//                    if (spend.isPresent()) {
//                        return Optional.of(SpendJson.fromEntity(spend.get()));
//                    } else {
//                        return Optional.empty();
//                    }
//                },
//                CFG.spendJdbcUrl()
//        );
//    }
//
//    public List<SpendJson> findAllByUsername(String username) {
//        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    List<SpendEntity> spendsEntity =
//                            new SpendDaoJdbc(connection).findAllByUsername(username);
//
//                    return spendsEntity.stream()
//                            .map(SpendJson::fromEntity)
//                            .toList();
//                },
//                CFG.spendJdbcUrl()
//        );
//    }
//
//    public List<SpendJson> findAllSpends() {
//        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
//                    List<SpendEntity> spendsEntity =
//                            new SpendDaoJdbc(connection).findAll();
//
//                    return spendsEntity.stream()
//                            .map(SpendJson::fromEntity)
//                            .toList();
//                },
//                CFG.spendJdbcUrl()
//        );
//    }

}