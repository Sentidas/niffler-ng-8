package guru.qa.niffler.api;

import jaxb.userdata.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserdataSoapApi {

    @Headers({
            "Content-type:text/xml",
            "Accept-Charset:utf-8"
    })
    @POST("/ws")
    Call<UserResponse> currentUser(@Body CurrentUserRequest currentUserRequest);

    @Headers({
            "Content-type:text/xml",
            "Accept-Charset:utf-8"
    })
    @POST("/ws")
    Call<UsersResponse> allUsers(@Body AllUsersRequest allUsersRequest);

    @Headers({
            "Content-type:text/xml",
            "Accept-Charset:utf-8"
    })

    @POST("/ws")
    Call<UsersResponse> friendsPageable(@Body FriendsPageRequest friendsPageRequest);

    @Headers({
            "Content-type:text/xml",
            "Accept-Charset:utf-8"
    })
    @POST("/ws")
    Call<UsersResponse> friends(@Body FriendsRequest friendsPageRequest);

    @Headers({
            "Content-type:text/xml",
            "Accept-Charset:utf-8"
    })
    @POST("/ws")
    Call<UserResponse> acceptInvitation(@Body AcceptInvitationRequest acceptInvitationRequest);

    @Headers({
            "Content-type:text/xml",
            "Accept-Charset:utf-8"
    })
    @POST("/ws")
    Call<UserResponse> declineInvitation(@Body DeclineInvitationRequest acceptInvitationRequest);

    @Headers({
            "Content-type:text/xml",
            "Accept-Charset:utf-8"
    })
    @POST("/ws")
    Call<UserResponse> sendInvitation(@Body SendInvitationRequest sendInvitationRequest);

    @Headers({
            "Content-type:text/xml",
            "Accept-Charset:utf-8"
    })
    @POST("/ws")
    Call<Void> removeFriend(@Body RemoveFriendRequest removeFriendRequest);
}
