package guru.qa.niffler.data.dao.impl.jdbc;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.spend.CategoryEntity;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthAuthorityDaoJdbc implements AuthAuthorityDao {

    private static final Config CFG = Config.getInstance();

    private final Connection connection;

    public AuthAuthorityDaoJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public AuthorityEntity create(AuthorityEntity authority) {
        try (PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO authority (user_id, authority) " +
                        "VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {

            ps.setObject(1, authority.getUserId().getId());
            ps.setString(2, authority.getAuthority().name());


            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            authority.setId(generatedKey);
            return authority;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(AuthorityEntity... authority) {
        List<CategoryEntity> categories = new ArrayList<>();
    }

    @Override
    public List<AuthorityEntity> findAll() {
        List<AuthorityEntity> authorities = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT * FROM authority"
        )) {

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                while (rs.next()) {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setId(rs.getObject("id", UUID.class));
                    ae.setAuthority(Authority.valueOf(rs.getString("authority")));

                    UUID userId = rs.getObject("user_id", UUID.class);

                    AuthUserEntity resultUser = new AuthUserEntity();
                    resultUser.setId(userId);
                    ae.setUserId(resultUser);
                    authorities.add(ae);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return authorities;
    }

    @Override
    public void delete(UUID userId) {
        try (PreparedStatement ps = connection.prepareStatement(
                "DELETE FROM authority WHERE user_id = ?"
        )) {
            ps.setObject(1, userId);

            int rowDeleted = ps.executeUpdate();
            System.out.println("Удалено из auth.authority '" + rowDeleted + "' строк");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

