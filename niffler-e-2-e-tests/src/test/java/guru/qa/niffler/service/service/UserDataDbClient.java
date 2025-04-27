package guru.qa.niffler.service.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.dao.UserdataUserDAO;
import guru.qa.niffler.data.dao.impl.UserdataUserDAOJdbc;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.userdata.UserJson;

import java.sql.Connection;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.transaction;

public class UserDataDbClient {


    private static final Config CFG = Config.getInstance();

    public UserJson createUser(UserJson user) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    UserEntity userEntity = UserEntity.fromJson(user);

                    return UserJson.fromEntity(
                            new UserdataUserDAOJdbc(connection).createUser(userEntity)
                    );
                },
                CFG.userdataJdbcUrl()
        );
    }

    public Optional<UserJson> findUserById(UUID userId) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    Optional<UserEntity> user = new UserdataUserDAOJdbc(connection).findById(userId);

                    if (user.isPresent()) {
                        return Optional.of(UserJson.fromEntity(user.get()));
                    } else {
                        return Optional.empty();
                    }
                },
                CFG.userdataJdbcUrl()
        );
    }

    public Optional<UserJson> findUserByUsername(String username) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    Optional<UserEntity> user = new UserdataUserDAOJdbc(connection).findByUsername(username);

                    if (user.isPresent()) {
                        return Optional.of(UserJson.fromEntity(user.get()));
                    } else {
                        return Optional.empty();
                    }
                },
                CFG.userdataJdbcUrl()
        );
    }

    public void deleteUser(UUID userId) {
        transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    UserdataUserDAO userDao = new UserdataUserDAOJdbc(connection);
                    userDao.findById(userId)
                            .ifPresentOrElse(user ->
                                            userDao.deleteUser(user),
                                    () -> {
                                        throw new IllegalArgumentException("User не найден: " + userId);
                                    });
                    return null;
                },
                CFG.userdataJdbcUrl());
    }
}
