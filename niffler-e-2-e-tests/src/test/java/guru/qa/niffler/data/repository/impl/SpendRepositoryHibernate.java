package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.repository.SpendRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jpa.EntityManagers.em;

public class SpendRepositoryHibernate implements SpendRepository {

    private static final Config CFG = Config.getInstance();

    private final EntityManager entityManager = em(CFG.spendJdbcUrl());

    @Override
    public SpendEntity create(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.persist(spend);
        return spend;
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        entityManager.joinTransaction();
        entityManager.merge(spend);
        return spend;
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.merge(category);
        return category;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        entityManager.persist(category);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(CategoryEntity.class, id));
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String categoryName) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT u FROM CategoryEntity u WHERE u.username= :username AND u.name= :name", CategoryEntity.class)
                            .setParameter("username", username)
                            .setParameter("name", categoryName)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        return Optional.ofNullable(
                entityManager.find(SpendEntity.class, id));
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndDescription(String username, String description) {
        try {
            return Optional.of(
                    entityManager.createQuery("SELECT u FROM SpendEntity u WHERE u.username= :username AND u.description= :description", SpendEntity.class)
                            .setParameter("username", username)
                            .setParameter("description", description)
                            .getSingleResult()
            );
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public void remove(SpendEntity spend) {
        entityManager.joinTransaction();

        SpendEntity removableSpend = entityManager.find(SpendEntity.class, spend.getId());

        if (removableSpend != null) {
            CategoryEntity category = removableSpend.getCategory();

            if (category != null) {
                category.getSpends().remove(removableSpend);
                removableSpend.setCategory(null);
            }
            entityManager.remove(removableSpend);
        }
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        entityManager.joinTransaction();
        CategoryEntity removableCategory = entityManager.find(CategoryEntity.class, category.getId());
        if(removableCategory !=null) {
            entityManager.remove(removableCategory);
        }
    }
}
