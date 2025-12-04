package com.example.da1.admin.category;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;
import com.example.da1.admin.AdminDashboardActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CategoryManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList;
    private FloatingActionButton fabAddCategory;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_management);

        initViews();
        setupRecyclerView();
        loadCategories();
        setupListeners();
    }

    private void initViews() {
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories);
        fabAddCategory = findViewById(R.id.fabAddCategory);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        categoryList = new ArrayList<>();
        categoryAdapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onEditClick(Category category) {
                openEditCategoryDialog(category);
            }

            @Override
            public void onDeleteClick(Category category) {
                deleteCategory(category);
            }
        });

        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewCategories.setAdapter(categoryAdapter);
    }

    private void loadCategories() {
        // TODO: Load categories from API/database
        // For now, using sample data
        categoryList.clear();
        // Add sample data if needed for testing
        categoryAdapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        fabAddCategory.setOnClickListener(v -> openAddCategoryDialog());

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryManagementActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void openAddCategoryDialog() {
        // TODO: Open dialog to add new category
        Toast.makeText(this, "Thêm danh mục mới", Toast.LENGTH_SHORT).show();
    }

    private void openEditCategoryDialog(Category category) {
        // TODO: Open dialog to edit category
        Toast.makeText(this, "Chỉnh sửa danh mục: " + category.getName(), Toast.LENGTH_SHORT).show();
    }

    private void deleteCategory(Category category) {
        // TODO: Delete category from API/database
        Toast.makeText(this, "Xóa danh mục: " + category.getName(), Toast.LENGTH_SHORT).show();
    }
}


