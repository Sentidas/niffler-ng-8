package guru.qa.niffler.model.userdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.spend.CurrencyValues;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
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
        byte[] photoSmall,
        @JsonIgnore
        TestData testData) {

    public static UserJson fromEntity(UserEntity entity) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getCurrency(),
                entity.getFirstname(),
                entity.getSurname(),
                entity.getFullname(),
                entity.getPhoto(),
                entity.getPhotoSmall(),
                new TestData(
                        null,
                        new ArrayList<>(),
                        new ArrayList<>()
                )
        );
    }

    public UserJson withPassword(String password) {
        return withTestData(
                new TestData(
                        password,
                        testData.categories(),
                        testData.spends()
                )
        );
    }

    public UserJson withTestData(TestData testData) {
        return new UserJson(
                id,
                username,
                currency,
                firstname,
                surname,
                fullname,
                photo,
                photoSmall,
                testData
        );
    }
}
