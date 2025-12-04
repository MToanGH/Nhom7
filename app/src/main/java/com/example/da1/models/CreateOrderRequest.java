package com.example.da1.models;

public class CreateOrderRequest {
    private String shippingAddressId;
    private String paymentMethod;
    private String voucherCode;

    public CreateOrderRequest() {
    }

    public CreateOrderRequest(String shippingAddressId, String paymentMethod, String voucherCode) {
        this.shippingAddressId = shippingAddressId;
        this.paymentMethod = paymentMethod;
        this.voucherCode = voucherCode;
    }

    public String getShippingAddressId() {
        return shippingAddressId;
    }

    public void setShippingAddressId(String shippingAddressId) {
        this.shippingAddressId = shippingAddressId;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }
}




