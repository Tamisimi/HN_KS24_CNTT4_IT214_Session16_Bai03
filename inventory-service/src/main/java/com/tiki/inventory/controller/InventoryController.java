package com.tiki.inventory.controller;

import com.tiki.inventory.dto.ProductInventoryDTO;
import com.tiki.inventory.service.InventoryService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping("/{productId}")
    public ProductInventoryDTO get(@PathVariable String productId) {
        return inventoryService.getInventory(productId);
    }

    @PutMapping("/{productId}")
    public ProductInventoryDTO update(@PathVariable String productId,
                                      @RequestBody Map<String, Integer> body) {
        return inventoryService.updateInventory(productId, body.get("quantity"));
    }
}
