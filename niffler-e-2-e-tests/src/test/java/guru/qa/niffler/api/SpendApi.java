package guru.qa.niffler.api;

import guru.qa.niffler.model.spend.CategoryJson;
import guru.qa.niffler.model.spend.CurrencyValues;
import guru.qa.niffler.model.spend.SpendJson;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

public interface SpendApi {

  @POST("internal/spends/add")
  Call<SpendJson> addSpend(@Body SpendJson spend);

  @PATCH("internal/spends/edit")
  Call<SpendJson> editSpend(@Body SpendJson spend);

  @GET("internal/spends/{id}")
  Call<SpendJson> getSpend(
          @Path("id") String id,
          @Query("username") String username
          );

  @GET("internal/spends/all")
  Call<List<SpendJson>> getSpends(
          @Query("username") String username,
          @Query("filterCurrency") CurrencyValues filterCurrency,
          @Query("from") String fromDate,
          @Query("to") String toDate
          );

  @DELETE("internal/spends/remove")
    Call<Void> deleteSpends(
          @Query("username") String username,
          @Query("ids") List<String> ids
  );

  @POST("internal/categories/add")
  Call<CategoryJson> addCategory(@Body CategoryJson category);

  @PATCH("internal/categories/update")
  Call<CategoryJson> updateCategory(@Body CategoryJson category);

  @GET("internal/categories/all")
  Call<List<CategoryJson>> getCategories(
          @Query("username") String username,
          @Query("excludeArchived") Boolean excludeArchived
  );
}
