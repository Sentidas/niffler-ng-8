package guru.qa.niffler.api;

import guru.qa.niffler.model.pageable.RestResponsePage;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import guru.qa.niffler.model.userdata.UserJson;
import guru.qa.niffler.page.model.DataFilterValues;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

import javax.annotation.Nullable;

public interface GetawayV2Api {

    @GET("/api/v2/friends/all")
    Call<RestResponsePage<UserJson>> allFriends(@Header("Authorization") String bearerToken,
                                                @Query("page") int page,
                                                @Query("size") int size,
                                                @Query("searchQuery") @Nullable String searchQuery
    );

    @GET("/api/v2/users/all")
    Call<RestResponsePage<UserJson>> allUsers(@Header("Authorization") String bearerToken,
                                              @Query("page") int page,
                                              @Query("size") int size,
                                              @Query("searchQuery") @Nullable String searchQuery
    );

    @GET("/api/v2/spends/all")
    Call<RestResponsePage<SpendJson>> getSpends(@Header("Authorization") String bearerToken,
                                                @Query("page") int page,
                                                @Query("size") int size,
                                                @Query("filterPeriod") @Nullable DataFilterValues filterPeriod,
                                                @Query("filterCurrency") @Nullable CurrencyValues filterCurrency,
                                                @Query("searchQuery") @Nullable String searchQuery
    );

    @GET("/api/v2/stat/total")
    Call<RestResponsePage<SpendJson>> getSpends(@Header("Authorization") String bearerToken,
                                                @Query("page") int page, // уточнить
                                                @Query("size") int size,  // уточнить
                                                @Query("statCurrency") @Nullable CurrencyValues statCurrency,
                                                @Query("filterCurrency") @Nullable CurrencyValues filterCurrency,
                                                @Query("filterPeriod") @Nullable DataFilterValues filterPeriod
    );
}
