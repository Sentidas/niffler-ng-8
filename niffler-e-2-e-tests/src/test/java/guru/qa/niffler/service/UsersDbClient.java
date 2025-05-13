package guru.qa.niffler.service;

import ch.qos.logback.core.testUtil.RandomUtil;
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
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.*;
import guru.qa.niffler.data.tpl.JdbcTransactionTemplate;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.auth.AuthUserJson;
import guru.qa.niffler.model.auth.AuthorityJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.utils.RandomDataUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.transaction.ChainedTransactionManager;
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

    private final AuthUserDao authUserDao = new AuthUserDaoJdbc();
    private final AuthAuthorityDao authAuthorityDao = new AuthAuthorityDaoJdbc();
    private final UserdataUserDAO userDataDao = new UserdataUserDAOJdbc();

    private final AuthUserDao authUserDaoSpring = new AuthUserDaoSpringJdbc();
    private final AuthAuthorityDao authAuthorityDaoSpring = new AuthAuthorityDaoSpringJdbc();
    private final UserdataUserDAO userDataDaoSpring = new UserdataUserDaoSpringJdbc();

    private final AuthUserRepository authUserRepositoryJdbc = new AuthUserRepositoryJdbc();
    private final AuthUserRepository authUserRepositoryHibernate = new AuthUserRepositoryHibernate();
    private final AuthUserRepository authUserRepositorySpring = new AuthUserRepositorySpringJdbc();
    private final UserdataUserRepository userdataUserRepositoryJdbc = new UserdataUserRepositoryJdbc();
    private final UserdataUserRepository userdataUserRepositorySpring = new UserdataUseRepositorySpringJdbc();
    private final UserdataUserRepository userdataUserRepositoryHibernate = new UserDataUserRepositoryHibernate();

    private final TransactionTemplate txTemplate = new TransactionTemplate(
            new JdbcTransactionManager(
                    dataSource(CFG.authJdbcUrl())
            )
    );

    private final JdbcTransactionTemplate jdbcTxTemplate = new JdbcTransactionTemplate(
            CFG.authJdbcUrl()
    );

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );

    private final TransactionTemplate xaTxTemplateChained = new TransactionTemplate(
            new ChainedTransactionManager(
                    new JdbcTransactionManager(dataSource(CFG.authJdbcUrl())),
                    new JdbcTransactionManager(dataSource(CFG.userdataJdbcUrl()))
            )
    );

    public UserJson createUserRepositoryHibernate(String username, String password) {
        return xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, password);

                    authUserRepositoryHibernate.create(authUser);
                    return UserJson.fromEntity(
                            userdataUserRepositoryHibernate.create(userEntity(username))
                    );
                }
        );
    }

    private UserEntity userEntity(String username) {
        UserEntity ue = new UserEntity();
        ue.setUsername(username);
        ue.setCurrency(CurrencyValues.RUB);
        return ue;
    }

    private AuthUserEntity authUserEntity(String username, String password) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(username);
        authUser.setPassword(pe.encode(password));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);
        authUser.setAuthorities(
                Arrays.stream(Authority.values()).map(
                        e -> {
                            AuthorityEntity ae = new AuthorityEntity();
                            ae.setUser(authUser);
                            ae.setAuthority(e);
                            return ae;
                        }
                ).toList()
        );
        return authUser;
    }
//
//    public UserJson createUserRepository(UserJson user) {
//        return xaTxTemplate.execute(() -> {
//            AuthUserEntity authUser = authUserEntity(user);
//
//            authUserRepositoryJdbc.create(authUser);
//                    return UserJson.fromEntity(
//                            userdataUserRepositoryJdbc.create(UserEntity.fromJson(user))
//                    );
//                }
//        );
//    }
//
//    public UserJson createUserRepositorySpring(UserJson user) {
//        return xaTxTemplate.execute(() -> {
//            AuthUserEntity authUser = authUserEntity(user);
//
//            authUserRepositorySpring.create(authUser);
//                    return UserJson.fromEntity(
//                            userdataUserRepositorySpring.create(UserEntity.fromJson(user))
//                    );
//                }
//        );
//    }

    public UserJson createUserSpringTx(UserJson user) {
        return xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDaoSpring.create(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUser(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthorityDaoSpring.create(authorityEntities);
                    return UserJson.fromEntity(
                            userDataDaoSpring.create(UserEntity.fromJson(user))
                    );
                }
        );
    }

    public UserJson createUserJDBCTx(UserJson user) {
        return xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDao.create(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUser(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthorityDao.create(authorityEntities);
                    return UserJson.fromEntity(
                            userDataDao.create(UserEntity.fromJson(user))
                    );
                }
        );
    }

    public UserJson createUserSpringTxChained(UserJson user) {
        return xaTxTemplateChained.execute(status -> {
                    AuthUserEntity authUser = new AuthUserEntity();
                    authUser.setUsername(user.username());
                    authUser.setPassword(pe.encode("12345"));
                    authUser.setEnabled(true);
                    authUser.setAccountNonExpired(true);
                    authUser.setAccountNonLocked(true);
                    authUser.setCredentialsNonExpired(true);

                    AuthUserEntity createdAuthUser = authUserDaoSpring.create(authUser);

                    AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                            e -> {
                                AuthorityEntity ae = new AuthorityEntity();
                                ae.setUser(createdAuthUser);
                                ae.setAuthority(e);
                                return ae;
                            }
                    ).toArray(AuthorityEntity[]::new);

                    authAuthorityDaoSpring.create(authorityEntities);
                    return UserJson.fromEntity(
                            userDataDaoSpring.create(UserEntity.fromJson(user))
                    );
                }
        );
    }

    public UserJson createUserJDBC(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserDao.create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(createdAuthUser);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        authAuthorityDao.create(authorityEntities);
        return UserJson.fromEntity(
                userDataDao.create(UserEntity.fromJson(user))
        );
    }

    public UserJson createUserSpring(UserJson user) {
        AuthUserEntity authUser = new AuthUserEntity();
        authUser.setUsername(user.username());
        authUser.setPassword(pe.encode("12345"));
        authUser.setEnabled(true);
        authUser.setAccountNonExpired(true);
        authUser.setAccountNonLocked(true);
        authUser.setCredentialsNonExpired(true);

        AuthUserEntity createdAuthUser = authUserDao.create(authUser);

        AuthorityEntity[] authorityEntities = Arrays.stream(Authority.values()).map(
                e -> {
                    AuthorityEntity ae = new AuthorityEntity();
                    ae.setUser(createdAuthUser);
                    ae.setAuthority(e);
                    return ae;
                }
        ).toArray(AuthorityEntity[]::new);

        authAuthorityDao.create(authorityEntities);
        return UserJson.fromEntity(
                userDataDao.create(UserEntity.fromJson(user))
        );
    }


    public void deleteUserSpringTx(String username) {
        xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = authUserDaoSpring.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found in auth: " + username));

            authAuthorityDaoSpring.delete(authUser.getId());
            authUserDaoSpring.delete(authUser.getId());

            UserEntity user = userDataDaoSpring.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found in userData: " + username));
            userDataDaoSpring.delete(user);

            return null;
        });
    }

    public void deleteUserJDBCTx(String username) {
        xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = authUserDao.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found in auth: " + username));

            authAuthorityDao.delete(authUser.getId());
            authUserDao.delete(authUser.getId());

            UserEntity user = userDataDao.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found in userData: " + username));
            userDataDao.delete(user);

            return null;
        });
    }

    public Optional<UserJson> findUserById(UUID userId) {
        Optional<UserEntity> user = userDataDaoSpring.findById(userId);
        if (user.isPresent()) {
            return Optional.of(UserJson.fromEntity(user.get()));
        } else {
            return Optional.empty();
        }
    }

    public Optional<AuthUserJson> findUserByIdRepositorySpring(UUID userId) {
        Optional<AuthUserEntity> user = authUserRepositorySpring.findById(userId);
        return user.map(AuthUserJson::fromEntity);
    }

    public Optional<UserJson> findUserByUsername(String username) {
        Optional<UserEntity> user = userDataDaoSpring.findByUsername(username);
        if (user.isPresent()) {
            return Optional.of(UserJson.fromEntity(user.get()));
        } else {
            return Optional.empty();
        }
    }


    public List<UserJson> findAllUdUsers() {
        List<UserEntity> user = userDataDao.findAll();

        return user.stream()
                .map(UserJson::fromEntity)
                .toList();
    }


    public List<UserJson> findAllUdUsersSpringJdbc() {
        List<UserEntity> users = userDataDaoSpring
                .findAll();

        return users.stream()
                .map(UserJson::fromEntity)
                .toList();
    }

    public List<AuthUserJson> findAllAuthUsers() {
        List<AuthUserEntity> user = authUserDao.findAll();

        return user.stream()
                .map(AuthUserJson::fromEntity)
                .toList();
    }


    public List<AuthUserJson> findAllAuthUsersSpringJdbc() {
        List<AuthUserEntity> users = authUserDaoSpring
                .findAll();

        return users.stream()
                .map(AuthUserJson::fromEntity)
                .toList();
    }

    public List<AuthorityJson> findAllAuthorities() {
        List<AuthorityEntity> user = authAuthorityDao.findAll();
        return user.stream()
                .map(AuthorityJson::fromEntity)
                .toList();
    }


    public List<AuthorityJson> findAllAuthoritiesSpringJdbc() {
        List<AuthorityEntity> users = authAuthorityDaoSpring
                .findAll();

        return users.stream()
                .map(AuthorityJson::fromEntity)
                .toList();
    }

    public void addOutcomeInvitationRepositoryJdbc(String requester, String addressee) {
        xaTxTemplate.execute(() -> {
                    UserdataUserRepository requesterUser = new UserdataUserRepositoryJdbc();
                    Optional<UserEntity> requesterUserEntity = requesterUser.findByUsername(requester);
                    Optional<UserEntity> addresseeUserEntity = requesterUser.findByUsername(addressee);

                    userdataUserRepositoryJdbc.addOutcomeInvitation(requesterUserEntity.get(), addresseeUserEntity.get());
                    return null;
                }
        );
    }

    public void addIncomeInvitationRepository(String requester, String addressee) {
        xaTxTemplate.execute(() -> {
                    UserdataUserRepository requesterUser = new UserdataUserRepositoryJdbc();
                    Optional<UserEntity> requesterUserEntity = requesterUser.findByUsername(requester);
                    Optional<UserEntity> addresseeUserEntity = requesterUser.findByUsername(addressee);

                    userdataUserRepositoryJdbc.addOutcomeInvitation(requesterUserEntity.get(), addresseeUserEntity.get());
                    return null;
                }
        );
    }

    public void addFriendRepositoryJdbc(String requester, String addressee) {
        xaTxTemplate.execute(() -> {
                    UserdataUserRepository requesterUser = new UserdataUserRepositoryJdbc();
                    Optional<UserEntity> requesterUserEntity = requesterUser.findByUsername(requester);
                    Optional<UserEntity> addresseeUserEntity = requesterUser.findByUsername(addressee);

                    userdataUserRepositoryJdbc.addFriend(requesterUserEntity.get(), addresseeUserEntity.get());
                    return null;
                }
        );
    }

    public void addOutcomeInvitationRepositorySpring(String requester, String addressee) {
        xaTxTemplate.execute(() -> {
                    UserdataUserRepository requesterUser = new UserdataUserRepositoryJdbc();
                    Optional<UserEntity> requesterUserEntity = requesterUser.findByUsername(requester);
                    Optional<UserEntity> addresseeUserEntity = requesterUser.findByUsername(addressee);

                    userdataUserRepositorySpring.addOutcomeInvitation(requesterUserEntity.get(), addresseeUserEntity.get());
                    return null;
                }
        );
    }

    public void addFriendRepositorySpring(String requester, String addressee) {
        xaTxTemplate.execute(() -> {
                    UserdataUserRepository requesterUser = new UserdataUserRepositoryJdbc();
                    Optional<UserEntity> requesterUserEntity = requesterUser.findByUsername(requester);
                    Optional<UserEntity> addresseeUserEntity = requesterUser.findByUsername(addressee);

                    userdataUserRepositorySpring.addFriend(requesterUserEntity.get(), addresseeUserEntity.get());
                    return null;
                }
        );
    }

    public void addIncomeInvitationRepositoryHibernate(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepositoryHibernate.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {

                            String username = RandomDataUtils.randomUsername();

                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepositoryHibernate.create(authUser);
                            UserEntity addressee = userdataUserRepositoryHibernate.create(userEntity(username));

                            userdataUserRepositoryHibernate.addIncomeInvitation(targetEntity, addressee);
                            return null;
                        }
                );
            }
        }
    }

    public void addOutcomeInvitationRepositoryHibernate(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepositoryHibernate.findById(
                    targetUser.id()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {

                            String username = RandomDataUtils.randomUsername();

                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepositoryHibernate.create(authUser);
                            UserEntity addressee = userdataUserRepositoryHibernate.create(userEntity(username));

                            userdataUserRepositoryHibernate.addOutcomeInvitation(targetEntity, addressee);
                            return null;
                        }
                );
            }
        }
    }

//    public void addFriendRepositoryHibernate(UserJson targetUser, int count) {
//        xaTxTemplate.execute(() -> {
//                    UserdataUserRepository requesterUser = new UserdataUserRepositoryJdbc();
//                    Optional<UserEntity> requesterUserEntity = requesterUser.findByUsername(requester);
//                    Optional<UserEntity> addresseeUserEntity = requesterUser.findByUsername(addressee);
//
//                    userdataUserRepositorySpring.addFriend(requesterUserEntity.get(), addresseeUserEntity.get());
//                    return null;
//                }
//        );
//    }
}

