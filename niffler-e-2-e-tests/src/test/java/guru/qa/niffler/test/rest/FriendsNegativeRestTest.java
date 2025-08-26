package guru.qa.niffler.test.rest;

import guru.qa.niffler.jupiter.annotation.ApiLogin;
import guru.qa.niffler.jupiter.annotation.Token;
import guru.qa.niffler.jupiter.annotation.User;
import guru.qa.niffler.jupiter.annotation.meta.RestTest;
import guru.qa.niffler.jupiter.extension.ApiLoginExtension;
import guru.qa.niffler.model.userdata.UserJsonError;
import guru.qa.niffler.service.gateway.GatewayApiClient;
import org.junit.jupiter.api.extension.RegisterExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@RestTest
public class FriendsNegativeRestTest {

    @RegisterExtension
    static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

    private final GatewayApiClient gatewayApiClient = new GatewayApiClient();

    @User
    @ApiLogin
    void sendInvitation_shouldReturn400WhenUsernameIsBlank(@Token String bearerToken) {

        final UserJsonError response = gatewayApiClient.sendInvitationError(
                bearerToken,
                "",
                400
        );
        assertEquals("Username can not be blank", response.detail());
    }

    @User
    @ApiLogin
    void sendInvitation_shouldReturn400WhenUsernameExceedMaxLength(@Token String bearerToken) {

        final UserJsonError response = gatewayApiClient.sendInvitationError(
                bearerToken,
                "7fA3pZ9qB2rY8sC1tX6uD0vW5wE4xU7yF8zG1hI5jKрlohgigig",
                400
        );
        assertEquals("Username can`t be longer than 50 characters", response.detail());
    }

    @User
    @ApiLogin
    void sendInvitation_shouldReturn404WhenUsernameNotFound(@Token String bearerToken) {
        final String friendName = "UsernameNotFound";

        final UserJsonError response = gatewayApiClient.sendInvitationError(
                bearerToken,
                friendName,
                404
        );
        assertEquals("Can`t find user by username: '" + friendName + "'", response.detail());
    }
}
