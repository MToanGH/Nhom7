package com.example.da1.admin.product;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;
import com.example.da1.models.ProductItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductManagementAdapter extends RecyclerView.Adapter<ProductManagementAdapter.ProductViewHolder> {
    private List<ProductItem> productList;
    private OnProductClickListener listener;

    public interface OnProductClickListener {
        void onEditClick(ProductItem product);
        void onDeleteClick(ProductItem product);
    }

    public ProductManagementAdapter(List<ProductItem> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductItem product = productList.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView tvProductName;
        private TextView tvProductPrice;
        private TextView tvProductStock;
        private TextView tvProductCategory;
        private TextView tvProductStatus;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductStock = itemView.findViewById(R.id.tvProductStock);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategory);
            tvProductStatus = itemView.findViewById(R.id.tvProductStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(ProductItem product) {
            if (tvProductName != null) {
                tvProductName.setText(product.getName() + " - " + product.getCode());
            }

            if (tvProductPrice != null) {
                NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                tvProductPrice.setText(currencyFormat.format(product.getPrice()));
            }

            if (tvProductStock != null) {
                tvProductStock.setText("Tồn kho: " + product.getStock());
            }

            if (tvProductCategory != null) {
                String categoryName = product.getCategoryNameFromObject();
                tvProductCategory.setText("Danh mục: " + (categoryName != null ? categoryName : "N/A"));
            }

            if (tvProductStatus != null) {
                // ProductItem có thể không có trường isActive, tạm thời hiển thị "Hoạt động"
                tvProductStatus.setText("Hoạt động");
            }

            if (btnEdit != null) {
                btnEdit.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onEditClick(product);
                    }
                });
            }

            if (btnDelete != null) {
                btnDelete.setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onDeleteClick(product);
                    }
                });
            }
        }
    }
}

