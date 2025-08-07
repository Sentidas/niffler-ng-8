package guru.qa.niffler.service;

import guru.qa.niffler.api.core.ThreadSafeCookieStore;
import guru.qa.niffler.config.Config;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang.ArrayUtils;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;

public abstract class RestClient {

    protected static final Config CFG = Config.getInstance();

    private final OkHttpClient okHttpClient;
    protected final Retrofit retrofit;

    public RestClient(String baseUrl) {
        this(baseUrl, false, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS);
    }

    public RestClient(String baseUrl, boolean followRedirect) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS);
    }

    public RestClient(String baseUrl, boolean followRedirect,  @Nullable Interceptor... interceptors) {
        this(baseUrl, followRedirect, JacksonConverterFactory.create(), HttpLoggingInterceptor.Level.HEADERS, interceptors);
    }

    public RestClient(String baseUrl, Converter.Factory factory) {
        this(baseUrl, false, factory, HttpLoggingInterceptor.Level.HEADERS);
    }

    public RestClient(String baseUrl, boolean followRedirect,
                      Converter.Factory factory,
                      HttpLoggingInterceptor.Level level,
                      @Nullable Interceptor... interceptors) {

        final OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .followRedirects(followRedirect)
                // Allure interceptor
                .addNetworkInterceptor(new AllureOkHttp3()
                        .setRequestTemplate("http-request.ftl")
                        .setResponseTemplate("http-response.ftl"));
       // Other interceptors
        if (ArrayUtils.isNotEmpty(interceptors)) {
            for (Interceptor interceptor : interceptors) {
                builder.addNetworkInterceptor(interceptor);
            }
        }
        // Logging
        builder.addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(level));
        // Cookie management
        builder.cookieJar(
                new JavaNetCookieJar(
                        new CookieManager(
                                ThreadSafeCookieStore.INSTANCE,
                                CookiePolicy.ACCEPT_ALL
                        )
                )
        );

        this.okHttpClient = builder.build();
        this.retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(factory)
                .build();
    }

    @Nonnull
    public <T> T create(final Class<T> service) {
        return this.retrofit.create(service);
    }

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

    public static final class DefaultRestClient extends RestClient {

        public DefaultRestClient(String baseUrl, boolean followRedirect) {
            super(baseUrl, followRedirect);
        }

        public DefaultRestClient(String baseUrl) {
            super(baseUrl);
        }

        public DefaultRestClient(String baseUrl, Converter.Factory factory) {
            super(baseUrl, factory);
        }

        public DefaultRestClient(String baseUrl, boolean followRedirect, Converter.Factory factory, HttpLoggingInterceptor.Level level, Interceptor... interceptors) {
            super(baseUrl, followRedirect, factory, level, interceptors);
        }
    }
}

