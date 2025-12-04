package com.example.da1.admin.voucher;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<Voucher> voucherList;
    private OnVoucherClickListener listener;

    public interface OnVoucherClickListener {
        void onEditClick(Voucher voucher);
        void onDeleteClick(Voucher voucher);
    }

    public VoucherAdapter(List<Voucher> voucherList, OnVoucherClickListener listener) {
        this.voucherList = voucherList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);
        holder.bind(voucher);
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    class VoucherViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVoucherCode;
        private TextView tvVoucherDescription;
        private TextView tvDiscount;
        private TextView tvMinPurchase;
        private TextView tvValidDate;
        private TextView tvUsageLimit;
        private TextView tvVoucherStatus;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVoucherCode = itemView.findViewById(R.id.tvVoucherCode);
            tvVoucherDescription = itemView.findViewById(R.id.tvVoucherDescription);
            tvDiscount = itemView.findViewById(R.id.tvDiscount);
            tvMinPurchase = itemView.findViewById(R.id.tvMinPurchase);
            tvValidDate = itemView.findViewById(R.id.tvValidDate);
            tvUsageLimit = itemView.findViewById(R.id.tvUsageLimit);
            tvVoucherStatus = itemView.findViewById(R.id.tvVoucherStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Voucher voucher) {
            tvVoucherCode.setText("Mã: " + voucher.getCode());
            tvVoucherDescription.setText(voucher.getDescription());
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            if ("PERCENTAGE".equals(voucher.getDiscountType())) {
                tvDiscount.setText("Giảm: " + voucher.getDiscountAmount() + "%");
            } else {
                tvDiscount.setText("Giảm: " + currencyFormat.format(voucher.getDiscountAmount()));
            }
            
            tvMinPurchase.setText("Đơn tối thiểu: " + currencyFormat.format(voucher.getMinPurchaseAmount()));
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dateRange = "";
            if (voucher.getStartDate() != null && voucher.getEndDate() != null) {
                dateRange = dateFormat.format(voucher.getStartDate()) + " - " + dateFormat.format(voucher.getEndDate());
            }
            tvValidDate.setText("Hiệu lực: " + dateRange);
            
            tvUsageLimit.setText("Đã dùng: " + voucher.getUsedCount() + "/" + voucher.getUsageLimit());
            tvVoucherStatus.setText(voucher.isActive() ? "Hoạt động" : "Không hoạt động");

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(voucher);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(voucher);
                }
            });
        }
    }
}


