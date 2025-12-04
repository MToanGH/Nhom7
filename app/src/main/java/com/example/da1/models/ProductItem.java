package com.example.da1.models;

public class ProductItem {
    // Sử dụng @SerializedName để map với field names từ API (MongoDB trả về _id)
    @com.google.gson.annotations.SerializedName("_id")
    private String id;
    
    private String name;
    private String code;
    private double price;
    private String imageUrl;
    private java.util.List<String> image; // API trả về mảng ảnh
    
    @com.google.gson.annotations.SerializedName("categoryid")
    private Object categoryid; // Có thể là ObjectId string hoặc Category object sau khi populate
    
    // Helper method để lấy category ID
    public String getCategoryIdString() {
        if (categoryid == null) return null;
        if (categoryid instanceof String) {
            return (String) categoryid;
        }
        // Nếu là object, có thể có _id field
        try {
            java.lang.reflect.Field idField = categoryid.getClass().getDeclaredField("_id");
            idField.setAccessible(true);
            Object idValue = idField.get(categoryid);
            return idValue != null ? idValue.toString() : null;
        } catch (Exception e) {
            return categoryid.toString();
        }
    }
    
    // Helper method để lấy category name nếu có
    public String getCategoryNameFromObject() {
        if (categoryid == null) return null;
        if (categoryid instanceof String) return null;
        try {
            java.lang.reflect.Field nameField = categoryid.getClass().getDeclaredField("name");
            nameField.setAccessible(true);
            Object nameValue = nameField.get(categoryid);
            return nameValue != null ? nameValue.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private String categoryName;
    private double rating;
    private int reviewCount;
    private java.util.List<String> sizes;
    private java.util.List<String> colors;
    private String description;
    private int stock; // Số lượng tồn kho

    public ProductItem() {
    }

    public ProductItem(String id, String name, String code, double price, String imageUrl,
                       String categoryId, String categoryName, double rating, int reviewCount) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.price = price;
        this.imageUrl = imageUrl;
        this.categoryid = categoryId; // Set vào categoryid với @SerializedName("categoryid")
        this.categoryName = categoryName;
        this.rating = rating;
        this.reviewCount = reviewCount;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        // Nếu có imageUrl thì trả về, nếu không thì lấy ảnh đầu tiên từ mảng image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            return imageUrl;
        }
        if (image != null && !image.isEmpty()) {
            return image.get(0);
        }
        return null;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public java.util.List<String> getImage() {
        return image;
    }

    public void setImage(java.util.List<String> image) {
        this.image = image;
    }

    public String getCategoryId() {
        // Lấy từ categoryid
        return getCategoryIdString();
    }

    public void setCategoryId(String categoryId) {
        // Set vào categoryid để Gson serialize với @SerializedName("categoryid")
        this.categoryid = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getReviewCount() {
        return reviewCount;
    }

    public void setReviewCount(int reviewCount) {
        this.reviewCount = reviewCount;
    }

    public java.util.List<String> getSizes() {
        return sizes;
    }

    public void setSizes(java.util.List<String> sizes) {
        this.sizes = sizes;
    }

    public java.util.List<String> getColors() {
        return colors;
    }

    public void setColors(java.util.List<String> colors) {
        this.colors = colors;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
}


