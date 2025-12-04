package com.example.da1.admin.voucher;

import android.content.Intent;
import android.os.Bundle;
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

public class VoucherManagementActivity extends AppCompatActivity {
    private RecyclerView recyclerViewVouchers;
    private VoucherAdapter voucherAdapter;
    private List<Voucher> voucherList;
    private FloatingActionButton fabAddVoucher;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher_management);

        initViews();
        setupRecyclerView();
        loadVouchers();
        setupListeners();
    }

    private void initViews() {
        recyclerViewVouchers = findViewById(R.id.recyclerViewVouchers);
        fabAddVoucher = findViewById(R.id.fabAddVoucher);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupRecyclerView() {
        voucherList = new ArrayList<>();
        voucherAdapter = new VoucherAdapter(voucherList, new VoucherAdapter.OnVoucherClickListener() {
            @Override
            public void onEditClick(Voucher voucher) {
                openEditVoucherDialog(voucher);
            }

            @Override
            public void onDeleteClick(Voucher voucher) {
                deleteVoucher(voucher);
            }
        });

        recyclerViewVouchers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewVouchers.setAdapter(voucherAdapter);
    }

    private void loadVouchers() {
        // TODO: Load vouchers from API/database
        voucherList.clear();
        voucherAdapter.notifyDataSetChanged();
    }

    private void setupListeners() {
        fabAddVoucher.setOnClickListener(v -> openAddVoucherDialog());

        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(VoucherManagementActivity.this, AdminDashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void openAddVoucherDialog() {
        // TODO: Open dialog to add new voucher
        Toast.makeText(this, "Thêm voucher mới", Toast.LENGTH_SHORT).show();
    }

    private void openEditVoucherDialog(Voucher voucher) {
        // TODO: Open dialog to edit voucher
        Toast.makeText(this, "Chỉnh sửa voucher: " + voucher.getCode(), Toast.LENGTH_SHORT).show();
    }

    private void deleteVoucher(Voucher voucher) {
        // TODO: Delete voucher from API/database
        Toast.makeText(this, "Xóa voucher: " + voucher.getCode(), Toast.LENGTH_SHORT).show();
    }
}


