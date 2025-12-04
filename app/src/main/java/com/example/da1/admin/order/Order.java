package com.example.da1.admin.order;

import com.google.gson.annotations.SerializedName;
import java.util.Date;
import java.util.List;

public class Order {
    @SerializedName("_id")
    private String id;
    
    @SerializedName("accountid")
    private String userId;
    
    private String userName;
    private String userEmail;
    private List<OrderItem> items;
    
    @SerializedName("totalAmount")
    private double totalAmount;
    
    private String status; // pending, processing, shipped, delivered, cancelled
    
    @SerializedName("paymentMethod")
    private String paymentMethod;
    
    @SerializedName("shippingAddress")
    private Object shippingAddress; // Có thể là ObjectId string hoặc Address object
    
    @SerializedName("createdAt")
    private Object orderDate; // Có thể là Date hoặc String từ API
    
    private Date deliveryDate;

    public Order() {
    }

    public Order(String id, String userId, String userName, String userEmail, List<OrderItem> items,
                 double totalAmount, String status, String paymentMethod, Object shippingAddress,
                 Object orderDate, Date deliveryDate) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.items = items;
        this.totalAmount = totalAmount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.shippingAddress = shippingAddress;
        this.orderDate = orderDate;
        this.deliveryDate = deliveryDate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Object getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Object shippingAddress) {
        this.shippingAddress = shippingAddress;
    }
    
    // Helper method để lấy shipping address string
    public String getShippingAddressString() {
        if (shippingAddress == null) return null;
        if (shippingAddress instanceof String) {
            return (String) shippingAddress;
        }
        // Nếu là object, có thể có các field như fullName, address, city, etc.
        try {
            java.lang.reflect.Field addressField = shippingAddress.getClass().getDeclaredField("address");
            addressField.setAccessible(true);
            Object addressValue = addressField.get(shippingAddress);
            return addressValue != null ? addressValue.toString() : null;
        } catch (Exception e) {
            return shippingAddress.toString();
        }
    }

    public Date getOrderDate() {
        if (orderDate == null) return null;
        if (orderDate instanceof Date) {
            return (Date) orderDate;
        }
        if (orderDate instanceof String) {
            try {
                // Parse ISO 8601 format: "2024-01-01T10:00:00.000Z"
                java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.getDefault());
                sdf.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                return sdf.parse((String) orderDate);
            } catch (Exception e) {
                try {
                    // Try another format
                    java.text.SimpleDateFormat sdf2 = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", java.util.Locale.getDefault());
                    sdf2.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                    return sdf2.parse((String) orderDate);
                } catch (Exception e2) {
                    return null;
                }
            }
        }
        return null;
    }

    public void setOrderDate(Object orderDate) {
        this.orderDate = orderDate;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public static class OrderItem {
        @SerializedName("_id")
        private String id;
        
        @SerializedName("productid")
        private Object productid; // Có thể là ObjectId string hoặc Product object sau khi populate
        
        @SerializedName("billid")
        private String billid;
        
        private int quantity;
        private String size;
        private String color;
        private double price;
        private String status;

        public OrderItem() {
        }

        public OrderItem(String id, Object productid, String billid, int quantity, String size, 
                       String color, double price, String status) {
            this.id = id;
            this.productid = productid;
            this.billid = billid;
            this.quantity = quantity;
            this.size = size;
            this.color = color;
            this.price = price;
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getProductid() {
            return productid;
        }

        public void setProductid(Object productid) {
            this.productid = productid;
        }
        
        // Helper method để lấy product ID
        public String getProductId() {
            if (productid == null) return null;
            if (productid instanceof String) {
                return (String) productid;
            }
            try {
                java.lang.reflect.Field idField = productid.getClass().getDeclaredField("_id");
                idField.setAccessible(true);
                Object idValue = idField.get(productid);
                return idValue != null ? idValue.toString() : null;
            } catch (Exception e) {
                return productid.toString();
            }
        }
        
        // Helper method để lấy product name
        public String getProductName() {
            if (productid == null) return null;
            if (productid instanceof String) return null;
            try {
                java.lang.reflect.Field nameField = productid.getClass().getDeclaredField("name");
                nameField.setAccessible(true);
                Object nameValue = nameField.get(productid);
                return nameValue != null ? nameValue.toString() : null;
            } catch (Exception e) {
                return null;
            }
        }

        public String getBillid() {
            return billid;
        }

        public void setBillid(String billid) {
            this.billid = billid;
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

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }
}


