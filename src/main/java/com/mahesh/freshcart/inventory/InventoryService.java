package com.mahesh.freshcart.inventory;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.mahesh.freshcart.ProductRepository;
import java.util.List;
@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository) {

        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
    }
    public Inventory saveInventory(Inventory inventory) {

        if (!productRepository.existsById(inventory.getProductId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Product not found"
            );
        }

        if (inventoryRepository.existsByProductId(inventory.getProductId())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Inventory already exists for this product"
            );
        }

        return inventoryRepository.save(inventory);
    }
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Inventory not found"
                ));

    }
    public Inventory updateInventory(Long id, Inventory updatedInventory) {
        Inventory existingInventory = inventoryRepository.findById(id).orElse(null);
        if (existingInventory == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Inventory not found"
            );
        }
        existingInventory.setStockQuantity(updatedInventory.getStockQuantity());
        existingInventory.setReorderLevel(updatedInventory.getReorderLevel());

        return inventoryRepository.save(existingInventory);
    }
    public void deleteInventory(Long id) {
        if (!inventoryRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Inventory not found"
            );
        }
        inventoryRepository.deleteById(id);
    }
    public boolean isLowStock(Long id) {

        Inventory inventory = getInventoryById(id);

        return inventory.getStockQuantity()
                <= inventory.getReorderLevel();
    }
    public List<Inventory> getLowStockInventory() {

        return inventoryRepository.findAll()
                .stream()
                .filter(inventory ->
                        inventory.getStockQuantity()
                                <= inventory.getReorderLevel())
                .toList();
    }
}
