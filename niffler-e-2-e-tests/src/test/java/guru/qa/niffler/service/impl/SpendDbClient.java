package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.repository.impl.SpendRepositoryHibernate;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.service.SpendClient;
import io.qameta.allure.Step;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.UUID;


public class SpendDbClient implements SpendClient {

    private static final Config CFG = Config.getInstance();

    private final SpendRepository spendRepository = new SpendRepositoryHibernate();

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.spendJdbcUrl()
    );

    @Override
    @Step("Create spend using SQL")
    public SpendJson createSpend(SpendJson spend) {
        return xaTxTemplate.execute(() -> {
                    SpendEntity spendEntity = SpendEntity.fromJson(spend);

                    if (spendEntity.getCategory().getId() == null) {

                        Optional<CategoryEntity> existingCategory = spendRepository
                                .findCategoryByUsernameAndName(spend.category().username(), spend.category().name());

                        if (existingCategory.isPresent()) {
                            spendEntity.setCategory(existingCategory.get());
                        } else {
                            CategoryEntity categoryEntity = spendRepository
                                    .createCategory(spendEntity.getCategory());
                            spendEntity.setCategory(categoryEntity);
                        }
                    }
                    return SpendJson.fromEntity(
                            spendRepository.create(spendEntity)
                    );
                }
        );

    }

    @Override
    @Step("Create category using SQL")
    public CategoryJson createCategory(CategoryJson category) {
        return xaTxTemplate.execute(() -> CategoryJson.fromEntity(
                spendRepository.createCategory(CategoryEntity.fromJson(category)))
        );
    }


    @Override
    @Step("Update spend using SQL")
    public SpendJson updateSpend(SpendJson updateSpend) {
        return xaTxTemplate.execute(() -> {

            UUID spendId = updateSpend.id();

            SpendEntity spendEntity = spendRepository.findById(spendId)
                    .orElseThrow(() -> new IllegalStateException("Spend not found: " + spendId));

            if (updateSpend.username() != null) {
                spendEntity.setUsername(updateSpend.username());
            }
            if (updateSpend.description() != null) {
                spendEntity.setDescription(updateSpend.description());
            }
            if (updateSpend.amount() != null) {
                spendEntity.setAmount(updateSpend.amount());
            }
            if (updateSpend.currency() != null) {
                spendEntity.setCurrency(updateSpend.currency());
            }

            if (updateSpend.category() != null && updateSpend.category().id() != null) {
                // 🔍 находим CategoryEntity, а не SpendEntity!
                CategoryEntity category = spendRepository.findCategoryById(updateSpend.category().id())
                        .orElseThrow(() -> new IllegalStateException("Category not found: " + updateSpend.category().id()));
                spendEntity.setCategory(category); // ✅ сюда передаём CategoryEntity
            }

            spendRepository.update(spendEntity);
            return SpendJson.fromEntity(spendEntity);
        });
    }

    @Override
    @Step("Update category using SQL")
    public CategoryJson updateCategory(CategoryJson updateCategory) {
        return xaTxTemplate.execute(() -> {

            UUID categoryId = updateCategory.id();

            CategoryEntity categoryEntity = spendRepository.findCategoryById(categoryId)
                    .orElseThrow(() -> new IllegalStateException("Category not found: " + categoryId));

            if (updateCategory.name() != null) {
                categoryEntity.setName(updateCategory.name());
            }

            if (updateCategory.username() != null) {
                categoryEntity.setUsername(updateCategory.username());
            }
            if (updateCategory.archived() != null) {
                categoryEntity.setArchived(updateCategory.archived());
            }

            spendRepository.updateCategory(categoryEntity);

            return CategoryJson.fromEntity(categoryEntity);

        });
    }

    @Override
    @Step("Delete category using SQL")
    public Optional<CategoryJson> removeCategory(CategoryJson categoryJson) {
        xaTxTemplate.execute(() -> {
            Optional<CategoryEntity> category = spendRepository
                    .findCategoryById(categoryJson.id());

            if (category.isPresent()) {
                spendRepository.removeCategory(category.get());
            } else {
                throw new IllegalArgumentException("Категория для удаления не найдена: " + categoryJson.id());
            }
            return null;
        });
        return null;
    }

    @Override
    @Step("Delete spend using SQL")
    public void removeSpend(SpendJson spendJson) {
        xaTxTemplate.execute(() -> {
            Optional<SpendEntity> spend = spendRepository
                    .findById(spendJson.id());

            if (spend.isPresent()) {
                spendRepository.remove(spend.get());
            } else {
                throw new IllegalArgumentException("Spend для удаления не найден: " + spendJson.id());
            }
            return null;
        });
    }

    @Override
    @Step("Get category '{1}' using SQL")
    public Optional<CategoryJson> findCategoryByUsernameAndSpendName(String username, String name) {
        return xaTxTemplate.execute(() -> {
            Optional<CategoryEntity> category = spendRepository
                    .findCategoryByUsernameAndName(username, name);
            return category.map(CategoryJson::fromEntity);
        });
    }

    @Override
    @Step("Get category by id using SQL")
    public Optional<CategoryJson> findCategoryById(UUID categoryId) {
        return xaTxTemplate.execute(() -> {
            Optional<CategoryEntity> category = spendRepository
                    .findCategoryById(categoryId);
            return category.map(CategoryJson::fromEntity);
        });
    }


    @Override
    @Step("Get spend by id using SQL")
    public Optional<SpendJson> findSpendByIdAndUsername(UUID spendId, String username) {
        return xaTxTemplate.execute(() -> {
            Optional<SpendEntity> spend = spendRepository
                    .findById(spendId);

            if (spend.isPresent()) {
                return Optional.of(SpendJson.fromEntity(spend.get()));
            } else {
                return Optional.empty();
            }
        });
    }

    @Override
    @Step("Get spend by description using SQL")
    public Optional<SpendJson> findByUsernameAndDescription(String username, String description) {
        return xaTxTemplate.execute(() -> {
            Optional<SpendEntity> spend = spendRepository
                    .findByUsernameAndDescription(username, description);

            if (spend.isPresent()) {
                return Optional.of(SpendJson.fromEntity(spend.get()));
            } else {
                return Optional.empty();
            }
        });
    }
}