package com.example.da1.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.da1.R;
import com.example.da1.HomeActivity;
import com.example.da1.api.ApiClient;
import com.example.da1.api.ApiResponse;
import com.example.da1.api.ApiService;
import com.example.da1.models.RegisterRequest;
import com.example.da1.models.LoginResponse;
import com.example.da1.utils.SharedPreferencesHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmailOrPhone;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private ProgressBar progressBar;
    private SharedPreferencesHelper prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Khởi tạo ApiClient với context
        ApiClient.init(this);
        
        prefs = new SharedPreferencesHelper(this);

        initViews();
        setupListeners();
    }

    private void initViews() {
        etEmailOrPhone = findViewById(R.id.etEmailOrPhone);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnRegister = findViewById(R.id.btnRegister);
        progressBar = findViewById(R.id.progressBar);
        
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setupListeners() {
        btnRegister.setOnClickListener(v -> {
            String emailOrPhone = etEmailOrPhone.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();
            String name = ""; // Name là optional, có thể để trống

            if (validateInput(emailOrPhone, password, confirmPassword)) {
                performRegister(emailOrPhone, password, name);
            }
        });
    }

    private boolean validateInput(String emailOrPhone, String password, String confirmPassword) {
        if (TextUtils.isEmpty(emailOrPhone)) {
            etEmailOrPhone.setError("Vui lòng nhập email");
            etEmailOrPhone.requestFocus();
            return false;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(emailOrPhone).matches()) {
            etEmailOrPhone.setError("Email không hợp lệ");
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

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Vui lòng nhập lại mật khẩu");
            etConfirmPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Mật khẩu không khớp");
            etConfirmPassword.requestFocus();
            return false;
        }

        return true;
    }

    private void performRegister(String email, String password, String name) {
        showLoading(true);
        
        RegisterRequest request = new RegisterRequest(email, password, name, null);
        
        Call<ApiResponse<LoginResponse>> call = ApiService.getAuthApiService().register(request);
        
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
                            
                            if (loginResponse.getUser() != null) {
                                prefs.saveUserId(loginResponse.getUser().getId());
                            }
                            
                            // Reset ApiClient để load lại với token mới
                            ApiClient.resetClient();
                            
                            Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                            
                            Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Không nhận được token từ server", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        String errorMsg = apiResponse.getMessage() != null 
                            ? apiResponse.getMessage() 
                            : "Đăng ký thất bại";
                        Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<LoginResponse>> call, Throwable t) {
                showLoading(false);
                String errorMsg = "Lỗi kết nối: " + t.getMessage();
                Toast.makeText(RegisterActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        btnRegister.setEnabled(!show);
    }
}


