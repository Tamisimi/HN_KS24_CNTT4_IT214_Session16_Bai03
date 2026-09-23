# Báo cáo — Cache-Aside Pattern (Tồn kho)

## a) Luồng đọc / ghi

### Read (Cache-Aside)
```
getInventory(productId)
  1. Tìm key trong Redis
  2a. HIT  → trả DTO từ cache
  2b. MISS → SELECT DB → PUT cache → trả DTO
```

**I/O đọc**
- Input: `productId` (String, not blank)
- Output: `ProductInventoryDTO { productId, quantity }`

### Write (Cache-Aside)
```
updateInventory(productId, newQuantity)
  1. Validate newQuantity >= 0
  2. UPDATE DB
  3. EVICT cache key (lần đọc sau sẽ miss và nạp số mới)
```

**I/O ghi**
- Input: `productId`, `newQuantity` (Integer ≥ 0)
- Output: `ProductInventoryDTO` sau cập nhật

Ví dụ iPhone 15: 100 → update 95 → DB=95, cache xóa → user xem → miss → DB 95 → nạp cache.

---

## b) Bẫy dữ liệu

### Số âm (`newQuantity = -10`)

Fail-fast trong service: ném `IllegalArgumentException` **trước** khi ghi DB / đụng cache → không làm hỏng tồn kho.

### Redis đứt kết nối

Dùng `CacheErrorHandler` (hoặc `RedisCacheManager` + error handler):
- **get lỗi:** log warn, coi như miss → đọc DB bình thường (user không thấy 500).
- **evict/put lỗi:** log error, **DB đã đúng**; dựa vào **TTL ngắn** (ví dụ 60s) để cache cũ tự hết hạn, lần đọc sau lấy DB mới.

---

## c) Giải pháp sự cố Redis

| Thao tác | Khi Redis lỗi |
|----------|----------------|
| Read (get) | Fallback DB — `handleCacheGetError` không rethrow |
| Evict | Không rollback DB; TTL + lần đọc sau tự sửa cache |
| Put | Bỏ qua; lần sau miss sẽ put lại |

Nguyên tắc Cache-Aside: **DB là source of truth**. Cache chỉ tăng tốc; mất Redis hệ thống vẫn đúng, chỉ chậm hơn.
