package guru.qa.niffler.data.entity.userdata;

import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.userdata.UserJson;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter
@Setter
public class UdUserEntity implements Serializable {

    private UUID id;
    private String username;
    private CurrencyValues currency;
    private String firstname;
    private String surname;
    private String fullname;
    private byte[] photo;
    private byte[] photoSmall;

    public static UdUserEntity fromJson(UserJson json) {
        UdUserEntity ue = new UdUserEntity();
        ue.setId(json.id());
        ue.setUsername(json.username());
        ue.setCurrency(json.currency());
        ue.setFirstname(json.firstname());
        ue.setSurname(json.surname());
        ue.setFullname(json.fullname());
        ue.setPhoto(json.photo());
        ue.setPhotoSmall(json.photoSmall());
        return ue;
    }

}
