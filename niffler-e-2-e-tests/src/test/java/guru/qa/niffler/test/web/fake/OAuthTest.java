package guru.qa.niffler.test.web.fake;

import guru.qa.niffler.config.Config;
import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.service.impl.AuthApiClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OAuthTest {

    private AuthApiClient authApiClient = new AuthApiClient();
    private static final Config CFG = Config.getInstance();

    @Test
    @ApiLogin(username = "duck", password = "12345")
    void oauthTest(@Token String token) {

        System.out.println("token -> " + token);
        assertNotNull(token);
    }
}
