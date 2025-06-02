package com.example.aplicaciongestionstockimprenta.network;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit retrofit;
    private static OkHttpClient client;

    private static final CookieManager cookieManager = new CookieManager();

    static {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
    }

    public static OkHttpClient provideClientWithCookieJar() {
        if (client == null) {
            client = new OkHttpClient.Builder()
                    .cookieJar(new JavaNetCookieJar(cookieManager))
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .retryOnConnectionFailure(true)
                    .build();
        }
        return client;
    }

    public static OdooService getOdooService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl("http://50.85.209.163:8069/")
                    .client(provideClientWithCookieJar())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(OdooService.class);
    }
}
