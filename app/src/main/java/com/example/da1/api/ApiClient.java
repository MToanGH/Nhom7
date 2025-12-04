package com.example.da1.api;

import android.content.Context;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class ApiClient {
    // URL API - Thay đổi IP này thành IP máy tính của bạn
    // Để lấy IP: chạy lệnh "ipconfig" trong CMD, tìm IPv4 Address
    // Hoặc dùng "localhost" nếu chạy trên emulator
    private static final String BASE_URL = "http://10.0.2.2:3002/api/ecommerce/"; // Cho Android Emulator
    // private static final String BASE_URL = "http://192.168.1.XXX:3002/api/ecommerce/"; // Cho thiết bị thật (thay XXX bằng IP máy bạn)
    private static Retrofit retrofit = null;
    private static Context appContext = null;

    public static void init(Context context) {
        appContext = context.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Logging interceptor để debug API calls
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);

            // Thêm AuthInterceptor nếu có context
            if (appContext != null) {
                clientBuilder.addInterceptor(new AuthInterceptor(appContext));
            }

            OkHttpClient okHttpClient = clientBuilder.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void resetClient() {
        retrofit = null;
    }

    public static void setBaseUrl(String baseUrl) {
        retrofit = null;
        // Có thể tạo method để thay đổi BASE_URL động nếu cần
    }
}


