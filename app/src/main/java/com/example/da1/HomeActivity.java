package com.example.da1;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.da1.adapters.CategoryAdapter;
import com.example.da1.adapters.ProductAdapter;
import com.example.da1.api.ApiClient;
import com.example.da1.api.ApiResponse;
import com.example.da1.api.ApiService;
import com.example.da1.api.ProductListResponse;
import com.example.da1.models.CategoryItem;
import com.example.da1.models.ProductItem;
import com.example.da1.utils.AuthHelper;
import com.example.da1.utils.SharedPreferencesHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {
    private EditText etSearch;
    private ImageView ivCart;
    private ImageButton btnAddProduct;
    private Button btnShopNow;
    private RecyclerView recyclerViewCategories;
    private RecyclerView recyclerViewProducts;
    private BottomNavigationView bottomNavigation;
    private CategoryAdapter categoryAdapter;
    private ProductAdapter productAdapter;
    private List<CategoryItem> categoryList;
    private List<ProductItem> productList;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Khởi tạo ApiClient
        ApiClient.init(this);

        initViews();
        setupRecyclerViews();
        loadData();
        setupListeners();
    }

    private void initViews() {
        etSearch = findViewById(R.id.etSearch);
        ivCart = findViewById(R.id.ivCart);
        btnAddProduct = findViewById(R.id.btnAddProduct);
        btnShopNow = findViewById(R.id.btnShopNow);
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        bottomNavigation = findViewById(R.id.bottomNavigation);
        progressBar = findViewById(R.id.progressBar);

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupRecyclerViews() {
        // Categories horizontal
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList, category -> {
            // Filter products by category
            Toast.makeText(this, "Chọn danh mục: " + category.getName(), Toast.LENGTH_SHORT).show();
        });
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategories.setAdapter(categoryAdapter);

        // Products grid
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(productList, product -> {
            // Navigate to product detail
            Intent intent = new Intent(HomeActivity.this, ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            startActivity(intent);
        });

        // Chỉ cho phép edit/delete nếu user là ADMIN
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(this);
        String userRole = prefs.getUserRole();
        boolean isAdmin = "ADMIN".equalsIgnoreCase(userRole);

        if (isAdmin) {
            productAdapter.setEditListener(product -> {
                showAddEditProductDialog(product);
            });

            productAdapter.setDeleteListener(product -> {
                showDeleteConfirmDialog(product);
            });
        }

        // Ẩn nút Add Product nếu không phải admin
        if (btnAddProduct != null) {
            btnAddProduct.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
        }

        recyclerViewProducts.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadData() {
        loadCategories();
        loadProducts();
    }

    private void loadCategories() {
        Call<ApiResponse<List<CategoryItem>>> call =
                ApiService.getCategoryApiService().getAllCategories();

        call.enqueue(new Callback<ApiResponse<List<CategoryItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryItem>>> call,
                                   Response<ApiResponse<List<CategoryItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<CategoryItem>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        categoryList.clear();
                        categoryList.addAll(apiResponse.getData());
                        categoryAdapter.notifyDataSetChanged();
                        Log.d("HomeActivity", "Loaded " + categoryList.size() + " categories");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryItem>>> call, Throwable t) {
                Log.e("HomeActivity", "Error loading categories", t);
                // Fallback to sample data
                loadSampleCategories();
            }
        });
    }

    private void loadSampleCategories() {
        categoryList.clear();
        categoryList.add(new CategoryItem("1", "Áo Polo", ""));
        categoryList.add(new CategoryItem("2", "Áo Sơ Mi", ""));
        categoryList.add(new CategoryItem("3", "Quần Tây", ""));
        categoryList.add(new CategoryItem("4", "Quần Short", ""));
        categoryAdapter.notifyDataSetChanged();
    }

    private void loadProducts() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Call<ApiResponse<ProductListResponse>> call = ApiService.getProductApiService().getAllProducts(
                null, null, 1, 20
        );

        call.enqueue(new Callback<ApiResponse<ProductListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductListResponse>> call,
                                   Response<ApiResponse<ProductListResponse>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProductListResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        ProductListResponse data = apiResponse.getData();
                        if (data.getProducts() != null) {
                            productList.clear();
                            productList.addAll(data.getProducts());
                            productAdapter.notifyDataSetChanged();
                            Log.d("HomeActivity", "Loaded " + productList.size() + " products");
                        } else {
                            loadSampleProducts();
                        }
                    } else {
                        loadSampleProducts();
                    }
                } else {
                    loadSampleProducts();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductListResponse>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Log.e("HomeActivity", "Error loading products", t);
                Toast.makeText(HomeActivity.this, "Lỗi tải sản phẩm: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                loadSampleProducts();
            }
        });
    }

    private void loadSampleProducts() {
        // Sample data chỉ để test UI, không dùng để navigate
        productList.clear();
        productAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Không có sản phẩm. Vui lòng tạo sản phẩm trong database.", Toast.LENGTH_LONG).show();
    }

    private void searchProducts(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            loadProducts();
            return;
        }

        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Call<ApiResponse<ProductListResponse>> call = ApiService.getSearchApiService().searchProducts(
                keyword.trim(), 1, 20
        );

        call.enqueue(new Callback<ApiResponse<ProductListResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductListResponse>> call,
                                   Response<ApiResponse<ProductListResponse>> response) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }

                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProductListResponse> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        ProductListResponse data = apiResponse.getData();
                        if (data.getProducts() != null) {
                            productList.clear();
                            productList.addAll(data.getProducts());
                            productAdapter.notifyDataSetChanged();
                            Log.d("HomeActivity", "Search results: " + productList.size() + " products for keyword: " + keyword);

                            if (productList.isEmpty()) {
                                Toast.makeText(HomeActivity.this, "Không tìm thấy sản phẩm nào", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            productList.clear();
                            productAdapter.notifyDataSetChanged();
                            Toast.makeText(HomeActivity.this, "Không tìm thấy sản phẩm nào", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : "Lỗi tìm kiếm";
                        Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi tìm kiếm (Code: " + response.code() + ")";
                    Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e("HomeActivity", "Search error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductListResponse>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Log.e("HomeActivity", "Error searching products", t);
                Toast.makeText(HomeActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        // Tìm kiếm khi người dùng nhập (với delay để tránh gọi API quá nhiều)
        etSearch.addTextChangedListener(new TextWatcher() {
            private android.os.Handler handler = new android.os.Handler();
            private Runnable runnable;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Hủy request trước đó nếu có
                if (runnable != null) {
                    handler.removeCallbacks(runnable);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                String keyword = s.toString().trim();

                // Nếu rỗng, load lại tất cả sản phẩm
                if (keyword.isEmpty()) {
                    loadProducts();
                    return;
                }

                // Delay 500ms trước khi tìm kiếm để tránh gọi API quá nhiều
                runnable = () -> searchProducts(keyword);
                handler.postDelayed(runnable, 500);
            }
        });

        ivCart.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
                Log.d("HomeActivity", "Navigating to CartActivity");
            } catch (Exception e) {
                Log.e("HomeActivity", "Error navigating to CartActivity", e);
                Toast.makeText(this, "Lỗi mở giỏ hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        btnAddProduct.setOnClickListener(v -> {
            showAddEditProductDialog(null);
        });

        btnShopNow.setOnClickListener(v -> {
            Toast.makeText(this, "Shop Now", Toast.LENGTH_SHORT).show();
        });

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                // Already on home
                return true;
            } else if (itemId == R.id.nav_cart) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_orders) {
                Intent intent = new Intent(HomeActivity.this, OrdersActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_wallet) {
                Toast.makeText(this, "Ví", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_account) {
                showAccountDialog();
                return true;
            }
            return false;
        });
    }

    private void showAddEditProductDialog(ProductItem product) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_product, null);

        EditText etProductName = dialogView.findViewById(R.id.etProductName);
        EditText etProductCode = dialogView.findViewById(R.id.etProductCode);
        EditText etProductPrice = dialogView.findViewById(R.id.etProductPrice);
        EditText etImageUrl = dialogView.findViewById(R.id.etImageUrl);
        ImageView ivPreviewImage = dialogView.findViewById(R.id.ivPreviewImage);
        Button btnPreviewImage = dialogView.findViewById(R.id.btnPreviewImage);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);

        boolean isEdit = product != null;
        if (isEdit) {
            tvDialogTitle.setText("Sửa sản phẩm");
            etProductName.setText(product.getName());
            etProductCode.setText(product.getCode());
            etProductPrice.setText(String.valueOf((int)product.getPrice()));
            etImageUrl.setText(product.getImageUrl());
        } else {
            tvDialogTitle.setText("Thêm sản phẩm");
            // Clear tất cả fields khi tạo mới
            etProductName.setText("");
            etProductCode.setText("");
            etProductPrice.setText("");
            etImageUrl.setText("");
        }

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .create();

        btnPreviewImage.setOnClickListener(v -> {
            String url = etImageUrl.getText().toString().trim();
            if (!TextUtils.isEmpty(url)) {
                // Preview image will be loaded when saved
                Toast.makeText(this, "Xem trước ảnh: " + url, Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(v -> {
            String name = etProductName.getText().toString().trim();
            String code = etProductCode.getText().toString().trim();
            String priceStr = etProductPrice.getText().toString().trim();
            String imageUrl = etImageUrl.getText().toString().trim();

            if (TextUtils.isEmpty(name) || TextUtils.isEmpty(code) || TextUtils.isEmpty(priceStr)) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double price = Double.parseDouble(priceStr);

                if (isEdit) {
                    // Update existing product via API
                    updateProductViaAPI(product.getId(), name, code, price, imageUrl, dialog);
                    // Không dismiss dialog ở đây, sẽ dismiss trong callback
                    return; // Return để không dismiss dialog
                } else {
                    // Add new product - Tạo qua API
                    createProductViaAPI(name, code, price, imageUrl, dialog);
                    // Không dismiss dialog ở đây, sẽ dismiss trong callback
                    return; // Return để không dismiss dialog
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void showDeleteConfirmDialog(ProductItem product) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xóa sản phẩm")
                .setMessage("Bạn có chắc chắn muốn xóa sản phẩm " + product.getName() + "?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    int position = productList.indexOf(product);
                    if (position != -1) {
                        productList.remove(position);
                        productAdapter.notifyItemRemoved(position);
                        Toast.makeText(this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showAccountDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Tài khoản")
                .setItems(new String[]{"Thông tin tài khoản", "Đăng xuất"}, (dialog, which) -> {
                    if (which == 0) {
                        // Thông tin tài khoản
                        Intent intent = new Intent(HomeActivity.this, AccountActivity.class);
                        startActivity(intent);
                    } else if (which == 1) {
                        // Đăng xuất
                        showLogoutConfirmDialog();
                    }
                })
                .show();
    }

    private void showLogoutConfirmDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    AuthHelper.logoutAndNavigateToLogin(this);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void createProductViaAPI(String name, String code, double price, String imageUrl, AlertDialog dialog) {
        // Tạo sản phẩm qua API
        // Lưu ý: API yêu cầu categoryid hợp lệ
        // Tạm thời dùng category đầu tiên hoặc yêu cầu user chọn

        if (categoryList.isEmpty()) {
            Toast.makeText(this, "Vui lòng tạo danh mục trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryId = categoryList.get(0).getId();

        // Tạo ProductItem để gửi lên API
        ProductItem newProduct = new ProductItem();
        newProduct.setName(name);
        newProduct.setCode(code);
        newProduct.setPrice(price);
        // API yêu cầu image là mảng, chuyển imageUrl thành mảng
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            java.util.List<String> imageList = new java.util.ArrayList<>();
            imageList.add(imageUrl.trim());
            newProduct.setImage(imageList);
        }
        newProduct.setCategoryId(categoryId); // Sẽ set vào categoryid với @SerializedName("categoryid")

        // Log để debug
        Log.d("HomeActivity", "Creating product - Name: " + name + ", CategoryId: " + categoryId + ", Price: " + price);
        Log.d("HomeActivity", "ProductItem categoryId: " + newProduct.getCategoryId());

        // Gọi API để tạo sản phẩm
        Call<ApiResponse<ProductItem>> call = ApiService.getProductApiService().createProduct(newProduct);

        call.enqueue(new Callback<ApiResponse<ProductItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductItem>> call,
                                   Response<ApiResponse<ProductItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProductItem> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        ProductItem createdProduct = apiResponse.getData();
                        productList.add(createdProduct);
                        productAdapter.notifyItemInserted(productList.size() - 1);
                        Toast.makeText(HomeActivity.this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        String errorMsg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : "Lỗi tạo sản phẩm";
                        Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Log chi tiết lỗi
                    String errorBody = "";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                            Log.e("HomeActivity", "Error body: " + errorBody);
                        }
                    } catch (Exception e) {
                        Log.e("HomeActivity", "Error reading error body", e);
                    }

                    String errorMsg = "Lỗi tạo sản phẩm (Code: " + response.code() + ")";
                    if (response.code() == 400) {
                        errorMsg = "Dữ liệu không hợp lệ. Vui lòng kiểm tra lại thông tin sản phẩm.";
                        if (!errorBody.isEmpty()) {
                            Log.e("HomeActivity", "400 Error details: " + errorBody);
                        }
                    } else if (response.code() == 401) {
                        errorMsg = "Vui lòng đăng nhập để tạo sản phẩm";
                    }
                    Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductItem>> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e("HomeActivity", "Error creating product", t);
            }
        });
    }

    private void updateProductViaAPI(String productId, String name, String code, double price, String imageUrl, AlertDialog dialog) {
        // Cập nhật sản phẩm qua API

        if (categoryList.isEmpty()) {
            Toast.makeText(this, "Vui lòng tạo danh mục trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryId = categoryList.get(0).getId();

        // Tạo ProductItem để gửi lên API
        ProductItem updatedProduct = new ProductItem();
        updatedProduct.setName(name);
        updatedProduct.setCode(code);
        updatedProduct.setPrice(price);
        // API yêu cầu image là mảng, chuyển imageUrl thành mảng
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            java.util.List<String> imageList = new java.util.ArrayList<>();
            imageList.add(imageUrl.trim());
            updatedProduct.setImage(imageList);
        }
        updatedProduct.setCategoryId(categoryId);

        // Gọi API để cập nhật sản phẩm
        Call<ApiResponse<ProductItem>> call = ApiService.getProductApiService().updateProduct(productId, updatedProduct);

        call.enqueue(new Callback<ApiResponse<ProductItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductItem>> call,
                                   Response<ApiResponse<ProductItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProductItem> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        // Reload danh sách sản phẩm từ API để đảm bảo đồng bộ
                        // Điều này đảm bảo dữ liệu ở trang chủ và chi tiết giống nhau
                        loadProducts();

                        Toast.makeText(HomeActivity.this, "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    } else {
                        String errorMsg = apiResponse.getMessage() != null
                                ? apiResponse.getMessage()
                                : "Lỗi cập nhật sản phẩm";
                        Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi cập nhật sản phẩm (Code: " + response.code() + ")";
                    if (response.code() == 401) {
                        errorMsg = "Vui lòng đăng nhập để cập nhật sản phẩm";
                    }
                    Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductItem>> call, Throwable t) {
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(HomeActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                Log.e("HomeActivity", "Error updating product", t);
            }
        });
    }
}
