package guru.qa.niffler.data.repository;

import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;

import java.util.Optional;
import java.util.UUID;

public interface SpendRepository {

    SpendEntity create(SpendEntity spend);

    SpendEntity update(SpendEntity spend);

    CategoryEntity updateCategory(CategoryEntity category);

    CategoryEntity createCategory(CategoryEntity category);

    Optional<CategoryEntity> findCategoryById(UUID id);

    Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String categoryName);

    Optional<SpendEntity> findById(UUID id);

    Optional<SpendEntity> findByUsernameAndDescription(String username, String description);

    void remove(SpendEntity spend);

    void removeCategory(CategoryEntity category);

}



