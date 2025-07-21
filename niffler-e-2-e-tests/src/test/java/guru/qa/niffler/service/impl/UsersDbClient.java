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
import guru.qa.niffler.model.userdata.FullUserJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.UsersClient;
import guru.qa.niffler.utils.RandomDataUtils;
import io.qameta.allure.Step;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;

@ParametersAreNonnullByDefault
public class UsersDbClient implements UsersClient {

    private static final Config CFG = Config.getInstance();
    private static final PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    private final AuthUserRepository authUserRepository = new AuthUserRepositoryHibernate();
    private final UserdataUserRepository userdataUserRepository = new UserDataUserRepositoryHibernate();

    private final XaTransactionTemplate xaTxTemplate = new XaTransactionTemplate(
            CFG.authJdbcUrl(),
            CFG.userdataJdbcUrl()
    );


    @Override
    @Step("Create user using SQL")
    public UserJson createUser(String username, String password) {
        return xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, password);

                    authUserRepository.create(authUser);
                    return UserJson.fromEntity(
                            userdataUserRepository.create(userEntity(username)), null
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

    @Step("Delete user '{0}' using SQL")
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

    @Step("Update user '{0}' using SQL")
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

            if (updatedUser.photo() != null && updatedUser.photo().startsWith("data:image")) {
                String base64 = updatedUser.photo().substring(updatedUser.photo().indexOf(",") + 1);
                user.setPhoto(Base64.getDecoder().decode(base64));
            }

            if (updatedUser.photoSmall() != null && updatedUser.photo().startsWith("data:image")) {
                String base64 = updatedUser.photo().substring(updatedUser.photo().indexOf(",") + 1);
                user.setPhotoSmall(Base64.getDecoder().decode(base64));
            }


            if (updatedUser.fullname() != null) {
                user.setFullname(updatedUser.fullname());
            }

            userdataUserRepository.update(user);

            return UserJson.fromEntity(user, null);
        });
    }

    @Step("Get user using SQL with id:'{0}'")
    public Optional<FullUserJson> findUserByIdWithAuth(UUID userId) {
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

    @Step("Get user '{0}' using SQL")
    public Optional<UserJson> findUserByUsername(String username) {
        return xaTxTemplate.execute(() ->
                userdataUserRepository.findByUsername(username)
                        .map(entity -> UserJson.fromEntity(entity, null))
        );
    }

    @Step("Get user '{0}' with authority using SQL")
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

    @Step("Create {1} income invitation using SQL")
    public List<UserJson> createIncomeInvitations(UserJson targetUser, int count) {
        List<UserJson> incomeInvitations = new ArrayList<>();

        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findByUsername(
                    targetUser.username()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                UserJson addressee = xaTxTemplate.execute(() -> {

                    String username = RandomDataUtils.randomUsername();

                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.create(authUser);
                    UserEntity fromUser = userdataUserRepository.create(userEntity(username));

                    userdataUserRepository.sendInvitation(fromUser, targetEntity);
                    return UserJson.fromEntity(fromUser, null);
                });
                incomeInvitations.add(addressee);
            }
        }
        return incomeInvitations;
    }

    @Step("Create {1} outcome invitation using SQL")
    public List<UserJson> createOutcomeInvitations(UserJson targetUser, int count) {
        List<UserJson> outcomeInvitations = new ArrayList<>();

        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findByUsername(
                    targetUser.username()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                String username = RandomDataUtils.randomUsername();

                UserJson addressee = xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.create(authUser);
                    UserEntity toUser = userdataUserRepository.create(userEntity(username));

                    userdataUserRepository.sendInvitation(targetEntity, toUser);
                    return UserJson.fromEntity(toUser, null);
                });
                outcomeInvitations.add(addressee);
            }
        }
        return outcomeInvitations;
    }

    @Step("Add {1} friends using SQL")
    public List<UserJson> addFriends(UserJson targetUser, int count) {
        List<UserJson> friends = new ArrayList<>();

        if (count > 0) {
            UserEntity targetEntity = userdataUserRepository.findByUsername(
                    targetUser.username()
            ).orElseThrow();

            for (int i = 0; i < count; i++) {
                String username = RandomDataUtils.randomUsername();

                UserJson friend = xaTxTemplate.execute(() -> {
                    AuthUserEntity authUser = authUserEntity(username, "12345");
                    authUserRepository.create(authUser);
                    UserEntity user = userdataUserRepository.create(userEntity(username));

                    userdataUserRepository.addFriend(targetEntity, user);
                    return UserJson.fromEntity(user, null);
                });
                friends.add(friend);
            }
        }
        return friends;
    }
}

