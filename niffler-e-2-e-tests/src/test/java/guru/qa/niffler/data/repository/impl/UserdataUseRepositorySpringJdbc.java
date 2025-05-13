package guru.qa.niffler.data.repository.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.userdata.FriendshipStatus;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.mapper.UserDataUserEntityRowMapper;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.tpl.DataSources;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class UserdataUseRepositorySpringJdbc implements UserdataUserRepository {

    private static final Config CFG = Config.getInstance();

    @Override
    public UserEntity create(UserEntity user) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));
        KeyHolder kh = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO \"user\" (username, currency, firstname, surname, photo, photo_small, full_name) " +
                            "VALUES(?, ?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getCurrency().name());
            ps.setString(3, user.getFirstname());
            ps.setString(4, user.getSurname());
            ps.setBytes(5, user.getPhoto());
            ps.setBytes(6, user.getPhotoSmall());
            ps.setString(7, user.getFullname());
            return ps;
        }, kh);
        final UUID generatedKey = (UUID) kh.getKeys().get("id");
        user.setId(generatedKey);
        return user;
    }


    @Override
    public Optional<UserEntity> findById(UUID id) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE id = ?",
                        UserDataUserEntityRowMapper.instance,
                        id
                )
        );
    }

    @Override
    public Optional<UserEntity> findByUsername(String username) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        return Optional.ofNullable(
                jdbcTemplate.queryForObject(
                        "SELECT * FROM \"user\" WHERE username = ?",
                        UserDataUserEntityRowMapper.instance,
                        username
                )
        );
    }

    @Override
    public void addOutcomeInvitation(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                                    "VALUES ( ?, ?, ?, ?)");

                    ps.setObject(1, requester.getId());
                    ps.setObject(2, addressee.getId());
                    ps.setString(3, String.valueOf(FriendshipStatus.PENDING));
                    ps.setDate(4, new java.sql.Date(new Date().getTime()));
                    return ps;
                }
        );
    }

    @Override
    public void addIncomeInvitation(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        jdbcTemplate.update(con -> {
                    PreparedStatement ps = con.prepareStatement(
                            "INSERT INTO friendship (requester_id, addressee_id, status, created_date) " +
                                    "VALUES ( ?, ?, ?, ?)");

                    ps.setObject(1, addressee.getId());
                    ps.setObject(2, requester.getId());
                    ps.setString(3, String.valueOf(FriendshipStatus.PENDING));
                    ps.setDate(4, new java.sql.Date(new Date().getTime()));
                    return ps;
                }
        );
    }

    @Override
    public void addFriend(UserEntity requester, UserEntity addressee) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(DataSources.dataSource(CFG.userdataJdbcUrl()));

        // Проверяем наличие заявки со статусом PENDING
        Integer result = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM friendship WHERE requester_id = ? AND addressee_id = ? AND status = ?",
                Integer.class,
                requester.getId(),
                addressee.getId(),
                String.valueOf(FriendshipStatus.PENDING));

        if (result == 0) {
            throw new IllegalStateException("Нет заявки от " + requester.getUsername() + " к " + addressee.getUsername());
        }

        // Добавляем обратную заявку
        addIncomeInvitation(requester, addressee);

        // Меняем статус на ACCEPTED для двух заявок
        jdbcTemplate.update(
                "UPDATE friendship SET status = ? WHERE " +
                        "(requester_id = ? AND addressee_id = ?) OR " +
                        "(requester_id = ? AND addressee_id = ?)",
                String.valueOf(FriendshipStatus.ACCEPTED),
                requester.getId(),
                addressee.getId(),
                addressee.getId(),
                requester.getId());
    }
}


