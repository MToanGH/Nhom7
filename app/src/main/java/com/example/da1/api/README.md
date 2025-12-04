# Hướng dẫn sử dụng API

## Cấu hình API URL

1. Mở file `ApiClient.java`
2. Thay đổi `BASE_URL` thành URL API của bạn:
```java
private static final String BASE_URL = "http://your-api-url.com/api/";
```

## Các API Service đã tạo

### 1. AuthApiService - Xác thực
- `login()` - Đăng nhập
- `register()` - Đăng ký
- `logout()` - Đăng xuất
- `forgotPassword()` - Quên mật khẩu

### 2. ProductApiService - Sản phẩm
- `getAllProducts()` - Lấy danh sách sản phẩm
- `getProductById()` - Lấy chi tiết sản phẩm
- `createProduct()` - Tạo sản phẩm mới
- `updateProduct()` - Cập nhật sản phẩm
- `deleteProduct()` - Xóa sản phẩm

### 3. CategoryApiService - Danh mục
- `getAllCategories()` - Lấy danh sách danh mục
- `getCategoryById()` - Lấy chi tiết danh mục
- `createCategory()` - Tạo danh mục mới
- `updateCategory()` - Cập nhật danh mục
- `deleteCategory()` - Xóa danh mục

### 4. CartApiService - Giỏ hàng
- `getCartItems()` - Lấy danh sách giỏ hàng
- `addToCart()` - Thêm vào giỏ hàng
- `updateCartItem()` - Cập nhật sản phẩm trong giỏ
- `removeFromCart()` - Xóa khỏi giỏ hàng
- `clearCart()` - Xóa toàn bộ giỏ hàng

### 5. OrderApiService - Đơn hàng
- `getAllOrders()` - Lấy danh sách đơn hàng
- `getOrderById()` - Lấy chi tiết đơn hàng
- `createOrder()` - Tạo đơn hàng mới
- `updateOrderStatus()` - Cập nhật trạng thái đơn hàng

### 6. UserApiService - Người dùng
- `getAllUsers()` - Lấy danh sách người dùng
- `getUserById()` - Lấy chi tiết người dùng
- `createUser()` - Tạo người dùng mới
- `updateUser()` - Cập nhật người dùng
- `deleteUser()` - Xóa người dùng

### 7. VoucherApiService - Voucher
- `getAllVouchers()` - Lấy danh sách voucher
- `getVoucherById()` - Lấy chi tiết voucher
- `validateVoucher()` - Kiểm tra voucher
- `createVoucher()` - Tạo voucher mới
- `updateVoucher()` - Cập nhật voucher
- `deleteVoucher()` - Xóa voucher

## Cách sử dụng

### Ví dụ: Gọi API lấy danh sách sản phẩm

```java
ApiService.getProductApiService().getAllProducts(null, null, 1, 10)
    .enqueue(new Callback<ApiResponse<List<ProductItem>>>() {
        @Override
        public void onResponse(Call<ApiResponse<List<ProductItem>>> call, 
                               Response<ApiResponse<List<ProductItem>>> response) {
            if (response.isSuccessful() && response.body() != null) {
                ApiResponse<List<ProductItem>> apiResponse = response.body();
                if (apiResponse.isSuccess()) {
                    List<ProductItem> products = apiResponse.getData();
                    // Xử lý dữ liệu
                } else {
                    // Xử lý lỗi từ API
                    String errorMessage = apiResponse.getMessage();
                }
            }
        }

        @Override
        public void onFailure(Call<ApiResponse<List<ProductItem>>> call, Throwable t) {
            // Xử lý lỗi kết nối
        }
    });
```

## Lưu ý

1. **Internet Permission**: Đã thêm vào AndroidManifest.xml
2. **HTTP Logging**: Đã bật để debug API calls
3. **Timeout**: 30 giây cho connect, read, write
4. **SharedPreferences**: Dùng để lưu token và thông tin user

## Format Response từ API

API nên trả về format:
```json
{
  "success": true,
  "message": "Thành công",
  "data": { ... }
}
```


