package com.example.da1.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;
import com.example.da1.models.CartItem;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private List<CartItem> cartItemList;
    private OnQuantityChangeListener quantityChangeListener;
    private OnDeleteItemListener deleteItemListener;
    private boolean readOnlyMode = false;

    public interface OnQuantityChangeListener {
        void onQuantityChanged(CartItem item, int newQuantity);
    }

    public interface OnDeleteItemListener {
        void onDeleteItem(CartItem item);
    }

    public CartAdapter(List<CartItem> cartItemList) {
        this.cartItemList = cartItemList;
    }

    public void setQuantityChangeListener(OnQuantityChangeListener listener) {
        this.quantityChangeListener = listener;
    }

    public void setDeleteItemListener(OnDeleteItemListener listener) {
        this.deleteItemListener = listener;
    }

    public void setReadOnlyMode(boolean readOnly) {
        this.readOnlyMode = readOnly;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivProductImage;
        private TextView tvBrand;
        private TextView tvProductName;
        private TextView tvColor;
        private View viewColorSwatch;
        private TextView tvSize;
        private TextView tvPrice;
        private LinearLayout llColorSize;
        private TextView tvQuantity;
        private ImageButton btnMinus;
        private ImageButton btnPlus;
        private ImageButton btnDelete;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvBrand = itemView.findViewById(R.id.tvBrand);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvColor = itemView.findViewById(R.id.tvColor);
            viewColorSwatch = itemView.findViewById(R.id.viewColorSwatch);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            llColorSize = itemView.findViewById(R.id.llColorSize);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(CartItem item) {
            // Log để debug
            android.util.Log.d("CartAdapter", "Binding item - ID: " + item.getId() + ", ProductName: " + item.getProductName() + ", Price: " + item.getPrice());
            
            // Load image from URL using Picasso
            String imageUrl = item.getImageUrl();
            if (imageUrl != null && !imageUrl.trim().isEmpty()) {
                imageUrl = imageUrl.trim();
                android.util.Log.d("CartAdapter", "Loading image from URL: " + imageUrl);
                
                // Kiểm tra nếu URL là đầy đủ (bắt đầu bằng http/https)
                if (imageUrl.startsWith("http://") || imageUrl.startsWith("https://")) {
                    // URL đầy đủ, load trực tiếp
                    Picasso.get()
                        .load(imageUrl)
                        .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh placeholder khi đang load
                        .error(android.R.drawable.ic_menu_report_image) // Ảnh hiển thị khi lỗi
                        .into(ivProductImage);
                } else {
                    // Có thể là relative path, thử thêm base URL
                    // Hoặc có thể là local resource, hiển thị placeholder
                    android.util.Log.w("CartAdapter", "Image URL không phải là URL đầy đủ: " + imageUrl);
                    ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
                }
            } else {
                // Set default placeholder nếu không có ảnh
                android.util.Log.d("CartAdapter", "No image URL, using placeholder");
                ivProductImage.setImageResource(android.R.drawable.ic_menu_gallery);
            }

            // Ẩn brand nếu không có
            if (item.getBrand() != null && !item.getBrand().isEmpty()) {
                tvBrand.setVisibility(View.VISIBLE);
                tvBrand.setText(item.getBrand());
            } else {
                tvBrand.setVisibility(View.GONE);
            }
            
            // Đảm bảo tvProductName luôn hiển thị và có text
            String productName = item.getProductName() != null && !item.getProductName().trim().isEmpty() 
                ? item.getProductName().trim() 
                : "Sản phẩm";
            tvProductName.setVisibility(View.VISIBLE);
            tvProductName.setText(productName);
            android.util.Log.d("CartAdapter", "Set product name: " + productName);
            
            // Ẩn phần color/size để đơn giản hóa như mẫu
            if (llColorSize != null) {
                llColorSize.setVisibility(View.GONE);
            }

            // Đảm bảo tvPrice luôn hiển thị và có text
            double price = item.getPrice() > 0 ? item.getPrice() : 0;
            NumberFormat numberFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            String formattedPrice = numberFormat.format((long)price) + "₫";
            tvPrice.setVisibility(View.VISIBLE);
            tvPrice.setText(formattedPrice);
            android.util.Log.d("CartAdapter", "Set price: " + formattedPrice);

            tvQuantity.setText(String.valueOf(item.getQuantity()));

            // Ẩn/disable các nút trong read-only mode (checkout)
            if (readOnlyMode) {
                btnDelete.setVisibility(View.GONE);
                btnMinus.setEnabled(false);
                btnMinus.setAlpha(0.5f);
                btnPlus.setEnabled(false);
                btnPlus.setAlpha(0.5f);
            } else {
                btnDelete.setVisibility(View.VISIBLE);
                btnMinus.setEnabled(true);
                btnMinus.setAlpha(1.0f);
                btnPlus.setEnabled(true);
                btnPlus.setAlpha(1.0f);
                
                btnMinus.setOnClickListener(v -> {
                    if (item.getQuantity() > 1) {
                        int newQuantity = item.getQuantity() - 1;
                        item.setQuantity(newQuantity);
                        tvQuantity.setText(String.valueOf(newQuantity));
                        if (quantityChangeListener != null) {
                            quantityChangeListener.onQuantityChanged(item, newQuantity);
                        }
                    }
                });

                btnPlus.setOnClickListener(v -> {
                    int newQuantity = item.getQuantity() + 1;
                    item.setQuantity(newQuantity);
                    tvQuantity.setText(String.valueOf(newQuantity));
                    if (quantityChangeListener != null) {
                        quantityChangeListener.onQuantityChanged(item, newQuantity);
                    }
                });

                btnDelete.setOnClickListener(v -> {
                    if (deleteItemListener != null) {
                        deleteItemListener.onDeleteItem(item);
                    }
                });
            }
        }
    }

}


