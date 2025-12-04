package com.example.da1;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.adapters.CartAdapter;
import com.example.da1.api.ApiClient;
import com.example.da1.api.ApiResponse;
import com.example.da1.api.ApiService;
import com.example.da1.models.CartItem;
import com.example.da1.models.CartItemResponse;
import com.example.da1.models.CartResponse;
import com.example.da1.models.UpdateCartRequest;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {
    private ImageButton btnBack;
    private RecyclerView recyclerViewCart;
    private TextView tvSubtotal;
    private ImageButton btnExpandSubtotal;
    private Button btnPurchase;
    private ProgressBar progressBar;
    private CartAdapter cartAdapter;
    private List<CartItem> cartItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        // Khởi tạo ApiClient
        ApiClient.init(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        // Set initial subtotal to 0
        updateSubtotal();
        // Load cart items from API
        loadCartItems();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btnBack);
        recyclerViewCart = findViewById(R.id.recyclerViewCart);
        tvSubtotal = findViewById(R.id.tvSubtotal);
        btnExpandSubtotal = findViewById(R.id.btnExpandSubtotal);
        btnPurchase = findViewById(R.id.btnPurchase);
        progressBar = findViewById(R.id.progressBar);
        
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerView() {
        cartItemList = new ArrayList<>();
        cartAdapter = new CartAdapter(cartItemList);
        
        cartAdapter.setQuantityChangeListener((item, newQuantity) -> {
            // Cập nhật quantity qua API
            updateCartItemQuantity(item, newQuantity);
        });
        
        cartAdapter.setDeleteItemListener(item -> {
            // Gọi API để xóa item trong database
            deleteCartItem(item);
        });

        recyclerViewCart.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCart.setAdapter(cartAdapter);
    }

    private void loadCartItems() {
        // Đảm bảo giỏ hàng bắt đầu trống
        cartItemList.clear();
        cartAdapter.notifyDataSetChanged();
        updateSubtotal();
        
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
        
        Call<ApiResponse<CartResponse>> call = ApiService.getCartApiService().getCartItems();
        
        call.enqueue(new Callback<ApiResponse<CartResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartResponse>> call, 
                                 Response<ApiResponse<CartResponse>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                
                // Log raw response để debug
                try {
                    if (response.raw() != null && response.raw().body() != null) {
                        Log.d("CartActivity", "Response code: " + response.code());
                        Log.d("CartActivity", "Response headers: " + response.headers().toString());
                    }
                } catch (Exception e) {
                    Log.e("CartActivity", "Error logging raw response", e);
                }
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartResponse> apiResponse = response.body();
                    Log.d("CartActivity", "Response success: " + apiResponse.isSuccess());
                    Log.d("CartActivity", "Response message: " + apiResponse.getMessage());
                    
                    // Log data để debug
                    if (apiResponse.getData() != null) {
                        Log.d("CartActivity", "Cart data is not null");
                    } else {
                        Log.w("CartActivity", "Cart data is null!");
                    }
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        CartResponse cartData = apiResponse.getData();
                        Log.d("CartActivity", "Cart data received - Items count: " + 
                            (cartData.getItems() != null ? cartData.getItems().size() : 0));
                        
                        // Luôn clear list trước khi thêm items mới
                        cartItemList.clear();
                        
                        if (cartData.getItems() != null && !cartData.getItems().isEmpty()) {
                            // Convert CartItemResponse to CartItem
                            for (com.example.da1.models.CartItemResponse itemResponse : cartData.getItems()) {
                                Log.d("CartActivity", "Processing item: " + itemResponse.getId());
                                Log.d("CartActivity", "Item productid raw: " + itemResponse.getProductidRaw());
                                
                                CartItemResponse.ProductData productData = itemResponse.getProductid();
                                
                                // Đảm bảo luôn có productName và price, ngay cả khi productData là null
                                String productName = "Sản phẩm";
                                String productCode = "";
                                String imageUrl = "";
                                
                                if (productData != null) {
                                    productName = (productData.getName() != null && !productData.getName().trim().isEmpty()) 
                                        ? productData.getName().trim() 
                                        : "Sản phẩm";
                                    productCode = productData.getId() != null ? productData.getId() : "";
                                    imageUrl = productData.getImage() != null ? productData.getImage() : "";
                                    Log.d("CartActivity", "Product data - ID: " + productData.getId() + ", Name: " + productData.getName() + ", Image URL: " + imageUrl);
                                } else {
                                    Log.w("CartActivity", "Product data is null for item ID: " + itemResponse.getId());
                                    Log.w("CartActivity", "Raw productid type: " + 
                                        (itemResponse.getProductidRaw() != null ? itemResponse.getProductidRaw().getClass().getName() : "null"));
                                }
                                
                                double itemPrice = itemResponse.getPrice() > 0 ? itemResponse.getPrice() : 0;
                                int itemQuantity = itemResponse.getQuantity() > 0 ? itemResponse.getQuantity() : 1;
                                
                                Log.d("CartActivity", String.format(
                                    "Creating CartItem - Name: %s, Price: %.0f, Quantity: %d, Total: %.0f",
                                    productName, itemPrice, itemQuantity, itemPrice * itemQuantity
                                ));
                                
                                CartItem cartItem = new CartItem(
                                    itemResponse.getId(),
                                    itemResponse.getBillid(),
                                    productName,
                                    productCode,
                                    "", // brand - có thể lấy từ product nếu có
                                    itemResponse.getColor() != null ? itemResponse.getColor() : "",
                                    itemResponse.getSize() != null ? itemResponse.getSize() : "",
                                    itemPrice, // Giá từ API
                                    itemQuantity,
                                    imageUrl
                                );
                                
                                cartItemList.add(cartItem);
                                Log.d("CartActivity", "Added CartItem to list. Total items: " + cartItemList.size());
                            }
                            
                            cartAdapter.notifyDataSetChanged();
                            updateSubtotal();
                            
                            Log.d("CartActivity", "Loaded " + cartItemList.size() + " items from cart");
                            Log.d("CartActivity", "Total amount from API: " + cartData.getTotalAmount());
                        } else {
                            // Giỏ hàng trống - đã clear ở trên, chỉ cần update UI
                            cartAdapter.notifyDataSetChanged();
                            updateSubtotal();
                            Log.d("CartActivity", "Cart is empty");
                        }
                    } else {
                        // API trả về lỗi hoặc data null - giữ giỏ hàng trống
                        cartItemList.clear();
                        cartAdapter.notifyDataSetChanged();
                        updateSubtotal();
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Lỗi tải giỏ hàng";
                        Toast.makeText(CartActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Response không thành công - giữ giỏ hàng trống
                    cartItemList.clear();
                    cartAdapter.notifyDataSetChanged();
                    updateSubtotal();
                    
                    // Log chi tiết lỗi
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            Log.e("CartActivity", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("CartActivity", "Error reading error body", e);
                    }
                    
                    String errorMsg = "Lỗi tải giỏ hàng (Code: " + response.code() + ")";
                    if (response.code() == 401) {
                        errorMsg = "Vui lòng đăng nhập để xem giỏ hàng";
                        // Chuyển đến màn hình đăng nhập
                        navigateToLogin();
                    } else if (response.code() == 403) {
                        errorMsg = "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại";
                        // Xóa token và chuyển đến màn hình đăng nhập
                        clearTokenAndNavigateToLogin();
                    }
                    Toast.makeText(CartActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("CartActivity", "HTTP Error: " + response.code());
                    if (!errorBody.isEmpty()) {
                        Log.e("CartActivity", "Error response: " + errorBody);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartResponse>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                // Giữ giỏ hàng trống khi lỗi
                cartItemList.clear();
                cartAdapter.notifyDataSetChanged();
                updateSubtotal();
                Log.e("CartActivity", "Error loading cart", t);
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            finish();
        });

        btnExpandSubtotal.setOnClickListener(v -> {
            // TODO: Expand/collapse subtotal details
            Toast.makeText(this, "Chi tiết tạm tính", Toast.LENGTH_SHORT).show();
        });

        btnPurchase.setOnClickListener(v -> {
            if (cartItemList.isEmpty()) {
                Toast.makeText(this, "Giỏ hàng trống", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: Navigate to checkout
            Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
            startActivity(intent);
        });
    }

    private void updateSubtotal() {
        double total = 0;
        for (CartItem item : cartItemList) {
            double itemTotal = item.getTotalPrice();
            total += itemTotal;
            Log.d("CartActivity", String.format(
                "Item: %s, Price: %.0f, Qty: %d, Subtotal: %.0f",
                item.getProductName(), item.getPrice(), item.getQuantity(), itemTotal
            ));
        }

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        tvSubtotal.setText(currencyFormat.format(total));
        Log.d("CartActivity", "Total subtotal: " + total);
    }
    
    private void deleteCartItem(CartItem item) {
        String itemId = item.getId();
        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }
        
        Call<ApiResponse<Void>> call = ApiService.getCartApiService().removeFromCart(itemId);
        
        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Xóa khỏi list và update UI
                        int position = cartItemList.indexOf(item);
                        if (position != -1) {
                            cartItemList.remove(position);
                            cartAdapter.notifyItemRemoved(position);
                            updateSubtotal();
                            Toast.makeText(CartActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Lỗi xóa sản phẩm";
                        Toast.makeText(CartActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi xóa sản phẩm (Code: " + response.code() + ")";
                    Toast.makeText(CartActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(CartActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e("CartActivity", "Error deleting cart item", t);
            }
        });
    }
    
    private void updateCartItemQuantity(CartItem item, int newQuantity) {
        if (newQuantity < 1) {
            // Nếu quantity = 0, xóa item
            deleteCartItem(item);
            return;
        }
        
        String itemId = item.getId();
        if (itemId == null || itemId.isEmpty()) {
            Toast.makeText(this, "Không tìm thấy ID sản phẩm", Toast.LENGTH_SHORT).show();
            return;
        }
        
        UpdateCartRequest request = new UpdateCartRequest(newQuantity);
        Call<ApiResponse<CartItemResponse>> call = ApiService.getCartApiService().updateCartItem(itemId, request);
        
        call.enqueue(new Callback<ApiResponse<CartItemResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<CartItemResponse>> call, 
                                 Response<ApiResponse<CartItemResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<CartItemResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        // Cập nhật quantity trong item
                        item.setQuantity(newQuantity);
                        updateSubtotal();
                        Log.d("CartActivity", "Updated quantity to: " + newQuantity);
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Lỗi cập nhật số lượng";
                        Toast.makeText(CartActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                        // Reload cart để sync lại
                        loadCartItems();
                    }
                } else {
                    Toast.makeText(CartActivity.this, "Lỗi cập nhật số lượng", Toast.LENGTH_SHORT).show();
                    // Reload cart để sync lại
                    loadCartItems();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<CartItemResponse>> call, Throwable t) {
                Toast.makeText(CartActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CartActivity", "Error updating quantity", t);
                // Reload cart để sync lại
                loadCartItems();
            }
        });
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Reload cart khi quay lại từ màn hình khác
        loadCartItems();
    }
    
    private void navigateToLogin() {
        Intent intent = new Intent(CartActivity.this, com.example.da1.auth.LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    
    private void clearTokenAndNavigateToLogin() {
        // Xóa token và thông tin user
        com.example.da1.utils.SharedPreferencesHelper prefs = 
            new com.example.da1.utils.SharedPreferencesHelper(this);
        prefs.clearAll();
        
        // Reset Retrofit client để xóa token cũ
        com.example.da1.api.ApiClient.resetClient();
        
        // Chuyển đến màn hình đăng nhập
        navigateToLogin();
    }
}


