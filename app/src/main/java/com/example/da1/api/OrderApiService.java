package com.example.da1.api;

import com.example.da1.admin.order.Order;
import com.example.da1.models.CreateOrderRequest;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface OrderApiService {
    @GET("orders")
    Call<ApiResponse<List<Order>>> getAllOrders(
            @Query("status") String status,
            @Query("page") int page,
            @Query("limit") int limit
    );

    @GET("orders/{id}")
    Call<ApiResponse<Order>> getOrderById(@Path("id") String id);

    @POST("orders")
    Call<ApiResponse<Order>> createOrder(@Body CreateOrderRequest request);

    @PUT("orders/{id}/status")
    Call<ApiResponse<Order>> updateOrderStatus(
            @Path("id") String id,
            @Query("status") String status
    );
}


