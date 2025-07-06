package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.extractor.AuthUserResultSetExtractor;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.jdbc.DataSources;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class AuthUserRepositorySpringJdbc implements AuthUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public AuthUserEntity create(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement userPs = con.prepareStatement(
                    "INSERT INTO \"user\" (username, password, enabled, account_non_expired, account_non_locked, credentials_non_expired) " +
                            "VALUES(?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            userPs.setString(1, user.getUsername());
            userPs.setString(2, user.getPassword());
            userPs.setBoolean(3, user.getEnabled());
            userPs.setBoolean(4, user.getAccountNonExpired());
            userPs.setBoolean(5, user.getAccountNonLocked());
            userPs.setBoolean(6, user.getCredentialsNonExpired());
            return userPs;
        }, kh);
        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);

        List<AuthorityEntity> authorities = user.getAuthorities();
        System.out.println("Роли до вставки: " + authorities);

        jdbcTemplate.batchUpdate(
                "INSERT INTO authority (user_id, authority) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setObject(1, generatedKey);
                        ps.setObject(2, authorities.get(i).getAuthority().name());
                    }

                    @Override
                    public int getBatchSize() {
                        return authorities.size();
                    }
                }
        );
        return user;
    }

    @Override
    public AuthUserEntity update(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "UPDATE \"user\" SET username = ?, password = ?, enabled = ?, account_non_expired = ?, " +
                            "account_non_locked = ?, credentials_non_expired = ? WHERE id = ?");

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setBoolean(3, user.getEnabled());
            ps.setBoolean(4, user.getAccountNonExpired());
            ps.setBoolean(5, user.getAccountNonLocked());
            ps.setBoolean(6, user.getCredentialsNonExpired());
            ps.setObject(7, user.getId());
            return ps;
        });
        return user;
    }

    @Override
    public Optional<AuthUserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                    SELECT
                                        u.id AS user_id,
                                        u.username,
                                        u.password,
                                        u.enabled,
                                        u.account_non_expired,
                                        u.account_non_locked,
                                        u.credentials_non_expired,
                                        a.id AS authority_id,
                                        a.authority
                                    FROM "user" u
                                    JOIN authority a ON u.id = a.user_id
                                    WHERE u.id = ?
                                """,
                        AuthUserResultSetExtractor.instance,
                        id
                )
        );
    }

    @Override
    public Optional<AuthUserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.query(
                        """
                                SELECT 
                                    u.id AS user_id,
                                    u.username,
                                    u.password,
                                    u.enabled,
                                    u.account_non_expired,
                                    u.account_non_locked,
                                    u.credentials_non_expired,
                                    a.id AS authority_id,
                                    a.authority
                                FROM "user" u
                                JOIN authority a ON u.id = a.user_id
                                WHERE u.username = ?
                                """,
                        AuthUserResultSetExtractor.instance,
                        username
                )
        );
    }

    @Override
    public void remove(AuthUserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.authJdbcUrl()));
        int rolesDeleted = jdbcTemplate.update("DELETE FROM authority WHERE user_id = ?", user.getId());
        int usersDeleted = jdbcTemplate.update("DELETE FROM \"user\" WHERE id = ?", user.getId());
        System.out.println("Удалено ролей из auth.authority: " + rolesDeleted + " строки");
        System.out.println("Удалено пользователей из auth.user: " + usersDeleted + " строка");
    }
}
