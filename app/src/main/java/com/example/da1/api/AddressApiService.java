package com.example.da1.api;

import com.example.da1.models.Address;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;

public interface AddressApiService {
    @GET("addresses")
    Call<ApiResponse<List<Address>>> getAllAddresses();

    @POST("addresses")
    Call<ApiResponse<Address>> createAddress(@Body Address address);

    @PUT("addresses/{id}")
    Call<ApiResponse<Address>> updateAddress(@Path("id") String id, @Body Address address);

    @DELETE("addresses/{id}")
    Call<ApiResponse<Void>> deleteAddress(@Path("id") String id);
}




