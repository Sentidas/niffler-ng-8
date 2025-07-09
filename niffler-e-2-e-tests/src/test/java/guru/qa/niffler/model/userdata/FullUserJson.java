package guru.qa.niffler.model.userdata;

import guru.qa.niffler.data.entity.auth.AuthUserEntity;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.spend.CurrencyValues;

import java.util.List;
import java.util.UUID;

public record FullUserJson(
        UUID id,
        String username,
        CurrencyValues currency,
        String firstname,
        String surname,
        String fullname,
        byte[] photo,
        byte[] photoSmall,
        List<String> authorities
) {
    public static FullUserJson fromEntity(AuthUserEntity auth, UserEntity user) {
        List<String> roles = auth.getAuthorities().stream()
                .map(a -> a.getAuthority().name())
                .toList();

        return new FullUserJson(
                user.getId(),
                user.getUsername(),
                user.getCurrency(),
                user.getFirstname(),
                user.getSurname(),
                user.getFullname(),
                user.getPhoto(),
                user.getPhotoSmall(),
                roles
        );
    }
}
