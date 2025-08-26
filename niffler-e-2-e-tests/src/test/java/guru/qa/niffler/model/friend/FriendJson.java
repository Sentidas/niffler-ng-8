package guru.qa.niffler.model.friend;

import com.fasterxml.jackson.annotation.JsonProperty;

public record FriendJson(
        @JsonProperty("username")
        String username
) {
}
