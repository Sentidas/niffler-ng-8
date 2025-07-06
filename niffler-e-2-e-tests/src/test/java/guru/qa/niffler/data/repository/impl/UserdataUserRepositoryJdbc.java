package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.model.spend.CurrencyValues;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.jdbc.Connections.holder;

public class UserdataUserRepositoryJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();


    @Override
    public UserEntity create(UserEntity user) {

        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO \"user\" (username,  currency) " +
                        "VALUES ( ?, ?)",
                Statement.RETURN_GENERATED_KEYS
        )) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());

            ps.executeUpdate();

            final UUID generatedKey;
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    generatedKey = rs.getObject("id", UUID.class);
                } else {
                    throw new SQLException("Can`t find id in ResultSet");
                }
            }
            user.setId(generatedKey);
            return user;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public Optional<UserEntity> findById(UUID id) {

        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE id = ?"
        )) {
            ps.setObject(1, id);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity ue = new UserEntity();
                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUsername(rs.getString("username"));
                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    ue.setFirstname(rs.getString("firstname"));
                    ue.setSurname(rs.getString("surname"));
                    ue.setFullname(rs.getString("full_name"));
                    ue.setPhoto(rs.getBytes("photo"));
                    ue.setPhotoSmall(rs.getBytes("photo_small"));
                    return Optional.of(ue);
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {

        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "SELECT * FROM \"user\" WHERE username = ?"
        )) {
            ps.setObject(1, username);

            ps.execute();

            try (ResultSet rs = ps.getResultSet()) {
                if (rs.next()) {
                    UserEntity ue = new UserEntity();
                    ue.setId(rs.getObject("id", UUID.class));
                    ue.setUsername(rs.getString("username"));
                    ue.setCurrency(CurrencyValues.valueOf(rs.getString("currency")));
                    ue.setFirstname(rs.getString("firstname"));
                    ue.setSurname(rs.getString("surname"));
                    ue.setFullname(rs.getString("full_name"));
                    ue.setPhoto(rs.getBytes("photo"));
                    ue.setPhotoSmall(rs.getBytes("photo_small"));
                    return Optional.of(ue);
                } else {
                    return Optional.empty();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public UserEntity update(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "UPDATE \"user\" SET username = ?, currency = ?, firstname = ?, surname = ?, " +
                        "full_name = ?, photo = ?, photo_small = ? WHERE id = ?"
        )) {
            ps.setString(1, user.getUsername());
            ps.setObject(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setString(5, user.getFullname());
            ps.setBytes(6, user.getPhoto());
            ps.setBytes(7, user.getPhotoSmall());
            ps.setObject(8, user.getId());

            int rowsUpdate = ps.executeUpdate();
            if (rowsUpdate == 0) {
                throw new IllegalStateException("Пользователь не найден с ID:" + user.getId());
            }
            System.out.println("Обновлено в userdata.user '" + rowsUpdate + "' строк");

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка при обновлении пользователя в userdata", e);
        }
        return user;
    }

    @Override
    public void sendInvitation(UserEntity requester, UserEntity addressee) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                        "VALUES ( ?, ?, ?, ?)")) {
            ps.setObject(1, requester.getId());
            ps.setObject(2, addressee.getId());
            ps.setString(3, String.valueOf(FriendshipStatus.PENDING));
            ps.setDate(4, new java.sql.Date(new Date().getTime()));

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {

        sendInvitation(requester, addressee);
        sendInvitation(addressee, requester);

        // Меняем статус на ACCEPTED для двух записей
        try (PreparedStatement update = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                """
                        UPDATE friendship
                        SET status = ?
                        WHERE (requester_id = ? AND addressee_id = ?)
                           OR (requester_id = ? AND addressee_id = ?)
                        """
        )) {
            update.setString(1, String.valueOf(FriendshipStatus.ACCEPTED));
            update.setObject(2, requester.getId());
            update.setObject(3, addressee.getId());
            update.setObject(4, addressee.getId());
            update.setObject(5, requester.getId());

            update.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(UserEntity user) {
        try (PreparedStatement ps = holder(CFG.userdataJdbcUrl()).connection().prepareStatement(
                "DELETE FROM \"user\" WHERE username = ?"
        )) {
            ps.setObject(1, user.getUsername());

            int rowDeleted = ps.executeUpdate();
            System.out.println("Удалено из userdata.user '" + rowDeleted + "' строк");

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}


