package com.example.da1.api;

import com.example.da1.models.ChangePasswordRequest;
import com.example.da1.models.ForgotPasswordRequest;
import com.example.da1.models.LoginRequest;
import com.example.da1.models.LoginResponse;
import com.example.da1.models.RegisterRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface AuthApiService {
    @POST("auth/login")
    Call<ApiResponse<LoginResponse>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<LoginResponse>> register(@Body RegisterRequest request);

    @POST("auth/logout")
    Call<ApiResponse<Void>> logout(@Header("Authorization") String token);

    @POST("auth/forgot-password")
    Call<ApiResponse<Void>> forgotPassword(@Body ForgotPasswordRequest request);

    @PUT("auth/change-password")
    Call<ApiResponse<Void>> changePassword(@Body ChangePasswordRequest request);
}


