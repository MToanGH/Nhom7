package com.example.da1.admin.voucher;

import java.util.Date;

public class Voucher {
    private String id;
    private String code;
    private String description;
    private double discountAmount; // Số tiền giảm hoặc phần trăm
    private String discountType; // PERCENTAGE, FIXED_AMOUNT
    private double minPurchaseAmount; // Số tiền tối thiểu để áp dụng
    private Date startDate;
    private Date endDate;
    private int usageLimit; // Số lần sử dụng tối đa
    private int usedCount; // Số lần đã sử dụng
    private boolean isActive;

    public Voucher() {
    }

    public Voucher(String id, String code, String description, double discountAmount,
                   String discountType, double minPurchaseAmount, Date startDate, Date endDate,
                   int usageLimit, int usedCount, boolean isActive) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.discountAmount = discountAmount;
        this.discountType = discountType;
        this.minPurchaseAmount = minPurchaseAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.usageLimit = usageLimit;
        this.usedCount = usedCount;
        this.isActive = isActive;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getDiscountType() {
        return discountType;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public double getMinPurchaseAmount() {
        return minPurchaseAmount;
    }

    public void setMinPurchaseAmount(double minPurchaseAmount) {
        this.minPurchaseAmount = minPurchaseAmount;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}


