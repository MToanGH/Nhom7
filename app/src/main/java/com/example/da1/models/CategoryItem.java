package com.example.da1.models;

public class CategoryItem {
    // Sử dụng @SerializedName để map với field names từ API (MongoDB trả về _id)
    @com.google.gson.annotations.SerializedName("_id")
    private String id;
    
    private String name;
    private String imageUrl;

    public CategoryItem() {
    }

    public CategoryItem(String id, String name, String imageUrl) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}


