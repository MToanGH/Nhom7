package com.example.da1.models;

public class CartItem {
    private String id;
    private String productId;
    private String productName;
    private String productCode;
    private String brand;
    private String color;
    private String size;
    private double price;
    private int quantity;
    private String imageUrl;

    public CartItem() {
    }

    public CartItem(String id, String productId, String productName, String productCode,
                    String brand, String color, String size, double price, int quantity, String imageUrl) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.productCode = productCode;
        this.brand = brand;
        this.color = color;
        this.size = size;
        this.price = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getTotalPrice() {
        return price * quantity;
    }
}


