package com.example.da1.api;

import com.example.da1.admin.voucher.Voucher;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface VoucherApiService {
    @GET("vouchers")
    Call<ApiResponse<List<Voucher>>> getAllVouchers(
            @Query("active") Boolean active
    );

    @GET("vouchers/{id}")
    Call<ApiResponse<Voucher>> getVoucherById(@Path("id") String id);

    @GET("vouchers/validate")
    Call<ApiResponse<Voucher>> validateVoucher(@Query("code") String code);

    @POST("vouchers")
    Call<ApiResponse<Voucher>> createVoucher(@Body Voucher voucher);

    @PUT("vouchers/{id}")
    Call<ApiResponse<Voucher>> updateVoucher(
            @Path("id") String id,
            @Body Voucher voucher
    );

    @DELETE("vouchers/{id}")
    Call<ApiResponse<Void>> deleteVoucher(@Path("id") String id);
}


