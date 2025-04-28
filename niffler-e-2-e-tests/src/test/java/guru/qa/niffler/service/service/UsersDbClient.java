package guru.qa.niffler.service.service;

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
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;
import java.util.Arrays;
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
                   ae.setUserId(createdAuthUser.getId());
                   ae.setAuthority(e);
                   return ae;
               }
       ).toArray(AuthorityEntity[]::new);

       new AuthAuthorityDaoSpringJdbc(dataSource(CFG.authJdbcUrl()))
               .create(authorityEntities);

       return UserJson.fromEntity(
               new UserdataUserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
               .createUser(UserEntity.fromJson(user))
       );
   }

    public AuthUserJson createUser(AuthUserJson user) {
        return xaTransaction(
                Connection.TRANSACTION_READ_COMMITTED,
                new Databases.XaFunction<>(
                        connection -> {
                            AuthUserJson updateUserJson = user.withEncodedPassword(pe.encode(user.password()));
                            AuthUserEntity userEntity = AuthUserEntity.fromJson(updateUserJson);
                            AuthUserDao userDao = new AuthUserDaoJdbc(connection);

                            AuthUserEntity createdUser = userDao.createUser(userEntity);

                            if (createdUser.getId() != null) {
                                AuthAuthorityDao authorityDao = new AuthAuthorityDaoJdbc(connection);

                                AuthorityEntity readRole = new AuthorityEntity();
                                readRole.setUser(createdUser);
                                readRole.setAuthority(Authority.read);
                                authorityDao.create(readRole);

                                AuthorityEntity writeRole = new AuthorityEntity();
                                writeRole.setUser(createdUser);
                                writeRole.setAuthority(Authority.write);
                                authorityDao.create(writeRole);
                            }
                            return AuthUserJson.fromEntity(createdUser);
                        },
                        CFG.authJdbcUrl()
                )
        );
    }

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


