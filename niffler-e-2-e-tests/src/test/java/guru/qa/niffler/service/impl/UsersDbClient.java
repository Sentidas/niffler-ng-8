package guru.qa.niffler.service.impl;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.auth.Authority;
import guru.qa.niffler.data.entity.auth.AuthorityEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.data.repository.AuthUserRepository;
import guru.qa.niffler.data.repository.UserdataUserRepository;
import guru.qa.niffler.data.repository.impl.*;
import guru.qa.niffler.data.tpl.XaTransactionTemplate;
import guru.qa.niffler.model.FullUserJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;


public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserdataUserRepositorySpringJdbc();

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );


    public UserJson createUser(String username, String password) {
        return xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, password);

                    authUserRepository.create(authUser);
                    return UserJson.fromEntity(
                            userdataUserRepository.create(userEntity(username))
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


    public void removeUser(String username) {
        xaTxTemplate.execute(() -> {
            AuthUserEntity authUser = authUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found in auth: " + username));

            authUserRepository.remove(authUser);

            UserEntity user = userdataUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found in userData: " + username));
            userdataUserRepository.remove(user);

            return null;
        });
    }

    public UserJson updateUser(String username, UserJson updatedUser) {
        return xaTxTemplate.execute(() -> {

          //  String username  = Optional.of(authUserRepository.findByUsername(updatedUser.username()));

            AuthUserEntity authUser = authUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found in auth: " + username));

            if (updatedUser.username() != null) {
                authUser.setUsername(updatedUser.username());
            }

            authUserRepository.update(authUser);

            UserEntity user = userdataUserRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalStateException("User not found in userData: " + username));

            if (updatedUser.username() != null) {
                user.setUsername(updatedUser.username());
            }

            if (updatedUser.currency() != null) {
                user.setCurrency(updatedUser.currency());
            }

            if (updatedUser.firstname() != null) {
                user.setFirstname(updatedUser.firstname());
            }

            if (updatedUser.surname() != null) {
                user.setSurname(updatedUser.surname());
            }

            if (updatedUser.photo() != null) {
                user.setPhoto(updatedUser.photo());
            }

            if (updatedUser.photoSmall() != null) {
                user.setPhotoSmall(updatedUser.photoSmall());
            }

            if (updatedUser.fullname() != null) {
                user.setFullname(updatedUser.fullname());
            }

            userdataUserRepository.update(user);

            return UserJson.fromEntity(user);
        });
    }


    public Optional<FullUserJson> findFullUserByById(UUID userId) {
        return xaTxTemplate.execute(() -> {

                    Optional<UserEntity> user = userdataUserRepository.findById(userId);

                    if (user.isEmpty()) {
                        return Optional.empty();
                    }

                    Optional<AuthUserEntity> authUser = authUserRepository.findByUsername(user.get().getUsername());

                    if (authUser.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(FullUserJson.fromEntity(authUser.get(), user.get()));
                }
        );
    }

    public Optional<FullUserJson> findFullUserByUsername(String username) {
        return xaTxTemplate.execute(() -> {
                    Optional<AuthUserEntity> authUser = authUserRepository.findByUsername(username);

                    if (authUser.isEmpty()) {
                        return Optional.empty();
                    }
                    Optional<UserEntity> user = userdataUserRepository.findByUsername(username);

                    if (user.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(FullUserJson.fromEntity(authUser.get(), user.get()));
                }
        );
    }

    public void createIncomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findByUsername(
                    targetUser.username()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {

                            String username = RandomDataUtils.randomUsername();

                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity addressee = userdataUserRepository.create(userEntity(username));

                            userdataUserRepository.sendInvitation(addressee, targetEntity);
                            return null;
                        }
                );
            }
        }
    }

    public void createOutcomeInvitations(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findByUsername(
                    targetUser.username()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                xaTxTemplate.execute(() -> {

                            String username = RandomDataUtils.randomUsername();

                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity addressee = userdataUserRepository.create(userEntity(username));

                            userdataUserRepository.sendInvitation(targetEntity, addressee);
                            return null;
                        }
                );
            }
        }
    }

    public void addFriends(UserJson targetUser, int count) {
        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findByUsername(
                    targetUser.username()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {

                xaTxTemplate.execute(() -> {
                            String username = RandomDataUtils.randomUsername();

                            AuthUserEntity authUser = authUserEntity(username, "12345");
                            authUserRepository.create(authUser);
                            UserEntity addressee = userdataUserRepository.create(userEntity(username));

                            userdataUserRepository.addFriend(targetEntity, addressee);
                            return null;
                        }
                );
            }
        }
    }
}

