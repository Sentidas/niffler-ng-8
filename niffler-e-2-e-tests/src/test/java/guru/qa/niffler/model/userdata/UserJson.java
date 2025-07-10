package guru.qa.niffler.model.userdata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.entity.userdata.UserEntity;
import guru.qa.niffler.model.spend.CurrencyValues;

import java.util.ArrayList;
import java.util.Base64;
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
        @JsonProperty("fullname")
        String fullname,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("photoSmall")
        String photoSmall,
        @JsonProperty("friendshipStatus")
        FriendshipStatus friendshipStatus,
        @JsonIgnore
        TestData testData
) {

    public static UserJson fromEntity(UserEntity entity, FriendshipStatus friendshipStatus) {


        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getCurrency(),
                entity.getFirstname(),
                entity.getSurname(),
                entity.getFullname(),

                entity.getPhoto() != null
                        ? "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getPhoto())
                        : null,

                entity.getPhotoSmall() != null
                        ? "data:image/png;base64," + Base64.getEncoder().encodeToString(entity.getPhotoSmall())
                        : null,
                friendshipStatus,
                new TestData(
                        null,
                        new ArrayList<>(),
                        new ArrayList<>(),
                        new ArrayList<>(),
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
                        testData.spends(),
                        testData.friends(),
                        testData.incomeInvitations(),
                        testData.outcomeInvitations()
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
                friendshipStatus,
                testData
        );
    }
}
