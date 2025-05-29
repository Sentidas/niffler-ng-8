package guru.qa.niffler.service;

import guru.qa.niffler.model.FullUserJson;
import guru.qa.niffler.model.userdata.UserJson;

import java.util.Optional;
import java.util.UUID;

public interface UsersClient {

    UserJson createUser(String username, String password);

    UserJson updateUser(String username, UserJson user);

    void removeUser(String username);

    void createIncomeInvitations(UserJson targetUser, int count);

    void createOutcomeInvitations(UserJson targetUser, int count);

    void addFriends(UserJson targetUser, int count);

    Optional<FullUserJson> findFullUserByById(UUID userId);

    Optional<FullUserJson> findFullUserByUsername(String username);
}
