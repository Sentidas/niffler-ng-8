package guru.qa.niffler.model.userdata;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UdUserEntity;
import guru.qa.niffler.model.spend.CurrencyValues;

import java.util.UUID;

public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("currency")
        CurrencyValues currency,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("full_name")
        String fullname,
        @JsonProperty("photo")
        byte[] photo,
        @JsonProperty("photo_small")
        byte[] photoSmall
) {

    public static UserJson fromEntity(UdUserEntity entity) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getCurrency(),
                entity.getFirstname(),
                entity.getSurname(),
                entity.getFullname(),
                entity.getPhoto(),
                entity.getPhotoSmall()
        );
    }
}
