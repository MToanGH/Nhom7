package com.example.da1.admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.da1.R;
import com.example.da1.admin.category.CategoryManagementActivity;
import com.example.da1.admin.order.OrderManagementActivity;
import com.example.da1.admin.product.ProductManagementActivity;
import com.example.da1.admin.user.UserManagementActivity;
import com.example.da1.admin.voucher.VoucherManagementActivity;
import com.example.da1.auth.LoginActivity;
import com.example.da1.utils.SharedPreferencesHelper;
import com.google.android.material.navigation.NavigationView;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        initViews();
        setupToolbar();
        setupDrawer();
        setupNavigationHeader();
    }

    private void initViews() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu_white);
        }
    }

    private void setupDrawer() {
        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupNavigationHeader() {
        View headerView = navigationView.getHeaderView(0);
        ImageButton btnCloseDrawer = headerView.findViewById(R.id.btnCloseDrawer);
        if (btnCloseDrawer != null) {
            btnCloseDrawer.setOnClickListener(v -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
            });
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_manage_products) {
            navigateToActivity(ProductManagementActivity.class);
        } else if (itemId == R.id.nav_manage_users) {
            navigateToActivity(UserManagementActivity.class);
        } else if (itemId == R.id.nav_manage_categories) {
            navigateToActivity(CategoryManagementActivity.class);
        } else if (itemId == R.id.nav_manage_orders) {
            navigateToActivity(OrderManagementActivity.class);
        } else if (itemId == R.id.nav_statistics) {
            showToast("Xem thống kê");
            // TODO: Navigate to statistics activity
        } else if (itemId == R.id.nav_logout) {
            showLogoutDialog();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    logout();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void logout() {
        // Clear SharedPreferences
        SharedPreferencesHelper prefs = new SharedPreferencesHelper(this);
        prefs.clearAll();

        // Navigate to LoginActivity
        Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(AdminDashboardActivity.this, activityClass);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
