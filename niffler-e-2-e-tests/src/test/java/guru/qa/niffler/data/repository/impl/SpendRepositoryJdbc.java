package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.mapper.CategoryEntityRowMapper;
import guru.qa.niffler.data.mapper.SpendEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.Connections.holder;

public class SpendRepositoryJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();


    @Override
    public SpendEntity create(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                        "VALUES ( ?, ?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            spend.setId(generatedKey);
            return spend;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {

        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT " +
                        "s.id AS spend_id, s.username AS spend_username, s.spend_date, s.currency, s.amount, s.description, s.category_id AS spend_category_id, " +
                        "c.id AS category_id, c.name AS category_name, c.username AS category_username, c.archived " +
                        "FROM spend s " +
                        "JOIN category c ON s.category_id = c.id " +
                        "WHERE s.id = ?"
        )) {
            ps.setObject(1, id);

            ps.execute();

            SpendEntity se;
            CategoryEntity ce;
            try (ResultSet rs = ps.getResultSet()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                ce = CategoryEntityRowMapper.instance.mapRow(rs, 1);
                se = SpendEntityRowMapper.instance.mapRow(rs, 1);

                se.setCategory(ce);

                return Optional.of(se);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndDescription(String username, String description) {

        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT " +
                        "s.id AS spend_id, s.username AS spend_username, s.spend_date, s.currency, s.amount, s.description, s.category_id AS spend_category_id, " +
                        "c.id AS category_id, c.name AS category_name, c.username AS category_username, c.archived " +
                        "FROM spend s " +
                        "JOIN category c ON s.category_id = c.id " +
                        "WHERE s.username = ? AND s.description = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, description);

            ps.execute();

            SpendEntity se;
            CategoryEntity ce;
            try (ResultSet rs = ps.getResultSet()) {
                if (!rs.next()) {
                    return Optional.empty();
                }
                ce = CategoryEntityRowMapper.instance.mapRow(rs, 1);
                se = SpendEntityRowMapper.instance.mapRow(rs, 1);

                se.setCategory(ce);

                return Optional.of(se);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(SpendEntity spend) {

        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM spend WHERE id = ?"
        )) {
            ps.setObject(1, spend.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public SpendEntity update(SpendEntity spend) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            ps.setObject(7, spend.getId());

            int rowsUpdate = ps.executeUpdate();
            if (rowsUpdate == 0) {
                throw new IllegalStateException("Spend не найден с ID:" + spend.getId());
            }
            System.out.println("Обновлено в spend '" + rowsUpdate + "' строка");

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении spend", e);
        }
        return spend;
    }


    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "UPDATE category SET name = ?, username = ?, archived = ? " +
                        "WHERE id = ?"
        )) {
            ps.setString(1, category.getName());
            ps.setString(2, category.getUsername());
            ps.setBoolean(3, category.isArchived());
            ps.setObject(4, category.getId());


            int rowsUpdate = ps.executeUpdate();
            if (rowsUpdate == 0) {
                throw new IllegalStateException("Категория не найдена с ID:" + category.getId());
            }
            System.out.println("Обновлено в category '" + rowsUpdate + "' строка");

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении category", e);
        }
        return category;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {

        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "INSERT INTO category (username, name, archived) " +
                        "VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            category.setId(generatedKey);
            return category;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {

        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT " +
                        "c.id AS category_id, c.name AS category_name, c.username AS category_username, c.archived, " +
                        "s.id AS spend_id, s.username AS spend_username, s.spend_date AS spend_date, s.currency, " +
                        "s.amount, s.description, s.category_id AS spend_category_id " +
                        "FROM category c  " +
                        "LEFT JOIN spend s ON c.id = s.category_id " +
                        "WHERE c.id = ?"
        )) {
            ps.setObject(1, id);

            ps.execute();

            CategoryEntity ce = null;
            List<SpendEntity> spends = new ArrayList<>();
            SpendEntity se;
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    if (ce == null) {
                        ce = CategoryEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    String spendId = rs.getString("spend_id");
                    if (spendId != null) {
                        se = SpendEntityRowMapper.instance.mapRow(rs, 1);
                        se.setCategory(ce);
                        spends.add(se);
                    }
                }
                if (ce == null) {
                    return Optional.empty();
                } else {
                    ce.setSpends(spends);
                    return Optional.of(ce);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String categoryName) {
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "SELECT " +
                        "c.id AS category_id, c.name AS category_name, c.username AS category_username, c.archived, " +
                        "s.id AS spend_id, s.username AS spend_username, s.spend_date AS spend_date, s.currency, " +
                        "s.amount, s.description, s.category_id AS spend_category_id " +
                        "FROM category c  " +
                        "LEFT JOIN spend s ON c.id = s.category_id " +
                        "WHERE c.username = ? AND c.name = ?"
        )) {
            ps.setString(1, username);
            ps.setString(2, categoryName);

            ps.execute();

            CategoryEntity ce = null;
            SpendEntity se = null;
            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    if (ce == null) {
                        ce = CategoryEntityRowMapper.instance.mapRow(rs, 1);
                    }
                    se = SpendEntityRowMapper.instance.mapRow(rs, 1);
                    se.setCategory(ce);
                }
                if (ce == null) {
                    return Optional.empty();
                } else {
                    return Optional.of(ce);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeCategory(CategoryEntity category) {

        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM spend WHERE spend.category_id = ?"
        )) {
            ps.setObject(1, category.getId());

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении spend", e);
        }
        try (PreparedStatement ps = holder(CFG.spendJdbcUrl()).connection().prepareStatement(
                "DELETE FROM category WHERE id = ?"
        )) {
            ps.setObject(1, category.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при удалении category", e);
        }
    }
}
