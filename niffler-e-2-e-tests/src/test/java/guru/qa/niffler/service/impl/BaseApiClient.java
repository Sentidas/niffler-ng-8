package guru.qa.niffler.service.impl;

import retrofit2.Call;
import retrofit2.Response;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.io.IOException;

@ParametersAreNonnullByDefault
public class BaseApiClient {

    @Nonnull
    protected <T> T execute(Call<T> call) {
        try {
            Response<T> response = call.execute();

            if (!response.isSuccessful()) {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                throw new RuntimeException("Unexpected response code: " + response.code() + ". " + errorBody);
            }
            T body = response.body();

            if (body == null) {
                throw new RuntimeException("Expected non-null response body but received null");
            }

            return body;

        } catch (IOException e) {
            throw new RuntimeException("Failed to execute request", e);
        }
    }

    protected <T> void executeWithoutBody(Call<T> call) {
        try {
            Response<T> response = call.execute();

            if (!response.isSuccessful()) {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "No error body";
                throw new RuntimeException("Unexpected response code: " + response.code() + ". " + errorBody);
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to execute request", e);
        }
    }
}
