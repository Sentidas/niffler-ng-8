package guru.qa.niffler.api.core;

import guru.qa.niffler.model.userdata.UserJson;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nullable;
import java.util.List;

public interface GetawayApi {

    @GET("/api/friends/all")
    Call<List<UserJson>> allFriends(@Header("Authorization") String authToken,
                                    @Query("searchQuery") @Nullable String searchQuery
    );
}
