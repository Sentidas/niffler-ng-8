package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.UserdataUserDAO;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.UserdataUserDAOJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthAuthorityDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.springJdbc.UserdataUserDaoSpringJdbc;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.auth.AuthorityJson;
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.Databases.*;

public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();


    public UserJson createUserSpringJdbc(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .createUser(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setId(createdAuthUser.getId());
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .create(authorityEntities);


        return UserJson.fromEntity(
                new UserdataUserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
                        .createUser(
                                UserEntity.fromJson(user)
                        )
        );
    }


    public UserJson createUser(AuthUserJson authUser, UserJson user) {
        return xaTransaction(
                Connection.TRANSACTION_READ_COMMITTED,
                new Databases.XaFunction<>(
                        connection -> {
                            AuthUserDao authUserDao = new AuthUserDaoJdbc(connection);

                            AuthUserJson encodedUserJson = authUser.withEncodedPassword(pe.encode(authUser.password()));
                            AuthUserEntity authUserEntity = AuthUserEntity.fromJson(encodedUserJson);

                            AuthUserEntity createdAuthUser = authUserDao.createUser(authUserEntity);

                            if (createdAuthUser.getId() != null) {
                                AuthAuthorityDao authorityDao = new AuthAuthorityDaoJdbc(connection);

                                authorityDao.create(new AuthorityEntity(createdAuthUser, Authority.read));
                                authorityDao.create(new AuthorityEntity(createdAuthUser, Authority.write));

                            }
                            return null;
                        },
                        CFG.authJdbcUrl()
                ),

                new Databases.XaFunction<>(
                        connection -> {
                            UserEntity userEntity = UserEntity.fromJson(user);
                            return UserJson.fromEntity(
                                    new UserdataUserDAOJdbc(connection).createUser(userEntity));

                        },
                        CFG.userdataJdbcUrl()
                )
        );
    }

    public void deleteUser(String username) {
        xaTransaction(
                Connection.TRANSACTION_READ_COMMITTED,
                new Databases.XaFunction<>(
                        connection -> {
                            AuthUserDao authUserDao = new AuthUserDaoJdbc(connection);
                            Optional<AuthUserEntity> authUser = authUserDao.findByUsername(username);

                            AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc(connection);
                            authAuthorityDao.delete(authUser.get().getId());

                            authUserDao.deleteUser(authUser.get().getId());

                            return null;
                        },
                        CFG.authJdbcUrl()
                ),
                new Databases.XaFunction<>(
                        connection -> {

                            UserdataUserDAO userDao = new UserdataUserDAOJdbc(connection);
                            Optional<UserEntity> user = userDao.findByUsername(username);
                            userDao.deleteUser(user.get());

                            return null;
                        },
                        CFG.userdataJdbcUrl()
                )
        );
    }

    public UserJson createUserInUserData(UserJson user) {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    UserEntity userEntity = UserEntity.fromJson(user);

                    return UserJson.fromEntity(
                            new UserdataUserDAOJdbc(connection).createUser(userEntity)
                    );
                },
                CFG.userdataJdbcUrl()
        );
    }

    public void deleteUserInUserdata(UUID userId) {
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

    public List<UserJson> findAllUdUsers() {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    List<UserEntity> user = new UserdataUserDAOJdbc(connection).findAll();

                    return user.stream()
                            .map(UserJson::fromEntity)
                            .toList();
                },
                CFG.userdataJdbcUrl()
        );
    }

    public List<UserJson> findAllUdUsersSpringJdbc() {
        List<UserEntity> users = new UserdataUserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
                .findAll();

        return users.stream()
                .map(UserJson::fromEntity)
                .toList();
    }

    public List<AuthUserJson> findAllAuthUsers() {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    List<AuthUserEntity> user = new AuthUserDaoJdbc(connection).findAll();

                    return user.stream()
                            .map(AuthUserJson::fromEntity)
                            .toList();
                },
                CFG.authJdbcUrl()
        );
    }

    public List<AuthUserJson> findAllAuthUsersSpringJdbc() {
        List<AuthUserEntity> users = new AuthUserDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .findAll();

        return users.stream()
                .map(AuthUserJson::fromEntity)
                .toList();
    }


    public List<AuthorityJson> findAllAuthorities() {
        return transaction(Connection.TRANSACTION_READ_COMMITTED, connection -> {
                    List<AuthorityEntity> user = new AuthAuthorityDaoJdbc(connection).findAll();

                    return user.stream()
                            .map(AuthorityJson::fromEntity)
                            .toList();
                },
                CFG.authJdbcUrl()
        );
    }

    public List<AuthorityJson> findAllAuthoritiesSpringJdbc() {
        List<AuthorityEntity> users = new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
                .findAll();

        return users.stream()
                .map(AuthorityJson::fromEntity)
                .toList();
    }
}
