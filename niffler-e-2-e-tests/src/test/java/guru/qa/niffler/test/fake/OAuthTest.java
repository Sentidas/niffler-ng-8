package guru.qa.niffler.test.fake;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.model.friend.FriendJson;
import guru.qa.niffler.model.userdata.FriendshipStatus;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@RestTest
public class OAuthTest {

    private AuthApiClient authApiClient = new AuthApiClient();
    private static final Config CFG = Config.getInstance();

    @User
    @ApiLogin
    void oauthTest(@Token String token, UserJson userJson) {

        System.out.println("token -> " + token);
        assertNotNull(token);
    }

    @User(outcomeInvitation = 1)
    @ApiLogin
    void sendInvitationAccept(@Token String bearerToken, UserJson user) {

        String tokenUser = bearerToken;


        System.out.println("токен пользователя :" + tokenUser);

        String usernameUser = user.username();
        System.out.println("имя основного пользователя: " + usernameUser);
        String usernameFriend = user.testData().outcomeInvitations().get(0).username();
        System.out.println("имя кому отправляем запрос на дружбу: " + usernameFriend);
    }

}
