package guru.qa.niffler.model.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.auth.AuthUserEntity;

import java.util.List;
import java.util.UUID;

public record AuthUserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("password")
        String password,
        @JsonProperty("enabled")
        Boolean enabled,
        @JsonProperty("account_non_expired")
        Boolean accountNonExpired,
        @JsonProperty("account_non_locked")
        Boolean accountNonLocked,
        @JsonProperty("credentials_non_expired")
        Boolean credentialsNonExpired,
        @JsonProperty("authorities")
        List<String> authorities
) {
        public static AuthUserJson fromEntity(AuthUserEntity entity) {
                List<String> roles = entity.getAuthorities().stream()
                        .map(a -> a.getAuthority().name())
                        .toList();

                return new AuthUserJson(
                        entity.getId(),
                        entity.getUsername(),
                        entity.getPassword(),
                        entity.getAccountNonExpired(),
                        entity.getAccountNonLocked(),
                        entity.getCredentialsNonExpired(),
                        entity.getEnabled(),
                        roles
                );
        }

//        public AuthUserJson withEncodedPassword(String encodedPassword) {
//                return new AuthUserJson(
//                        this.id,
//                        this.username,
//                        encodedPassword,
//                        this.enabled,
//                        this.accountNonExpired,
//                        this.accountNonExpired,
//                        this.credentialsNonExpired
//                );
//        }
}
