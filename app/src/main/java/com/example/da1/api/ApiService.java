package com.example.da1.api;

public class ApiService {
    private static AuthApiService authApiService;
    private static ProductApiService productApiService;
    private static CategoryApiService categoryApiService;
    private static CartApiService cartApiService;
    private static OrderApiService orderApiService;
    private static UserApiService userApiService;
    private static VoucherApiService voucherApiService;
    private static SearchApiService searchApiService;
    private static AddressApiService addressApiService;

    public static AuthApiService getAuthApiService() {
        if (authApiService == null) {
            authApiService = ApiClient.getClient().create(AuthApiService.class);
        }
        return authApiService;
    }

    public static ProductApiService getProductApiService() {
        if (productApiService == null) {
            productApiService = ApiClient.getClient().create(ProductApiService.class);
        }
        return productApiService;
    }

    public static CategoryApiService getCategoryApiService() {
        if (categoryApiService == null) {
            categoryApiService = ApiClient.getClient().create(CategoryApiService.class);
        }
        return categoryApiService;
    }

    public static CartApiService getCartApiService() {
        if (cartApiService == null) {
            cartApiService = ApiClient.getClient().create(CartApiService.class);
        }
        return cartApiService;
    }

    public static OrderApiService getOrderApiService() {
        if (orderApiService == null) {
            orderApiService = ApiClient.getClient().create(OrderApiService.class);
        }
        return orderApiService;
    }

    public static UserApiService getUserApiService() {
        if (userApiService == null) {
            userApiService = ApiClient.getClient().create(UserApiService.class);
        }
        return userApiService;
    }

    public static VoucherApiService getVoucherApiService() {
        if (voucherApiService == null) {
            voucherApiService = ApiClient.getClient().create(VoucherApiService.class);
        }
        return voucherApiService;
    }

    public static SearchApiService getSearchApiService() {
        if (searchApiService == null) {
            searchApiService = ApiClient.getClient().create(SearchApiService.class);
        }
        return searchApiService;
    }

    public static AddressApiService getAddressApiService() {
        if (addressApiService == null) {
            addressApiService = ApiClient.getClient().create(AddressApiService.class);
        }
        return addressApiService;
    }
}


