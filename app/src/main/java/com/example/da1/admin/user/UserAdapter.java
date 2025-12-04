package com.example.da1.admin.user;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private List<User> userList;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onEditClick(User user);
        void onDeleteClick(User user);
        void onToggleActiveClick(User user);
    }

    public UserAdapter(List<User> userList, OnUserClickListener listener) {
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    class UserViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUserName;
        private TextView tvUserEmail;
        private TextView tvUserFullName;
        private TextView tvUserPhone;
        private TextView tvUserRole;
        private TextView tvUserStatus;
        private TextView tvCreatedAt;
        private Switch switchActive;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserFullName = itemView.findViewById(R.id.tvUserFullName);
            tvUserPhone = itemView.findViewById(R.id.tvUserPhone);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            tvCreatedAt = itemView.findViewById(R.id.tvCreatedAt);
            switchActive = itemView.findViewById(R.id.switchActive);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(User user) {
            tvUserName.setText("Tên đăng nhập: " + user.getUsername());
            tvUserEmail.setText("Email: " + user.getEmail());
            tvUserFullName.setText("Họ tên: " + user.getFullName());
            tvUserPhone.setText("SĐT: " + user.getPhone());
            tvUserRole.setText("Vai trò: " + getRoleText(user.getRole()));
            tvUserStatus.setText(user.isActive() ? "Hoạt động" : "Không hoạt động");
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            if (user.getCreatedAt() != null) {
                tvCreatedAt.setText("Ngày tạo: " + dateFormat.format(user.getCreatedAt()));
            }

            switchActive.setChecked(user.isActive());
            switchActive.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null) {
                    listener.onToggleActiveClick(user);
                }
            });

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(user);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(user);
                }
            });
        }

        private String getRoleText(String role) {
            switch (role) {
                case "ADMIN":
                    return "Quản trị viên";
                case "CUSTOMER":
                    return "Khách hàng";
                default:
                    return role;
            }
        }
    }
}


