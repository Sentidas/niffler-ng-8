package guru.qa.niffler.api.core;

import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.userdata.UserJson;
import org.springframework.data.domain.Page;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nullable;
import java.util.List;

public interface GetawayV2Api {

    @GET("/api/v2/friends/all")
    Call<RestResponsePage<UserJson>> allFriends(@Header("Authorization") String authToken,
                                                @Query("page") int page,
                                                @Query("size") int size,
                                                @Query("searchQuery") @Nullable String searchQuery
    );
}
