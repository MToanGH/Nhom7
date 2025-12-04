package com.example.da1.api;

import com.example.da1.models.CategoryItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CategoryApiService {
    @GET("categories")
    Call<ApiResponse<List<CategoryItem>>> getAllCategories();

    @GET("categories/{id}")
    Call<ApiResponse<CategoryItem>> getCategoryById(@Path("id") String id);

    @POST("categories")
    Call<ApiResponse<CategoryItem>> createCategory(@Body CategoryItem category);

    @PUT("categories/{id}")
    Call<ApiResponse<CategoryItem>> updateCategory(
            @Path("id") String id,
            @Body CategoryItem category
    );

    @DELETE("categories/{id}")
    Call<ApiResponse<Void>> deleteCategory(@Path("id") String id);
}


