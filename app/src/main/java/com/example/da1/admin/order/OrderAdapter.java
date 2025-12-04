package com.example.da1.admin.order;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private List<Order> orderList;
    private OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onViewClick(Order order);
        void onStatusChangeClick(Order order);
    }

    public OrderAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.bind(order);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    class OrderViewHolder extends RecyclerView.ViewHolder {
        private TextView tvOrderId;
        private TextView tvCustomerName;
        private TextView tvOrderDate;
        private TextView tvTotalAmount;
        private TextView tvOrderStatus;
        private TextView tvPaymentMethod;
        private Button btnViewDetails;
        private Button btnChangeStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvCustomerName = itemView.findViewById(R.id.tvCustomerName);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalAmount = itemView.findViewById(R.id.tvTotalAmount);
            tvOrderStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvPaymentMethod = itemView.findViewById(R.id.tvPaymentMethod);
            btnViewDetails = itemView.findViewById(R.id.btnViewDetails);
            btnChangeStatus = itemView.findViewById(R.id.btnChangeStatus);
        }

        public void bind(Order order) {
            tvOrderId.setText("Mã đơn: " + order.getId());
            tvCustomerName.setText("Khách hàng: " + order.getUserName());
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            if (order.getOrderDate() != null) {
                tvOrderDate.setText("Ngày đặt: " + dateFormat.format(order.getOrderDate()));
            }
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvTotalAmount.setText("Tổng tiền: " + currencyFormat.format(order.getTotalAmount()));
            
            tvOrderStatus.setText("Trạng thái: " + getStatusText(order.getStatus()));
            tvPaymentMethod.setText("Thanh toán: " + order.getPaymentMethod());

            btnViewDetails.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onViewClick(order);
                }
            });

            btnChangeStatus.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onStatusChangeClick(order);
                }
            });
        }

        private String getStatusText(String status) {
            switch (status) {
                case "PENDING":
                    return "Chờ xử lý";
                case "CONFIRMED":
                    return "Đã xác nhận";
                case "SHIPPING":
                    return "Đang giao hàng";
                case "DELIVERED":
                    return "Đã giao";
                case "CANCELLED":
                    return "Đã hủy";
                default:
                    return status;
            }
        }
    }
}


