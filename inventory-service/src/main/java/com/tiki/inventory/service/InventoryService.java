package com.tiki.inventory.service;

import com.tiki.inventory.dto.ProductInventoryDTO;
import com.tiki.inventory.model.ProductInventory;
import com.tiki.inventory.repository.ProductInventoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final ProductInventoryRepository repository;

    public InventoryService(ProductInventoryRepository repository) {
        this.repository = repository;
    }

    /**
     * READ — Cache-Aside: cache trước, miss thì DB (+ Spring tự put cache).
     */
    @Cacheable(value = "inventory", key = "#productId",
            condition = "#productId != null and !#productId.isBlank()")
    public ProductInventoryDTO getInventory(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be null or blank");
        }
        ProductInventory entity = repository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        return toDto(entity);
    }

    /**
     * WRITE — Cache-Aside: ghi DB trước, rồi EVICT cache.
     */
    @Transactional
    @CacheEvict(value = "inventory", key = "#productId")
    public ProductInventoryDTO updateInventory(String productId, Integer newQuantity) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be null or blank");
        }
        // Tình huống 1: chặn số âm
        if (newQuantity == null || newQuantity < 0) {
            throw new IllegalArgumentException("newQuantity must be >= 0, got: " + newQuantity);
        }

        ProductInventory entity = repository.findById(productId)
                .orElse(new ProductInventory(productId, 0));
        entity.setQuantity(newQuantity);
        repository.save(entity);

        return toDto(entity);
    }

    private ProductInventoryDTO toDto(ProductInventory e) {
        return new ProductInventoryDTO(e.getProductId(), e.getQuantity());
    }
}
