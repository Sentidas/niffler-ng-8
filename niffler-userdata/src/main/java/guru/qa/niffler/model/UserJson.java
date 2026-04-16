package guru.qa.niffler.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.niffler.data.CurrencyValues;
import guru.qa.niffler.data.UserEntity;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jaxb.userdata.Currency;
import jaxb.userdata.User;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserJson(
        @JsonProperty("id")
        UUID id,
        @JsonProperty("username")
        String username,
        @JsonProperty("firstname")
        String firstname,
        @JsonProperty("surname")
        String surname,
        @JsonProperty("fullname")
        String fullname,
        @JsonProperty("currency")
        CurrencyValues currency,
        @JsonProperty("photo")
        String photo,
        @JsonProperty("photoSmall")
        String photoSmall,
        @JsonProperty("friendshipStatus")
        FriendshipStatus friendshipStatus) implements IUserJson {

    public @Nonnull User toJaxbUser() {
        User jaxbUser = new User();
        jaxbUser.setId(id != null ? id.toString() : null);
        jaxbUser.setUsername(username);
        jaxbUser.setFirstname(firstname);
        jaxbUser.setSurname(surname);
        jaxbUser.setFullname(fullname);
        jaxbUser.setCurrency(currency != null ? Currency.valueOf(currency.name()) : null);
        jaxbUser.setPhoto(photo);
        jaxbUser.setPhotoSmall(photoSmall);
        jaxbUser.setFriendshipStatus(friendshipStatus() == null ?
                jaxb.userdata.FriendshipStatus.VOID :
                jaxb.userdata.FriendshipStatus.valueOf(friendshipStatus().name()));
        return jaxbUser;
    }

    public static @Nonnull UserJson fromJaxb(@Nonnull User jaxbUser) {
        return new UserJson(
                jaxbUser.getId() != null ? UUID.fromString(jaxbUser.getId()) : null,
                jaxbUser.getUsername(),
                jaxbUser.getFirstname(),
                jaxbUser.getSurname(),
                jaxbUser.getFullname(),
                jaxbUser.getCurrency() != null ? CurrencyValues.valueOf(jaxbUser.getCurrency().name()) : null,
                jaxbUser.getPhoto(),
                jaxbUser.getPhotoSmall(),
                (jaxbUser.getFriendshipStatus() != null && jaxbUser.getFriendshipStatus() != jaxb.userdata.FriendshipStatus.VOID)
                        ? FriendshipStatus.valueOf(jaxbUser.getFriendshipStatus().name())
                        : null
        );
    }

    public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity, @Nullable FriendshipStatus friendshipStatus) {
        return new UserJson(
                entity.getId(),
                entity.getUsername(),
                entity.getFirstname(),
                entity.getSurname(),
                entity.getFullname(),
                entity.getCurrency(),
                entity.getPhoto() != null && entity.getPhoto().length > 0 ? new String(entity.getPhoto(), StandardCharsets.UTF_8) : null,
                entity.getPhotoSmall() != null && entity.getPhotoSmall().length > 0 ? new String(entity.getPhotoSmall(), StandardCharsets.UTF_8) : null,
                friendshipStatus
        );
    }

    public static @Nonnull UserJson fromEntity(@Nonnull UserEntity entity) {
        return fromEntity(entity, null);
    }

    public static @Nonnull UserJson fromProto(@Nonnull guru.qa.niffler.grpc.UserRequest r) {
        return new UserJson(
                r.getId().isEmpty() ? null : java.util.UUID.fromString(r.getId()),
                emptyToNull(r.getUsername()),
                r.hasFirstname() ? emptyToNull(r.getFirstname()) : null,
                r.hasSurname() ? emptyToNull(r.getSurname()) : null,
                r.hasFullname() ? emptyToNull(r.getFullname()) : null,
                r.hasCurrency() ? guru.qa.niffler.data.CurrencyValues.valueOf(r.getCurrency().name()) : null,
                r.hasPhoto() ? emptyToNull(r.getPhoto()) : null,
                r.hasPhotoSmall() ? emptyToNull(r.getPhotoSmall()) : null,
                null
        );
    }

    public void toProto(guru.qa.niffler.grpc.UserResponse.Builder b) {
        if (id != null) b.setId(id.toString());
        if (username != null) b.setUsername(username);
        if (firstname != null) b.setFirstname(firstname);
        if (surname != null) b.setSurname(surname);
        if (fullname != null) b.setFullname(fullname);

        b.setCurrency(currency != null
                ? guru.qa.niffler.grpc.CurrencyValues.valueOf(currency.name())
                : guru.qa.niffler.grpc.CurrencyValues.UNSPECIFIED);

        if (photo != null) b.setPhoto(photo);
        if (photoSmall != null) b.setPhotoSmall(photoSmall);

        b.setFriendshipStatus(friendshipStatus != null
                ? guru.qa.niffler.grpc.FriendshipStatus.valueOf(friendshipStatus.name())
                : guru.qa.niffler.grpc.FriendshipStatus.UNDEFINED);
    }

    private static String emptyToNull(String s) {
        return (s == null || s.isEmpty()) ? null : s;
    }

}
