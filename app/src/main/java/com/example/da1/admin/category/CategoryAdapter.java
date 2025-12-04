package com.example.da1.admin.category;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.da1.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onEditClick(Category category);
        void onDeleteClick(Category category);
    }

    public CategoryAdapter(List<Category> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categoryList.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView tvCategoryName;
        private TextView tvCategoryDescription;
        private TextView tvCategoryStatus;
        private ImageButton btnEdit;
        private ImageButton btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
            tvCategoryStatus = itemView.findViewById(R.id.tvCategoryStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(Category category) {
            tvCategoryName.setText(category.getName());
            tvCategoryDescription.setText(category.getDescription());
            tvCategoryStatus.setText(category.isActive() ? "Hoạt động" : "Không hoạt động");

            btnEdit.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onEditClick(category);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteClick(category);
                }
            });
        }
    }
}


