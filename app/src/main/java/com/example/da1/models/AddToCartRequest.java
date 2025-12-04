package com.example.da1.models;

public class AddToCartRequest {
    private String productid;
    private int quantity;
    private String size;
    private String color;

    public AddToCartRequest() {
    }

    public AddToCartRequest(String productid, int quantity, String size, String color) {
        this.productid = productid;
        this.quantity = quantity;
        this.size = size;
        this.color = color;
    }

    public String getProductid() {
        return productid;
    }

    public void setProductid(String productid) {
        this.productid = productid;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}




