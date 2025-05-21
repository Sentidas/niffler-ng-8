package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.spend.CategoryEntity;
import guru.qa.niffler.data.entity.spend.SpendEntity;
import guru.qa.niffler.data.extractor.SpendResultSetExtractor;
import guru.qa.niffler.data.mapper.SpendAndCategoryEntityRowMapper;
import guru.qa.niffler.data.repository.SpendRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.Optional;
import java.util.UUID;

public class SpendRepositorySpringJdbc implements SpendRepository {

    private static final Config CFG = Config.getInstance();


    @Override
    public SpendEntity create(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO spend (username, spend_date, currency, amount, description, category_id) " +
                            "VALUES ( ?, ?, ?, ?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            return ps;
        }, kh);
        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        spend.setId(generatedKey);
        return spend;
    }

    @Override
    public SpendEntity update(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE spend SET username = ?, spend_date = ?, currency = ?, amount = ?, description = ?, category_id = ? " +
                            "WHERE id = ?");

            ps.setString(1, spend.getUsername());
            ps.setDate(2, new java.sql.Date(spend.getSpendDate().getTime()));
            ps.setString(3, spend.getCurrency().name());
            ps.setDouble(4, spend.getAmount());
            ps.setString(5, spend.getDescription());
            ps.setObject(6, spend.getCategory().getId());
            ps.setObject(7, spend.getId());
            return ps;
        });
        return spend;
    }

    @Override
    public CategoryEntity updateCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE category SET username = ?, name = ? , archived = ? " +
                            " WHERE id = ? ");

            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());
            ps.setObject(4, category.getId());
            return ps;
        });
        return category;
    }

    @Override
    public CategoryEntity createCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO category (username, name, archived) " +
                            "VALUES (?, ?, ?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            ps.setString(1, category.getUsername());
            ps.setString(2, category.getName());
            ps.setBoolean(3, category.isArchived());
            return ps;
        }, kh);
        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        category.setId(generatedKey);
        return category;
    }

    @Override
    public Optional<CategoryEntity> findCategoryById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (" +
                        "SELECT 1 FROM category  " +
                        "WHERE id = ?)",
                Boolean.class,
                id
        );

        if (Boolean.FALSE.equals(exists)) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT " +
                                "s.id AS spend_id, s.username AS spend_username, s.spend_date, s.currency, s.amount, s.description, s.category_id AS spend_category_id, " +
                                "c.id AS category_id, c.name AS category_name, c.username AS category_username, c.archived " +
                                "FROM category c " +
                                "LEFT JOIN spend s ON c.id = s.category_id " +
                                "WHERE c.id = ?",
                        SpendResultSetExtractor.instance,
                        id
                )
        );
    }

    @Override
    public Optional<CategoryEntity> findCategoryByUsernameAndName(String username, String categoryName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        Boolean exists = jdbcTemplate.queryForObject(
                "SELECT EXISTS (" +
                        "SELECT 1 FROM category " +
                        "WHERE username = ? AND name = ?)",
                Boolean.class,
                username,
                categoryName
        );

        if (Boolean.FALSE.equals(exists)) {
            return Optional.empty();
        }

        return Optional.ofNullable(
                jdbcTemplate.query(
                        "SELECT " +
                                "s.id AS spend_id, s.username AS spend_username, s.spend_date, s.currency, s.amount, s.description, s.category_id AS spend_category_id, " +
                                "c.id AS category_id, c.name AS category_name, c.username AS category_username, c.archived " +
                                "FROM category c " +
                                "LEFT JOIN spend s ON c.id = s.category_id " +
                                "WHERE c.username = ? AND c.name = ?",
                        SpendResultSetExtractor.instance,
                        username,
                        categoryName
                )
        );
    }

    @Override
    public Optional<SpendEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT " +
                                "s.id AS spend_id, s.username AS spend_username, s.spend_date, s.currency, s.amount, s.description, s.category_id AS spend_category_id, " +
                                "c.id AS category_id, c.name AS category_name, c.username AS category_username, c.archived " +
                                "FROM spend s " +
                                "JOIN category c ON s.category_id = c.id " +
                                "WHERE s.id = ?",
                        SpendAndCategoryEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public Optional<SpendEntity> findByUsernameAndDescription(String username, String description) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT " +
                                "s.id AS spend_id, s.username AS spend_username, s.spend_date, s.currency, s.amount, s.description, s.category_id AS spend_category_id, " +
                                "c.id AS category_id, c.name AS category_name, c.username AS category_username, c.archived " +
                                "FROM spend s " +
                                "JOIN category c ON s.category_id = c.id " +
                                "WHERE s.username = ? AND s.description = ?",
                        SpendAndCategoryEntityRowMapper.instance,
                        username,
                        description
                )
        );
    }

    @Override
    public void remove(SpendEntity spend) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update("DELETE FROM spend WHERE id = ?", spend.getId());
    }

    @Override
    public void removeCategory(CategoryEntity category) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.spendJdbcUrl()));
        jdbcTemplate.update("DELETE FROM spend WHERE spend.category_id = ?", category.getId());
        jdbcTemplate.update("DELETE FROM category WHERE id = ?", category.getId());

    }
}
