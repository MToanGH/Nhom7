package com.example.da1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.adapters.AddressAdapter;
import com.example.da1.adapters.CartAdapter;
import com.example.da1.api.ApiClient;
import com.example.da1.api.ApiResponse;
import com.example.da1.api.ApiService;
import com.example.da1.models.Address;
import com.example.da1.models.CartItem;
import com.example.da1.models.CartItemResponse;
import com.example.da1.models.CartResponse;
import com.example.da1.models.CreateOrderRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private ImageButton btnEditAddress;
    private TextView tvAddress;
    private RecyclerView recyclerViewOrderItems;
    private Button btnContinuePayment;
    
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;
    private Address selectedAddress;
    private List<Address> addressList;
    private String selectedPaymentMethod = "card"; // Default payment method

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Khởi tạo ApiClient
        ApiClient.init(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        loadAddresses();
        loadCartItems();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        btnEditAddress = findViewById(R.id.btnEditAddress);
        tvAddress = findViewById(R.id.tvAddress);
        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        btnContinuePayment = findViewById(R.id.btnContinuePayment);
    }

    private void setupRecyclerView() {
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList);
        
        // Bật read-only mode để ẩn các nút delete và disable quantity change
        cartAdapter.setReadOnlyMode(true);

        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrderItems.setAdapter(cartAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnEditAddress.setOnClickListener(v -> {
            showAddressDialog();
        });

        btnContinuePayment.setOnClickListener(v -> {
            if (cartItemList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (selectedAddress == null) {
                Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
                return;
            }
            
            createOrder();
        });
    }

    private void loadAddresses() {
        Call<ApiResponse<List<Address>>> call = ApiService.getAddressApiService().getAllAddresses();
        
        call.enqueue(new Callback<ApiResponse<List<Address>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Address>>> call, 
                                 Response<ApiResponse<List<Address>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Address>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        addressList = apiResponse.getData();
                        
                        // Chọn địa chỉ mặc định hoặc địa chỉ đầu tiên
                        if (!addressList.isEmpty()) {
                            for (Address address : addressList) {
                                if (address.isDefault()) {
                                    selectedAddress = address;
                                    break;
                                }
                            }
                            if (selectedAddress == null) {
                                selectedAddress = addressList.get(0);
                            }
                            displayAddress(selectedAddress);
                        } else {
                            tvAddress.setText("Chưa có địa chỉ. Vui lòng thêm địa chỉ");
                        }
                    }
                } else {
                    Log.e("CheckoutActivity", "Error loading addresses: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Address>>> call, Throwable t) {
                Log.e("CheckoutActivity", "Error loading addresses", t);
                tvAddress.setText("Chưa có địa chỉ");
            }
        });
    }

    private void displayAddress(Address address) {
        if (address != null) {
            String fullAddress = address.getFullAddress();
            if (fullAddress.isEmpty()) {
                fullAddress = address.getAddress();
            }
            tvAddress.setText(fullAddress);
        } else {
            tvAddress.setText("Chưa có địa chỉ");
        }
    }

    private void loadCartItems() {
        Call<ApiResponse<CartResponse>> call = ApiService.getCartApiService().getCartItems();
        
        call.enqueue(new Callback<ApiResponse<CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartResponse>> call, 
                                 Response<ApiResponse<CartResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        CartResponse cartData = apiResponse.getData();
                        
                        cartItemList.clear();
                        
                        if (cartData.getItems() != null && !cartData.getItems().isEmpty()) {
                            // Convert CartItemResponse to CartItem
                            for (CartItemResponse itemResponse : cartData.getItems()) {
                                CartItemResponse.ProductData productData = itemResponse.getProductid();
                                
                                if (productData == null) {
                                    Log.w("CheckoutActivity", "Skipping item with null product data");
                                    continue;
                                }
                                
                                String productName = productData.getName() != null ? productData.getName() : "Sản phẩm";
                                String productCode = productData.getId() != null ? productData.getId() : "";
                                String imageUrl = productData.getImage() != null ? productData.getImage() : "";
                                
                                double itemPrice = itemResponse.getPrice();
                                int itemQuantity = itemResponse.getQuantity();
                                
                                CartItem cartItem = new CartItem(
                                    itemResponse.getId(),
                                    itemResponse.getBillid(),
                                    productName,
                                    productCode,
                                    "",
                                    itemResponse.getColor() != null ? itemResponse.getColor() : "",
                                    itemResponse.getSize() != null ? itemResponse.getSize() : "",
                                    itemPrice,
                                    itemQuantity,
                                    imageUrl
                                );
                                
                                cartItemList.add(cartItem);
                            }
                            
                            cartAdapter.notifyDataSetChanged();
                            Log.d("CheckoutActivity", "Loaded " + cartItemList.size() + " items for checkout");
                        } else {
                            Toast.makeText(CheckoutActivity.this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                } else {
                    String errorMsg = "Lỗi tải giỏ hàng (Code: " + response.code() + ")";
                    if (response.code() == 401 || response.code() == 403) {
                        errorMsg = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại";
                        // Chuyển đến màn hình đăng nhập
                        navigateToLogin();
                    }
                    Toast.makeText(CheckoutActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("CheckoutActivity", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                Log.e("CheckoutActivity", "Error loading cart", t);
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createOrder() {
        if (selectedAddress == null) {
            Toast.makeText(this, "Vui lòng chọn địa chỉ giao hàng", Toast.LENGTH_SHORT).show();
            return;
        }

        btnContinuePayment.setEnabled(false);
        btnContinuePayment.setText("Đang xử lý...");

        CreateOrderRequest request = new CreateOrderRequest(
            selectedAddress.getId(),
            selectedPaymentMethod,
            null // voucherCode - có thể thêm sau
        );

        Call<ApiResponse<com.example.da1.admin.order.Order>> call = 
            ApiService.getOrderApiService().createOrder(request);

        call.enqueue(new Callback<ApiResponse<com.example.da1.admin.order.Order>>() {
            @Override
            public void onResponse(Call<ApiResponse<com.example.da1.admin.order.Order>> call, 
                                 Response<ApiResponse<com.example.da1.admin.order.Order>> response) {
                btnContinuePayment.setEnabled(true);
                btnContinuePayment.setText("Continue to payment");

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<com.example.da1.admin.order.Order> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_SHORT).show();
                        
                        // Chuyển đến màn hình đơn hàng hoặc quay về home
                        Intent intent = new Intent(CheckoutActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Đặt hàng thất bại";
                        Toast.makeText(CheckoutActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi đặt hàng (Code: " + response.code() + ")";
                    if (response.code() == 401 || response.code() == 403) {
                        errorMsg = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại";
                        navigateToLogin();
                    }
                    Toast.makeText(CheckoutActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("CheckoutActivity", "Error creating order: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<com.example.da1.admin.order.Order>> call, Throwable t) {
                btnContinuePayment.setEnabled(true);
                btnContinuePayment.setText("Continue to payment");
                Log.e("CheckoutActivity", "Error creating order", t);
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToLogin() {
        Intent intent = new Intent(CheckoutActivity.this, com.example.da1.auth.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void showAddressDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_address, null);
        
        RecyclerView recyclerViewAddresses = dialogView.findViewById(R.id.recyclerViewAddresses);
        TextInputLayout tilFullName = dialogView.findViewById(R.id.tilFullName);
        TextInputLayout tilPhone = dialogView.findViewById(R.id.tilPhone);
        TextInputLayout tilAddress = dialogView.findViewById(R.id.tilAddress);
        TextInputLayout tilCity = dialogView.findViewById(R.id.tilCity);
        TextInputLayout tilDistrict = dialogView.findViewById(R.id.tilDistrict);
        TextInputLayout tilWard = dialogView.findViewById(R.id.tilWard);
        
        TextInputEditText etFullName = dialogView.findViewById(R.id.etFullName);
        TextInputEditText etPhone = dialogView.findViewById(R.id.etPhone);
        TextInputEditText etAddress = dialogView.findViewById(R.id.etAddress);
        TextInputEditText etCity = dialogView.findViewById(R.id.etCity);
        TextInputEditText etDistrict = dialogView.findViewById(R.id.etDistrict);
        TextInputEditText etWard = dialogView.findViewById(R.id.etWard);
        
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        
        // Tạo dialog trước để có thể sử dụng trong lambda
        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create();
        
        // Setup RecyclerView cho danh sách địa chỉ
        List<Address> addresses = addressList != null ? addressList : new ArrayList<>();
        AddressAdapter addressAdapter = new AddressAdapter(addresses);
        addressAdapter.setSelectedAddress(selectedAddress);
        addressAdapter.setOnAddressSelectedListener(address -> {
            selectedAddress = address;
            addressAdapter.setSelectedAddress(address);
            displayAddress(address);
            // Tự động đóng dialog sau khi chọn
            dialog.dismiss();
        });
        
        recyclerViewAddresses.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewAddresses.setAdapter(addressAdapter);
        
        // Ẩn RecyclerView nếu không có địa chỉ
        if (addresses.isEmpty()) {
            recyclerViewAddresses.setVisibility(View.GONE);
        }
        
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        
        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String address = etAddress.getText().toString().trim();
            String city = etCity.getText().toString().trim();
            String district = etDistrict.getText().toString().trim();
            String ward = etWard.getText().toString().trim();
            
            // Validate
            if (TextUtils.isEmpty(fullName)) {
                tilFullName.setError("Vui lòng nhập họ và tên");
                return;
            }
            if (TextUtils.isEmpty(phone)) {
                tilPhone.setError("Vui lòng nhập số điện thoại");
                return;
            }
            if (TextUtils.isEmpty(address)) {
                tilAddress.setError("Vui lòng nhập địa chỉ");
                return;
            }
            
            // Clear errors
            tilFullName.setError(null);
            tilPhone.setError(null);
            tilAddress.setError(null);
            
            // Tạo địa chỉ mới
            Address newAddress = new Address();
            newAddress.setFullName(fullName);
            newAddress.setPhone(phone);
            newAddress.setAddress(address);
            newAddress.setCity(city);
            newAddress.setDistrict(district);
            newAddress.setWard(ward);
            newAddress.setDefault(false);
            
            // Lưu địa chỉ mới qua API
            createNewAddress(newAddress, dialog);
        });
        
        dialog.show();
    }
    
    private void createNewAddress(Address address, AlertDialog dialog) {
        Call<ApiResponse<Address>> call = ApiService.getAddressApiService().createAddress(address);
        
        call.enqueue(new Callback<ApiResponse<Address>>() {
            @Override
            public void onResponse(Call<ApiResponse<Address>> call, 
                                 Response<ApiResponse<Address>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Address> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Address savedAddress = apiResponse.getData();
                        selectedAddress = savedAddress;
                        displayAddress(savedAddress);
                        
                        // Reload addresses list
                        loadAddresses();
                        
                        dialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, "Đã lưu địa chỉ", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Lỗi lưu địa chỉ";
                        Toast.makeText(CheckoutActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(CheckoutActivity.this, "Lỗi lưu địa chỉ (Code: " + response.code() + ")", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Address>> call, Throwable t) {
                Log.e("CheckoutActivity", "Error creating address", t);
                Toast.makeText(CheckoutActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}

