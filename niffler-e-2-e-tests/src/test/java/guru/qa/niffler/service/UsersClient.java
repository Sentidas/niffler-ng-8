package guru.qa.niffler.service;

import guru.qa.niffler.model.userdata.FullUserJson;
import guru.qa.niffler.model.userdata.UserJson;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsersClient {

    UserJson createUser(String username, String password) throws InterruptedException;

    UserJson updateUser(String username, UserJson user);

    void removeUser(String username);

    List<UserJson> createIncomeInvitations(UserJson targetUser, int count);

    List<UserJson> createOutcomeInvitations(UserJson targetUser, int count);

    List<UserJson> addFriends(UserJson targetUser, int count);

    List<UserJson> getFriends(String username);

    List<UserJson> getIncomeInvitation(String username);

    List<UserJson> getOutcomeInvitation(String username);

    List<UserJson> getAllUsers(String username);

    Optional<UserJson> findUserByUsername(String username);

    Optional<FullUserJson> findUserByIdWithAuth(UUID userId);

    Optional<FullUserJson> findFullUserByUsername(String username);
}
