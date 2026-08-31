package com.mahesh.freshcart.inventory;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import jakarta.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/inventory")

public class InventoryController {
    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }
    @PostMapping
    public Inventory createInventory(@Valid @RequestBody Inventory inventory) {
        return inventoryService.saveInventory(inventory);
    }
    @GetMapping
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }
    @GetMapping("/{id}")
    public Inventory getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }
    @PutMapping("/{id}")
    public Inventory updateInventory(@PathVariable Long id, @Valid @RequestBody Inventory updatedInventory) {
        return inventoryService.updateInventory(id, updatedInventory);
    }
    @DeleteMapping("/{id}")
    public void deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
    }
    @GetMapping("/{id}/low-stock")
    public boolean isLowStock(@PathVariable Long id) {
        return inventoryService.isLowStock(id);
    }
    @GetMapping("/low-stock")
    public List<Inventory> getLowStockInventory() {
        return inventoryService.getLowStockInventory();
    }
}
