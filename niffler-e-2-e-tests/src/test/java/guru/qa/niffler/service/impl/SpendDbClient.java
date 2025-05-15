package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public class SpendDbClient {

    private static final Config CFG = Config.getInstance();

    private final SpendDao spendDao = new SpendDaoJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    // CATEGORY JDBC

    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> CategoryJson.fromEntity(
                categoryDao.create(CategoryEntity.fromJson(category)))
        );
    }

    public void updateCategory(CategoryJson category) {
        jdbcTxTemplate.execute(() ->
                CategoryJson.fromEntity(
                        categoryDao.update(CategoryEntity.fromJson(category)))
        );
    }

    public void deleteCategory(UUID categoryId) {
        jdbcTxTemplate.execute(() -> {
            Optional<CategoryEntity> category = categoryDao
                    .findCategoryById(categoryId);

            if (category.isPresent()) {
                categoryDao.delete(category.get());
            } else {
                throw new IllegalArgumentException("Категория для удаления не найдена: " + categoryId);
            }
            return null;
        });
    }

    public Optional<CategoryJson> findCategoryByNameAndUserName(String username, String categoryName) {
        return jdbcTxTemplate.execute(() -> {
            Optional<CategoryEntity> category = categoryDao
                    .findCategoryByUsernameAndCategoryName(username, categoryName);
            return category.map(CategoryJson::fromEntity);
        });
    }

    public Optional<CategoryJson> findCategoryById(UUID categoryId) {
        return jdbcTxTemplate.execute(() -> {
            Optional<CategoryEntity> category = categoryDao
                    .findCategoryById(categoryId);
            return category.map(CategoryJson::fromEntity);
        });
    }

    public List<CategoryJson> findAllCategoriesByUserName(String username) {
        return jdbcTxTemplate.execute(() -> {
            List<CategoryEntity> categories = categoryDao
                    .findAllByUsername(username);

            return categories.stream()
                    .map(CategoryJson::fromEntity)
                    .toList();
        });
    }

    public List<CategoryJson> findAllCategories() {
        return jdbcTxTemplate.execute(() -> {
            List<CategoryEntity> categories = categoryDao.findAll();

            return categories.stream()
                    .map(CategoryJson::fromEntity)
                    .toList();
        });
    }

    // SPEND JDBC

    public SpendJson createSpend(SpendJson spend) {
        return jdbcTxTemplate.execute(() -> {
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
                            spendDao.create(spendEntity)
                    );
                }
        );

    }

    public void deleteSpend(UUID spendId) {
        jdbcTxTemplate.execute(() -> {
            Optional<SpendEntity> spend = spendDao
                    .findSpendById(spendId);

            if (spend.isPresent()) {
                spendDao.delete(spend.get());
            } else {
                throw new IllegalArgumentException("Spend для удаления не найден: " + spendId);
            }
            return null;
        });
    }

    public Optional<SpendJson> findSpendById(UUID spendId) {
        return jdbcTxTemplate.execute(() -> {
            Optional<SpendEntity> spend = spendDao
                    .findSpendById(spendId);

            if (spend.isPresent()) {
                return Optional.of(SpendJson.fromEntity(spend.get()));
            } else {
                return Optional.empty();
            }
        });
    }


    public List<SpendJson> findAllSpendByUserName(String username) {
        return jdbcTxTemplate.execute(() -> {
            List<SpendEntity> spendsEntity = spendDao
                    .findAllByUsername(username);

            return spendsEntity.stream()
                    .map(SpendJson::fromEntity)
                    .toList();
        });
    }

    public List<SpendJson> findAllSpends() {
        return jdbcTxTemplate.execute(() -> {
            List<SpendEntity> spendsEntity = spendDao
                    .findAll();

            return spendsEntity.stream()
                    .map(SpendJson::fromEntity)
                    .toList();
        });
    }
}