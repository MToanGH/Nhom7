package com.example.da1.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;
import com.example.da1.models.ProductItem;

import java.io.InputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<ProductItem> productList;
    private OnProductClickListener listener;
    private OnProductEditListener editListener;
    private OnProductDeleteListener deleteListener;

    public interface OnProductClickListener {
        void onProductClick(ProductItem product);
    }

    public interface OnProductEditListener {
        void onProductEdit(ProductItem product);
    }

    public interface OnProductDeleteListener {
        void onProductDelete(ProductItem product);
    }

    public ProductAdapter(List<ProductItem> productList, OnProductClickListener listener) {
        this.productList = productList;
        this.listener = listener;
    }

    public void setEditListener(OnProductEditListener editListener) {
        this.editListener = editListener;
    }

    public void setDeleteListener(OnProductDeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_home, parent, false);
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
        private ImageView ivProductImage;
        private TextView tvProductName;
        private TextView tvProductPrice;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(ProductItem product) {
            tvProductName.setText(product.getName() + " - " + product.getCode());
            
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            tvProductPrice.setText(currencyFormat.format(product.getPrice()));

            // Load image from URL
            if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
                new LoadImageTask(ivProductImage).execute(product.getImageUrl());
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onProductClick(product);
                }
            });

            // Chỉ hiển thị nút edit/delete nếu có listener (tức là admin)
            if (btnEdit != null) {
                if (editListener != null) {
                    btnEdit.setVisibility(View.VISIBLE);
                    btnEdit.setOnClickListener(v -> {
                        editListener.onProductEdit(product);
                    });
                } else {
                    btnEdit.setVisibility(View.GONE);
                }
            }

            if (btnDelete != null) {
                if (deleteListener != null) {
                    btnDelete.setVisibility(View.VISIBLE);
                    btnDelete.setOnClickListener(v -> {
                        deleteListener.onProductDelete(product);
                    });
                } else {
                    btnDelete.setVisibility(View.GONE);
                }
            }
        }
    }

    private static class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView;

        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... urls) {
            String url = urls[0];
            Bitmap bitmap = null;
            try {
                InputStream inputStream = new URL(url).openStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
