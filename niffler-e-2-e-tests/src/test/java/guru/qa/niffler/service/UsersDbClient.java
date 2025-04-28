package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.Databases.XaFunction;

import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoSpringJdbc;

import guru.qa.niffler.data.dao.impl.AuthUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoJdbc;
import guru.qa.niffler.data.dao.impl.UdUserDaoSpringJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.jdbc.AuthUserDaoJdbc;
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

import static guru.qa.niffler.data.Databases.dataSource;
import static guru.qa.niffler.data.Databases.xaTransaction;

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
        .create(authUser);

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
        new UdUserDaoSpringJdbc(dataSource(CFG.userdataJdbcUrl()))
            .create(
                UserEntity.fromJson(user)
            )
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
}
