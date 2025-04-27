package guru.qa.niffler.service.service;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.Databases;
import guru.qa.niffler.data.dao.AuthAuthorityDao;
import guru.qa.niffler.data.dao.AuthUserDao;
import guru.qa.niffler.data.dao.impl.AuthAuthorityDaoJdbc;
import guru.qa.niffler.data.dao.impl.AuthUserDaoJdbc;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.auth.UserEntity;
import guru.qa.niffler.model.auth.UserJson;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Connection;

import static guru.qa.niffler.data.Databases.xaTransaction;

public class AuthDbClient {


    private static final Config CFG = Config.getInstance();

    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();


    public UserJson createUser(UserJson user) {
        return xaTransaction(
                Connection.TRANSACTION_READ_COMMITTED,
                new Databases.XaFunction<>(
                        connection -> {
                            UserJson updateUserJson = user.withEncodedPassword(pe.encode(user.password()));
                            UserEntity userEntity = UserEntity.fromJson(updateUserJson);
                            AuthUserDao userDao = new AuthUserDaoJdbc(connection);

                            UserEntity createdUser = userDao.createUser(userEntity);

                            if (createdUser.getId() != null) {
                                AuthAuthorityDao authorityDao = new AuthAuthorityDaoJdbc(connection);

                                AuthorityEntity readRole = new AuthorityEntity();
                                readRole.setUser(createdUser);
                                readRole.setAuthority(Authority.read);
                                authorityDao.createUser(readRole);

                                AuthorityEntity writeRole = new AuthorityEntity();
                                writeRole.setUser(createdUser);
                                writeRole.setAuthority(Authority.write);
                                authorityDao.createUser(writeRole);
                            }
                            return UserJson.fromEntity(createdUser);
                        },
                        CFG.authJdbcUrl()
                )
        );
    }
}
