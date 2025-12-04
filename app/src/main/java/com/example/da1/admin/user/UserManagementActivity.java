package com.example.da1.admin.user;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;
import com.example.da1.admin.AdminDashboardActivity;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewUsers;
    private UserAdapter userAdapter;
    private List<User> userList;
    private EditText etSearchUser;
    private Spinner spinnerFilterRole;
    private Button btnSearch;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        initViews();
        setupRecyclerView();
        loadUsers();
        setupListeners();
    }

    private void initViews() {
        recyclerViewUsers = findViewById(R.id.recyclerViewUsers);
        etSearchUser = findViewById(R.id.etSearchUser);
        spinnerFilterRole = findViewById(R.id.spinnerFilterRole);
        btnSearch = findViewById(R.id.btnSearch);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, new UserAdapter.OnUserClickListener() {
            @Override
            public void onEditClick(User user) {
                openEditUserDialog(user);
            }

            @Override
            public void onDeleteClick(User user) {
                deleteUser(user);
            }

            @Override
            public void onToggleActiveClick(User user) {
                toggleUserActive(user);
            }
        });

        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewUsers.setAdapter(userAdapter);
    }

    private void loadUsers() {
        // TODO: Load users from API/database
        userList.clear();
        userAdapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        btnSearch.setOnClickListener(v -> {
            String searchQuery = etSearchUser.getText().toString().trim();
            searchUsers(searchQuery);
        });

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(UserManagementActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void searchUsers(String query) {
        // TODO: Search users by name, email, or username
        Toast.makeText(this, "Tìm kiếm: " + query, Toast.LENGTH_SHORT).show();
    }

    private void openEditUserDialog(User user) {
        // TODO: Open dialog to edit user
        Toast.makeText(this, "Chỉnh sửa người dùng: " + user.getFullName(), Toast.LENGTH_SHORT).show();
    }

    private void deleteUser(User user) {
        // TODO: Delete user from API/database
        Toast.makeText(this, "Xóa người dùng: " + user.getFullName(), Toast.LENGTH_SHORT).show();
    }

    private void toggleUserActive(User user) {
        // TODO: Toggle user active status
        user.setActive(!user.isActive());
        userAdapter.notifyDataSetChanged();
        Toast.makeText(this, "Thay đổi trạng thái người dùng: " + user.getFullName(), Toast.LENGTH_SHORT).show();
    }
}


