package guru.qa.niffler.api;

import guru.qa.niffler.model.friend.FriendJson;
import guru.qa.niffler.model.userdata.UserJson;
import retrofit2.Call;
import retrofit2.http.*;

import javax.annotation.Nullable;
import java.util.List;

public interface GetawayApi {

    @GET("/api/friends/all")
    Call<List<UserJson>> allFriends(@Header("Authorization") String bearerToken,
                                    @Query("searchQuery") @Nullable String searchQuery
    );

    @DELETE("/api/friends/remove")
    Call<Void> removeFriend(@Header("Authorization") String bearerToken,
                            @Query("username") String targetUsername
    );

    @POST("/api/invitations/send")
    Call<UserJson> sendInvitation(@Header("Authorization") String bearerToken,
                                  @Body FriendJson usernameFriend
    );

    @POST("/api/invitations/accept")
    Call<UserJson> acceptInvitation(@Header("Authorization") String bearerToken,
                                    @Body FriendJson invitation
    );

    @POST("/api/invitations/decline")
    Call<UserJson> declineInvitation(@Header("Authorization") String bearerToken,
                                     @Body FriendJson invitation
    );

    @GET("/api/users/all")
    Call<List<UserJson>> allUsers(@Header("Authorization") String bearerToken,
                            @Query("searchQuery") String targetUsername);
}
