package com.example.da1.models;

public class UpdateCartRequest {
    private int quantity;

    public UpdateCartRequest() {
    }

    public UpdateCartRequest(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}




