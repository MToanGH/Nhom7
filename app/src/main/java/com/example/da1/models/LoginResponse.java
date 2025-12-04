package com.example.da1.models;

public class LoginResponse {
    private UserData user;
    private String token;
    private String refreshToken;

    public LoginResponse() {
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    // Inner class để match với response từ API
    public static class UserData {
        @com.google.gson.annotations.SerializedName("_id")
        private String id;
        private String email;
        private String name;
        private String phone;
        private String avatar;
        private String role; // ADMIN hoặc CUSTOMER

        public UserData() {
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public boolean isAdmin() {
            return "ADMIN".equalsIgnoreCase(role);
        }
    }
}


