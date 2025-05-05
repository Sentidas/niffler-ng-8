package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.CategoryDao;
import guru.qa.niffler.data.dao.SpendDao;
import guru.qa.niffler.data.dao.impl.jdbc.CategoryDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.SpendDaoJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.CategoryDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.SpendDaoSpringJdbc;
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
    private final SpendDao spendDaoSpring = new SpendDaoSpringJdbc();
    private final CategoryDao categoryDao = new CategoryDaoJdbc();
    private final CategoryDao categoryDaoSpring = new CategoryDaoSpringJdbc();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
          CFG.spendJdbcUrl()
    );

    // CATEGORY SPRING JDBC

    public CategoryJson createCategorySpringJdbc(CategoryJson category) {
        return CategoryJson.fromEntity(
                categoryDaoSpring
                        .create(
                                CategoryEntity.fromJson(category)
                        )
        );
    }

    public void updateCategorySpringJdbc(CategoryJson category) {
        CategoryJson.fromEntity(
                categoryDaoSpring
                        .update(
                                CategoryEntity.fromJson(category)
                        )
        );
    }

    public void deleteCategorySpringJdbc(UUID categoryId) {

        //CategoryDao categoryDao = new CategoryDaoSpringJdbc(dataSource(CFG.spendJdbcUrl()));

        Optional<CategoryEntity> category = categoryDaoSpring
                .findCategoryById(categoryId);

        if (category.isPresent()) {
            categoryDaoSpring.deleteCategory(category.get());
        } else {
            throw new IllegalArgumentException("Категория для удаления не найдена: " + categoryId);
        }
    }

    public Optional<CategoryJson> findCategoryByNameAndUserNameSpringJdbc(String username, String categoryName) {

        Optional<CategoryEntity> category = categoryDaoSpring
                .findCategoryByUsernameAndCategoryName(username, categoryName);

        // return category.map(CategoryJson::fromEntity);

        if (category.isPresent()) {
            return Optional.of(CategoryJson.fromEntity(category.get()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<CategoryJson> findCategoryByIdSpringJdbc(UUID categoryId) {

        Optional<CategoryEntity> category = categoryDaoSpring
                .findCategoryById(categoryId);

        if (category.isPresent()) {
            return Optional.of(CategoryJson.fromEntity(category.get()));
        } else {
            return Optional.empty();
        }
    }

    public List<CategoryJson> findAllCategoriesByUserNameSpringJdbc(String username) {

        List<CategoryEntity> categories = categoryDaoSpring
                .findAllByUsername(username);

        return categories.stream()
                .map(CategoryJson::fromEntity)
                .toList();
    }

    public List<CategoryJson> findAllCategoriesSpringJdbc() {
        List<CategoryEntity> categories = categoryDaoSpring
                .findAll();

        return categories.stream()
                .map(CategoryJson::fromEntity)
                .toList();

    }

    // SPEND SPRING JDBC

    public SpendJson createSpendSpringJdbc(SpendJson spend) {
        SpendEntity spendEntity = SpendEntity.fromJson(spend);

        if (spendEntity.getCategory().getId() == null) {

            Optional<CategoryEntity> existingCategory = categoryDaoSpring
                    .findCategoryByUsernameAndCategoryName(spend.category().username(), spend.category().name());
            if (existingCategory.isPresent()) {
                spendEntity.setCategory(existingCategory.get());
            } else {
                CategoryEntity categoryEntity = categoryDaoSpring
                        .create(spendEntity.getCategory());
                spendEntity.setCategory(categoryEntity);
            }
        }
        return SpendJson.fromEntity(
                spendDaoSpring
                        .create(spendEntity)
        );
    }

    public void deleteSpendSpringJdbc(UUID spendId) {

       // SpendDao spendDao = new SpendDaoSpringJdbc(dataSource(CFG.spendJdbcUrl()));

        Optional<SpendEntity> spend = spendDaoSpring
                .findSpendById(spendId);

        if (spend.isPresent()) {
            spendDao.deleteSpend(spend.get());
        } else {
            throw new IllegalArgumentException("Spend для удаления не найден: " + spendId);
        }
    }


    public Optional<SpendJson> findSpendByIdSpringJdbc(UUID spendId) {

        Optional<SpendEntity> spend = spendDaoSpring
                .findSpendById(spendId);

        if (spend.isPresent()) {
            return Optional.of(SpendJson.fromEntity(spend.get()));
        } else {
            return Optional.empty();
        }
    }


    public List<SpendJson> findAllSpendByUserNameSpringJdbc(String username) {
        List<SpendEntity> spendsEntity = spendDaoSpring
                .findAllByUsername(username);

        return spendsEntity.stream()
                .map(SpendJson::fromEntity)
                .toList();

    }

    public List<SpendJson> findAllSpendsSpringJdbc() {
        List<SpendEntity> spendsEntity = spendDaoSpring
                .findAll();


        return spendsEntity.stream()
                .map(SpendJson::fromEntity)
                .toList();

    }


    // CATEGORY JDBC

    public CategoryJson createCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);

                    return CategoryJson.fromEntity(
                            categoryDao.create(categoryEntity)
                    );
                }
        );
    }

    public CategoryJson updateCategory(CategoryJson category) {
        return jdbcTxTemplate.execute(() -> {
                    CategoryEntity categoryEntity = CategoryEntity.fromJson(category);

                    return CategoryJson.fromEntity(
                            categoryDao.update(categoryEntity)
                    );
                }
        );
    }

    public void deleteCategory(UUID categoryId) {
         jdbcTxTemplate.execute(() -> {
                    categoryDao.findCategoryById(categoryId)
                            .ifPresentOrElse(category ->
                                            categoryDao.deleteCategory(category),
                                    () -> {
                                        throw new IllegalArgumentException("Категория не найдена: " + categoryId);
                                    });
                    return null;
                }
         );
    }


    public Optional<CategoryJson> findCategoryById(UUID categoryId) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<CategoryEntity> category =
                            categoryDao.findCategoryById(categoryId);

                    if (category.isPresent()) {
                        return Optional.of(CategoryJson.fromEntity(category.get()));
                    } else {
                        return Optional.empty();
                    }
                }
        );
    }

    public Optional<CategoryJson> findCategoryByNameAndUserName(String username, String categoryName) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<CategoryEntity> category =
                            categoryDao.findCategoryByUsernameAndCategoryName(username, categoryName);

                    if (category.isPresent()) {
                        return Optional.of(CategoryJson.fromEntity(category.get()));
                    } else {
                        return Optional.empty();
                    }
                }
        );
    }

    public List<CategoryJson> findAllCategoriesByUserName(String username) {
        return jdbcTxTemplate.execute(() -> {
                    List<CategoryEntity> categoriesEntity =
                            categoryDao.findAllByUsername(username);

                    return categoriesEntity.stream()
                            .map(CategoryJson::fromEntity)
                            .toList();
                }
        );
    }

    public List<CategoryJson> findAllCategory() {
        return jdbcTxTemplate.execute(() -> {
                    List<CategoryEntity> categoriesEntity =
                            categoryDao.findAll();

                    return categoriesEntity.stream()
                            .map(CategoryJson::fromEntity)
                            .toList();
                }
        );
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
                    spendDao.findSpendById(spendId)
                            .ifPresentOrElse(spend ->
                                            spendDao.deleteSpend(spend),
                                    () -> {
                                        throw new IllegalArgumentException("Spend не найден: " + spendId);
                                    });
                    return null;
                }
        );

    }

    public Optional<SpendJson> findSpendById(UUID spendId) {
        return jdbcTxTemplate.execute(() -> {
                    Optional<SpendEntity> spend = spendDao.findSpendById(spendId);

                    if (spend.isPresent()) {
                        return Optional.of(SpendJson.fromEntity(spend.get()));
                    } else {
                        return Optional.empty();
                    }
                }
        );
    }

    public List<SpendJson> findAllByUsername(String username) {
        return jdbcTxTemplate.execute(() -> {
                    List<SpendEntity> spendsEntity =
                            spendDao.findAllByUsername(username);

                    return spendsEntity.stream()
                            .map(SpendJson::fromEntity)
                            .toList();
                }
        );
    }

    public List<SpendJson> findAllSpends() {
       return jdbcTxTemplate.execute(() -> {
                    List<SpendEntity> spendsEntity =
                            spendDao.findAll();

                    return spendsEntity.stream()
                            .map(SpendJson::fromEntity)
                            .toList();
                }
        );
    }
}