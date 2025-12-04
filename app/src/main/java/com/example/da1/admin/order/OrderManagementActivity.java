package com.example.da1.admin.order;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;
import com.example.da1.admin.AdminDashboardActivity;

import java.util.ArrayList;
import java.util.List;

public class OrderManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewOrders;
    private OrderAdapter orderAdapter;
    private List<Order> orderList;
    private Spinner spinnerFilterStatus;
    private Button btnBack;
    private Button btnFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_management);

        initViews();
        setupRecyclerView();
        loadOrders();
        setupListeners();
    }

    private void initViews() {
        recyclerViewOrders = findViewById(R.id.recyclerViewOrders);
        spinnerFilterStatus = findViewById(R.id.spinnerFilterStatus);
        btnBack = findViewById(R.id.btnBack);
        btnFilter = findViewById(R.id.btnFilter);
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderAdapter = new OrderAdapter(orderList, new OrderAdapter.OnOrderClickListener() {
            @Override
            public void onViewClick(Order order) {
                viewOrderDetails(order);
            }

            @Override
            public void onStatusChangeClick(Order order) {
                changeOrderStatus(order);
            }
        });

        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewOrders.setAdapter(orderAdapter);
    }

    private void loadOrders() {
        // TODO: Load orders from API/database
        orderList.clear();
        orderAdapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(OrderManagementActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        });

        btnFilter.setOnClickListener(v -> {
            String selectedStatus = spinnerFilterStatus.getSelectedItem().toString();
            filterOrdersByStatus(selectedStatus);
        });
    }

    private void viewOrderDetails(Order order) {
        // TODO: Open order details dialog/activity
        Toast.makeText(this, "Xem chi tiết đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();
    }

    private void changeOrderStatus(Order order) {
        // TODO: Open dialog to change order status
        Toast.makeText(this, "Thay đổi trạng thái đơn hàng: " + order.getId(), Toast.LENGTH_SHORT).show();
    }

    private void filterOrdersByStatus(String status) {
        // TODO: Filter orders by status
        Toast.makeText(this, "Lọc đơn hàng theo trạng thái: " + status, Toast.LENGTH_SHORT).show();
    }
}


