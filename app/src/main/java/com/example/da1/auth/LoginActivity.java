package com.example.da1.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.da1.R;
import com.example.da1.HomeActivity;
import com.example.da1.admin.AdminDashboardActivity;
import com.example.da1.api.ApiClient;
import com.example.da1.api.ApiResponse;
import com.example.da1.api.ApiService;
import com.example.da1.models.LoginRequest;
import com.example.da1.models.LoginResponse;
import com.example.da1.utils.SharedPreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private EditText etEmailOrPhone;
    private EditText etPassword;
    private Button btnLogin;
    private TextView tvForgotPassword;
    private TextView tvRegister;
    private ProgressBar progressBar;
    private SharedPreferencesHelper prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo ApiClient với context
        ApiClient.init(this);
        
        prefs = new SharedPreferencesHelper(this);
        
        // Nếu đã đăng nhập, kiểm tra role và chuyển đến màn hình phù hợp
        if (prefs.isLoggedIn()) {
            String role = prefs.getUserRole();
            if ("ADMIN".equalsIgnoreCase(role)) {
                navigateToAdminDashboard();
            } else {
                navigateToHome();
            }
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmailOrPhone = findViewById(R.id.etEmailOrPhone);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvRegister = findViewById(R.id.tvRegister);
        progressBar = findViewById(R.id.progressBar);
        
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnLogin.setOnClickListener(v -> {
            String emailOrPhone = etEmailOrPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (validateInput(emailOrPhone, password)) {
                performLogin(emailOrPhone, password);
            }
        });

        tvForgotPassword.setOnClickListener(v -> {
            // TODO: Navigate to forgot password screen
            Toast.makeText(this, "Quên mật khẩu", Toast.LENGTH_SHORT).show();
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInput(String emailOrPhone, String password) {
        if (TextUtils.isEmpty(emailOrPhone)) {
            etEmailOrPhone.setError("Vui lòng nhập email");
            etEmailOrPhone.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Vui lòng nhập mật khẩu");
            etPassword.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            etPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            etPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void performLogin(String emailOrPhone, String password) {
        showLoading(true);
        
        LoginRequest request = new LoginRequest(emailOrPhone, password);
        
        Call<ApiResponse<LoginResponse>> call = ApiService.getAuthApiService().login(request);
        
        call.enqueue(new Callback<ApiResponse<LoginResponse>>() {
            @Override
            public void onResponse(Call<ApiResponse<LoginResponse>> call, Response<ApiResponse<LoginResponse>> response) {
                showLoading(false);
                
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<LoginResponse> apiResponse = response.body();
                    
                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        LoginResponse loginResponse = apiResponse.getData();
                        
                        // Lưu token và thông tin user
                        String token = loginResponse.getToken();
                        if (token != null) {
                            prefs.saveUserToken(token);
                            prefs.setLoggedIn(true);
                            
                            LoginResponse.UserData userData = loginResponse.getUser();
                            if (userData != null) {
                                prefs.saveUserId(userData.getId());
                                
                                // Lưu role vào SharedPreferences
                                String role = userData.getRole();
                                if (role != null) {
                                    prefs.saveUserRole(role);
                                }
                                
                                // Kiểm tra role và navigate đến màn hình phù hợp
                                if (userData.isAdmin()) {
                                    // Nếu là admin, chuyển đến AdminDashboardActivity
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công với quyền quản lý!", Toast.LENGTH_SHORT).show();
                                    navigateToAdminDashboard();
                                } else {
                                    // Nếu là user thường, chuyển đến HomeActivity
                                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                    navigateToHome();
                                }
                            } else {
                                // Nếu không có user data, mặc định là user thường
                                prefs.saveUserRole("CUSTOMER");
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                navigateToHome();
                            }
                            
                            // Reset ApiClient để load lại với token mới
                            ApiClient.resetClient();
                        } else {
                            Toast.makeText(LoginActivity.this, "Không nhận được token từ server", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Đăng nhập thất bại";
                        Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    String errorMsg = "Lỗi kết nối: " + response.code();
                    if (response.errorBody() != null) {
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                showLoading(false);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(LoginActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void navigateToAdminDashboard() {
        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        btnLogin.setEnabled(!show);
    }
}


