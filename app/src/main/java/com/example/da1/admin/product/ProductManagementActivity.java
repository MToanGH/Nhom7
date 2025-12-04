package com.example.da1.admin.product;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;
import com.example.da1.admin.AdminDashboardActivity;
import com.example.da1.api.ApiClient;
import com.example.da1.api.ApiResponse;
import com.example.da1.api.ApiService;
import com.example.da1.api.ProductListResponse;
import com.example.da1.models.CategoryItem;
import com.example.da1.models.ProductItem;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewProducts;
    private ProductManagementAdapter productAdapter;
    private List<ProductItem> productList;
    private List<CategoryItem> categoryList;
    private FloatingActionButton fabAddProduct;
    private Button btnBack;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_management);

        // Khởi tạo ApiClient
        ApiClient.init(this);

        initViews();
        setupRecyclerView();
        loadCategories();
        loadProducts();
        setupListeners();
    }

    private void initViews() {
        recyclerViewProducts = findViewById(R.id.recyclerViewProducts);
        fabAddProduct = findViewById(R.id.fabAddProduct);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBar);
        
        if (progressBar == null) {
            // Nếu không có progressBar trong layout, tạo một cái
            progressBar = new ProgressBar(this);
        }
    }

    private void setupRecyclerView() {
        productList = new ArrayList<>();
        productAdapter = new ProductManagementAdapter(productList, new ProductManagementAdapter.OnProductClickListener() {
            @Override
            public void onEditClick(ProductItem product) {
                openEditProductDialog(product);
            }

            @Override
            public void onDeleteClick(ProductItem product) {
                showDeleteConfirmDialog(product);
            }
        });

        recyclerViewProducts.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewProducts.setAdapter(productAdapter);
    }

    private void loadCategories() {
        Call<ApiResponse<List<CategoryItem>>> call = ApiService.getCategoryApiService().getAllCategories();
        call.enqueue(new Callback<ApiResponse<List<CategoryItem>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<CategoryItem>>> call, 
                                 Response<ApiResponse<List<CategoryItem>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<CategoryItem>> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        categoryList = apiResponse.getData();
                        Log.d("ProductManagement", "Loaded " + categoryList.size() + " categories");
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<CategoryItem>>> call, Throwable t) {
                Log.e("ProductManagement", "Error loading categories", t);
                categoryList = new ArrayList<>();
            }
        });
    }

    private void loadProducts() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }

        Call<ApiResponse<ProductListResponse>> call = ApiService.getProductApiService().getAllProducts(
            null, null, 1, 100
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
                            Log.d("ProductManagement", "Loaded " + productList.size() + " products");
                        }
                    } else {
                        Toast.makeText(ProductManagementActivity.this, 
                            "Không thể tải danh sách sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi tải sản phẩm (Code: " + response.code() + ")";
                    Toast.makeText(ProductManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductListResponse>> call, Throwable t) {
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
                Log.e("ProductManagement", "Error loading products", t);
                Toast.makeText(ProductManagementActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupListeners() {
        fabAddProduct.setOnClickListener(v -> openAddProductDialog());

        btnBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void openAddProductDialog() {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_product, null);

        EditText etProductName = dialogView.findViewById(R.id.etProductName);
        EditText etProductCode = dialogView.findViewById(R.id.etProductCode);
        EditText etProductPrice = dialogView.findViewById(R.id.etProductPrice);
        EditText etImageUrl = dialogView.findViewById(R.id.etImageUrl);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);

        tvDialogTitle.setText("Thêm sản phẩm mới");

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create();

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
                createProductViaAPI(name, code, price, imageUrl, dialog);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Giá không hợp lệ", Toast.LENGTH_SHORT).show();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void openEditProductDialog(ProductItem product) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_product, null);

        EditText etProductName = dialogView.findViewById(R.id.etProductName);
        EditText etProductCode = dialogView.findViewById(R.id.etProductCode);
        EditText etProductPrice = dialogView.findViewById(R.id.etProductPrice);
        EditText etImageUrl = dialogView.findViewById(R.id.etImageUrl);
        Button btnSave = dialogView.findViewById(R.id.btnSave);
        Button btnCancel = dialogView.findViewById(R.id.btnCancel);
        TextView tvDialogTitle = dialogView.findViewById(R.id.tvDialogTitle);

        tvDialogTitle.setText("Sửa sản phẩm");
        etProductName.setText(product.getName());
        etProductCode.setText(product.getCode());
        etProductPrice.setText(String.valueOf((int)product.getPrice()));
        etImageUrl.setText(product.getImageUrl());

        AlertDialog dialog = new MaterialAlertDialogBuilder(this)
            .setView(dialogView)
            .create();

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
                updateProductViaAPI(product.getId(), name, code, price, imageUrl, dialog);
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
                deleteProduct(product);
            })
            .setNegativeButton("Hủy", null)
            .show();
    }

    private void createProductViaAPI(String name, String code, double price, String imageUrl, AlertDialog dialog) {
        if (categoryList == null || categoryList.isEmpty()) {
            Toast.makeText(this, "Vui lòng tạo danh mục trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryId = categoryList.get(0).getId();

        ProductItem newProduct = new ProductItem();
        newProduct.setName(name);
        newProduct.setCode(code);
        newProduct.setPrice(price);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            List<String> imageList = new ArrayList<>();
            imageList.add(imageUrl.trim());
            newProduct.setImage(imageList);
        }
        newProduct.setCategoryId(categoryId);

        Call<ApiResponse<ProductItem>> call = ApiService.getProductApiService().createProduct(newProduct);

        call.enqueue(new Callback<ApiResponse<ProductItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductItem>> call, 
                                 Response<ApiResponse<ProductItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProductItem> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Toast.makeText(ProductManagementActivity.this, "Đã thêm sản phẩm", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadProducts(); // Reload danh sách
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Lỗi tạo sản phẩm";
                        Toast.makeText(ProductManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi tạo sản phẩm (Code: " + response.code() + ")";
                    Toast.makeText(ProductManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductItem>> call, Throwable t) {
                Toast.makeText(ProductManagementActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProductManagement", "Error creating product", t);
            }
        });
    }

    private void updateProductViaAPI(String productId, String name, String code, double price, String imageUrl, AlertDialog dialog) {
        if (categoryList == null || categoryList.isEmpty()) {
            Toast.makeText(this, "Vui lòng tạo danh mục trước", Toast.LENGTH_SHORT).show();
            return;
        }

        String categoryId = categoryList.get(0).getId();

        ProductItem updatedProduct = new ProductItem();
        updatedProduct.setName(name);
        updatedProduct.setCode(code);
        updatedProduct.setPrice(price);
        if (imageUrl != null && !imageUrl.trim().isEmpty()) {
            List<String> imageList = new ArrayList<>();
            imageList.add(imageUrl.trim());
            updatedProduct.setImage(imageList);
        }
        updatedProduct.setCategoryId(categoryId);

        Call<ApiResponse<ProductItem>> call = ApiService.getProductApiService().updateProduct(productId, updatedProduct);

        call.enqueue(new Callback<ApiResponse<ProductItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<ProductItem>> call, 
                                 Response<ApiResponse<ProductItem>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<ProductItem> apiResponse = response.body();
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        Toast.makeText(ProductManagementActivity.this, "Đã cập nhật sản phẩm", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        loadProducts(); // Reload danh sách
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Lỗi cập nhật sản phẩm";
                        Toast.makeText(ProductManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi cập nhật sản phẩm (Code: " + response.code() + ")";
                    Toast.makeText(ProductManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<ProductItem>> call, Throwable t) {
                Toast.makeText(ProductManagementActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProductManagement", "Error updating product", t);
            }
        });
    }

    private void deleteProduct(ProductItem product) {
        Call<ApiResponse<Void>> call = ApiService.getProductApiService().deleteProduct(product.getId());

        call.enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        Toast.makeText(ProductManagementActivity.this, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                        loadProducts(); // Reload danh sách
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Lỗi xóa sản phẩm";
                        Toast.makeText(ProductManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi xóa sản phẩm (Code: " + response.code() + ")";
                    Toast.makeText(ProductManagementActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                Toast.makeText(ProductManagementActivity.this, 
                    "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ProductManagement", "Error deleting product", t);
            }
        });
    }
}
