package com.example.da1.models;

import com.google.gson.annotations.SerializedName;

public class Address {
    @SerializedName("_id")
    private String id;
    private String fullName;
    private String phone;
    private String address;
    private String city;
    private String district;
    private String ward;
    private boolean isDefault;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getWard() {
        return ward;
    }

    public void setWard(String ward) {
        this.ward = ward;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getFullAddress() {
        StringBuilder sb = new StringBuilder();
        if (address != null && !address.isEmpty()) {
            sb.append(address);
        }
        if (ward != null && !ward.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(ward);
        }
        if (district != null && !district.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(district);
        }
        if (city != null && !city.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(city);
        }
        return sb.toString();
    }
}




