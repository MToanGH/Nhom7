package com.example.da1.api;

import com.example.da1.admin.user.User;
import com.example.da1.models.LoginResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {
    @GET("users/me")
    Call<ApiResponse<LoginResponse.UserData>> getCurrentUser();

    @GET("users")
    Call<ApiResponse<List<User>>> getAllUsers(
            @Query("role") String role,
            @Query("search") String search
    );

    @GET("users/{id}")
    Call<ApiResponse<User>> getUserById(@Path("id") String id);

    @POST("users")
    Call<ApiResponse<User>> createUser(@Body User user);

    @PUT("users/me")
    Call<ApiResponse<LoginResponse.UserData>> updateCurrentUser(@Body LoginResponse.UserData user);

    @PUT("users/{id}")
    Call<ApiResponse<User>> updateUser(
            @Path("id") String id,
            @Body User user
    );

    @DELETE("users/{id}")
    Call<ApiResponse<Void>> deleteUser(@Path("id") String id);
}


