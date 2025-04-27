package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.auth.AuthorityJson;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthorityEntity {

    private UUID userId;
    private AuthUserEntity user;
    private Authority authority;

    public static AuthorityEntity fromJson(AuthorityJson json) {
        AuthorityEntity ae = new AuthorityEntity();
        ae.setUserId(json.id());
        ae.setUser(json.user());
        ae.setAuthority(json.authority());
        return ae;
    }
}
