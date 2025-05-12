package guru.qa.niffler.data.entity.auth;

import guru.qa.niffler.model.auth.AuthorityJson;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AuthorityEntity {

    private UUID id;
    private UUID userId;
    private Authority authority;

//    public AuthorityEntity(AuthUserEntity user, Authority authority) {
//        this.userId = user;
//        this.authority = authority;
//    }

    public AuthorityEntity() {

    }

    public static AuthorityEntity fromJson(AuthorityJson json) {
        AuthorityEntity ae = new AuthorityEntity();
        ae.setId(json.id());
        ae.setUserId(json.user());
        ae.setAuthority(json.authority());
        return ae;
    }
}
