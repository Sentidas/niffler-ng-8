package guru.qa.niffler.service;

import guru.qa.niffler.config.Config;
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
import guru.qa.niffler.data.tpl.DataSources;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.auth.AuthorityJson;
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static guru.qa.niffler.data.tpl.DataSources.dataSource;


public class UsersDbClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();
    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final UserdataUserDAO userdataUserDAO = new UserdataUserDAOJdbc();

    private final AuthAuthorityDao authAuthorityDaoSpring = new AuthAuthorityDaoSpringJdbc();
    private final AuthUserDao authUserDaoSpring = new AuthUserDaoSpringJdbc();
    private final UserdataUserDAO userdataUserDAOSpring = new UserdataUserDaoSpringJdbc();

    private final TransactionTemplate txTemplateUserData = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.userdataUrl())
            )
    );

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    DataSources.dataSource(CFG.authUrl())
            )
    );

    private final JdbcTransactionTemplate jdbcTxTemplateUserData = new JdbcTransactionTemplate(
            CFG.authJdbcUrl()
    );

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.authJdbcUrl()
    );


    private final XaTransactionTemplate xaTransactionTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    public UserJson createUserSpringJdbc(UserJson user) {
        return xaTransactionTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDaoSpring
                            .createUser(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUserId(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthorityDaoSpring.create(authorityEntities);
                    return UserJson.fromEntity(
                            userdataUserDAOSpring.createUser(UserEntity.fromJson(user))
                    );
                }
        );
    }


//    public UserJson createUser(AuthUserJson authUser, UserJson user) {
//        return xaTransaction(
//                Connection.TRANSACTION_READ_COMMITTED,
//                new Databases.XaFunction<>(
//                        connection -> {
//                            AuthUserDao authUserDao = new AuthUserDaoJdbc(connection);
//
//                            AuthUserJson encodedUserJson = authUser.withEncodedPassword(pe.encode(authUser.password()));
//                            AuthUserEntity authUserEntity = AuthUserEntity.fromJson(encodedUserJson);
//
//                            AuthUserEntity createdAuthUser = authUserDao.createUser(authUserEntity);
//
//                            if (createdAuthUser.getId() != null) {
//                                AuthAuthorityDao authorityDao = new AuthAuthorityDaoJdbc(connection);
//
//                                authorityDao.create(new AuthorityEntity(createdAuthUser, Authority.read));
//                                authorityDao.create(new AuthorityEntity(createdAuthUser, Authority.write));
//
//                            }
//                            return null;
//                        },
//                        CFG.authJdbcUrl()
//                ),
//
//                new Databases.XaFunction<>(
//                        connection -> {
//                            UserEntity userEntity = UserEntity.fromJson(user);
//                            return UserJson.fromEntity(
//                                    new UserdataUserDAOJdbc(connection).createUser(userEntity));
//
//                        },
//                        CFG.userdataJdbcUrl()
//                )
//        );
//    }

//    public void deleteUser(String username) {
//        xaTransaction(
//                Connection.TRANSACTION_READ_COMMITTED,
//                new Databases.XaFunction<>(
//                        connection -> {
//                            AuthUserDao authUserDao = new AuthUserDaoJdbc(connection);
//                            Optional<AuthUserEntity> authUser = authUserDao.findByUsername(username);
//
//                            AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc(connection);
//                            authAuthorityDao.delete(authUser.get().getId());
//
//                            authUserDao.deleteUser(authUser.get().getId());
//
//                            return null;
//                        },
//                        CFG.authJdbcUrl()
//                ),
//                new Databases.XaFunction<>(
//                        connection -> {
//
//                            UserdataUserDAO userDao = new UserdataUserDAOJdbc(connection);
//                            Optional<UserEntity> user = userDao.findByUsername(username);
//                            userDao.deleteUser(user.get());
//
//                            return null;
//                        },
//                        CFG.userdataJdbcUrl()
//                )
//        );
//    }

    public UserJson createUserInUserData(UserJson user) {
        return jdbcTxTemplateUserData.execute(() -> {
                    UserEntity userEntity = UserEntity.fromJson(user);

                    return UserJson.fromEntity(
                            userdataUserDAO.createUser(userEntity)
                    );
                }
        );
    }

    public void deleteUserInUserdata(UUID userId) {
        jdbcTxTemplateUserData.execute(() -> {

                    userdataUserDAO.findById(userId)
                            .ifPresentOrElse(user ->
                                            userdataUserDAO.deleteUser(user),
                                    () -> {
                                        throw new IllegalArgumentException("User не найден: " + userId);
                                    });
                    return null;
                }
        );
    }

    public Optional<UserJson> findUserById(UUID userId) {
        return  jdbcTxTemplateUserData.execute(() -> {
                    Optional<UserEntity> user = userdataUserDAO.findById(userId);

                    if (user.isPresent()) {
                        return Optional.of(UserJson.fromEntity(user.get()));
                    } else {
                        return Optional.empty();
                    }
                }
        );
    }

    public Optional<UserJson> findUserByUsername(String username) {
        return  jdbcTxTemplateUserData.execute(() -> {
                    Optional<UserEntity> user = userdataUserDAO.findByUsername(username);

                    if (user.isPresent()) {
                        return Optional.of(UserJson.fromEntity(user.get()));
                    } else {
                        return Optional.empty();
                    }
                }
        );
    }

    public List<UserJson> findAllUdUsers() {
        return  jdbcTxTemplateUserData.execute(() -> {
                    List<UserEntity> user = userdataUserDAO.findAll();

                    return user.stream()
                            .map(UserJson::fromEntity)
                            .toList();
                }
        );
    }

    public List<UserJson> findAllUdUsersSpringJdbc() {
        List<UserEntity> users = userdataUserDAOSpring
                .findAll();

        return users.stream()
                .map(UserJson::fromEntity)
                .toList();
    }

    public List<AuthUserJson> findAllAuthUsers() {
        return  jdbcTxTemplate.execute(() -> {
                    List<AuthUserEntity> user = authUserDao.findAll();

                    return user.stream()
                            .map(AuthUserJson::fromEntity)
                            .toList();
                }
        );
    }

    public List<AuthUserJson> findAllAuthUsersSpringJdbc() {
        List<AuthUserEntity> users = authUserDaoSpring
                .findAll();

        return users.stream()
                .map(AuthUserJson::fromEntity)
                .toList();
    }


    public List<AuthorityJson> findAllAuthorities() {
        return  jdbcTxTemplate.execute(() -> {
                    List<AuthorityEntity> user = authAuthorityDao.findAll();

                    return user.stream()
                            .map(AuthorityJson::fromEntity)
                            .toList();
                }
        );
    }

    public List<AuthorityJson> findAllAuthoritiesSpringJdbc() {
        List<AuthorityEntity> users = authAuthorityDaoSpring
                .findAll();

        return users.stream()
                .map(AuthorityJson::fromEntity)
                .toList();
    }
}
