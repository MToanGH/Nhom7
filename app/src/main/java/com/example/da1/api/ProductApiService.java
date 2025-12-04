package com.example.da1.api;

import com.example.da1.models.ProductItem;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ProductApiService {
    @GET("products")
    Call<ApiResponse<ProductListResponse>> getAllProducts(
            @Query("categoryid") String categoryId,
            @Query("search") String search,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("products/{id}")
    Call<ApiResponse<ProductItem>> getProductById(@Path("id") String id);

    @POST("products")
    Call<ApiResponse<ProductItem>> createProduct(@Body ProductItem product);

    @PUT("products/{id}")
    Call<ApiResponse<ProductItem>> updateProduct(
            @Path("id") String id,
            @Body ProductItem product
    );

    @DELETE("products/{id}")
    Call<ApiResponse<Void>> deleteProduct(@Path("id") String id);
}


