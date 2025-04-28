package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.CategoryDaoJdbc;

import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;


public class SpendDbClient {

    private static final Config CFG = Config.getInstance();


    public SpendJson createSpend(SpendJson spend) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);

                    if (spendEntity.getCategory().getId() == null) {

                        Optional<CategoryEntity> existingCategory = new CategoryDaoJdbc(connection)
                                .findCategoryByUsernameAndCategoryName(spend.category().username(), spend.category().name());
                        if (existingCategory.isPresent()) {
                            spendEntity.setCategory(existingCategory.get());
                        } else {
                            CategoryEntity categoryEntity = new CategoryDaoJdbc(connection)
                                    .create(spendEntity.getCategory());
                            spendEntity.setCategory(categoryEntity);
                        }
                    }
                    return SpendJson.fromEntity(
                            new SpendDaoJdbc(connection).create(spendEntity)
                    );
                },
                CFG.spendJdbcUrl()
        );
    }

    public Optional<SpendJson> findSpendById(UUID spendId) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    Optional<SpendEntity> spend = new SpendDaoJdbc(connection).findSpendById(spendId);

                    if (spend.isPresent()) {
                        return Optional.of(SpendJson.fromEntity(spend.get()));
                    } else {
                        return Optional.empty();
                    }
                },
                CFG.spendJdbcUrl()
        );
    }

    public List<SpendJson> findAllByUsername(String username) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    List<SpendEntity> spendsEntity =
                            new SpendDaoJdbc(connection).findAllByUsername(username);

                    List<SpendJson> spends = new ArrayList<>();
                    for (SpendEntity spend : spendsEntity) {
                        spends.add(SpendJson.fromEntity(spend));
                    }
                    return spends;
                },
                CFG.spendJdbcUrl()
        );
    }

    public void deleteSpend(UUID spendId) {
        transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    SpendDao spendDao = new SpendDaoJdbc(connection);
                    spendDao.findSpendById(spendId)
                            .ifPresentOrElse(spend ->
                                            spendDao.deleteSpend(spend),
                                    () -> {
                                        throw new IllegalArgumentException("Spend не найден: " + spendId);
                                    });
                    return null;
                },
                CFG.spendJdbcUrl());
    }

    public CategoryJson createCategory(CategoryJson category) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);

                    return CategoryJson.fromEntity(
                            new CategoryDaoJdbc(connection).create(categoryEntity)
                    );
                },
                CFG.spendJdbcUrl()
        );
    }

    public Optional<CategoryJson> findCategoryById(UUID categoryId) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    Optional<CategoryEntity> category =
                            new CategoryDaoJdbc(connection).findCategoryById(categoryId);

                    if (category.isPresent()) {
                        return Optional.of(CategoryJson.fromEntity(category.get()));
                    } else {
                        return Optional.empty();
                    }
                },
                CFG.spendJdbcUrl()
        );
    }

    public Optional<CategoryJson> findCategoryByNameAndUserName(String username, String categoryName) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    Optional<CategoryEntity> category =
                            new CategoryDaoJdbc(connection).findCategoryByUsernameAndCategoryName(username, categoryName);

                    if (category.isPresent()) {
                        return Optional.of(CategoryJson.fromEntity(category.get()));
                    } else {
                        return Optional.empty();
                    }
                },
                CFG.spendJdbcUrl()
        );
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);

                    return CategoryJson.fromEntity(
                            new CategoryDaoJdbc(connection).updateCategory(categoryEntity)
                    );
                },
                CFG.spendJdbcUrl()
        );
    }

    public void deleteCategory(UUID categoryId) {
        transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    CategoryDao categoryDao = new CategoryDaoJdbc(connection);
                    categoryDao.findCategoryById(categoryId)
                            .ifPresentOrElse(category ->
                                            categoryDao.deleteCategory(category),
                                    () -> {
                                        throw new IllegalArgumentException("Категория не найдена: " + categoryId);
                                    });
                    return null;
                },
                CFG.spendJdbcUrl());
    }

    public List<CategoryJson> findAllCategoryByUserName(String username) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    List<CategoryEntity> categoriesEntity =
                            new CategoryDaoJdbc(connection).findAllByUsername(username);

                    List<CategoryJson> categories = new ArrayList<>();
                    for (CategoryEntity category : categoriesEntity) {
                        categories.add(CategoryJson.fromEntity(category));
                    }
                    return categories;
                },
                CFG.spendJdbcUrl()
        );
    }
}