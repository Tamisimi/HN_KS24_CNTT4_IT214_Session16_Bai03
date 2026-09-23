# Bài 3 Session 16 — Cache-Aside quản lý tồn kho

## Pattern

**Đọc:** Cache → miss → DB → put cache  
**Ghi:** DB trước → **evict** cache (không update cache trực tiếp)

## Chạy

- Redis: `localhost:6379` (hoặc tắt Redis để xem fallback qua `CacheErrorHandler`)
- `cd inventory-service && ./gradlew bootRun`

Báo cáo: `BAO_CAO_PHAN_TICH.md`
