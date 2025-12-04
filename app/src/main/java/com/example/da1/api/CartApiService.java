package com.example.da1.api;

import com.example.da1.models.AddToCartRequest;
import com.example.da1.models.CartItemResponse;
import com.example.da1.models.CartResponse;
import com.example.da1.models.UpdateCartRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CartApiService {
    @GET("cart")
    Call<ApiResponse<CartResponse>> getCartItems();

    @POST("cart/add")
    Call<ApiResponse<CartItemResponse>> addToCart(@Body AddToCartRequest request);

    @PUT("cart/update/{itemId}")
    Call<ApiResponse<CartItemResponse>> updateCartItem(
            @Path("itemId") String itemId,
            @Body UpdateCartRequest request
    );

    @DELETE("cart/remove/{itemId}")
    Call<ApiResponse<Void>> removeFromCart(@Path("itemId") String itemId);

    @DELETE("cart/clear")
    Call<ApiResponse<Void>> clearCart();
}


