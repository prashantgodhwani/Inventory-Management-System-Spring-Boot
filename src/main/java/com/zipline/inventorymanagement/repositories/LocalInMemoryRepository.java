package com.zipline.inventorymanagement.repositories;

import com.zipline.inventorymanagement.entities.InventoryLineItem;
import com.zipline.inventorymanagement.entities.Product;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@Log4j2
public class LocalInMemoryRepository implements InventoryAndCatalogRepository {

    private Map<String, Product> catalog = new HashMap<>();

    private Map<String, InventoryLineItem> inventory = new HashMap<>();

    @Override
    public void initCatalog(@RequestBody List<Product> productInfo) {
        for (Product product : productInfo) {
            log.info("Adding to catalog " + product);
            catalog.put(product.getProductId(), product);
        }
    }

    @Override
    public void initInventory(@RequestBody List<InventoryLineItem> inventoryInfo) {
        for (InventoryLineItem item : inventoryInfo) {
            if (catalog.containsKey(item.getProductId())) {
                log.info("Adding product [" + item.getProductId() + "] with quantity " + item.getQuantity() + " to inventory");
                inventory.put(item.getProductId(), item);
            } else
                log.error("Cannot add inventory to product [" + item.getProductId() + "] not in catalog. Skipping.");
        }
    }

    @Override
    public Optional<Product> findInCatalogByProductId(String productId) {
        return Optional.ofNullable(this.catalog.get(productId));
    }

    @Override
    public Map<String, InventoryLineItem> getCurrentInventory() {
        return this.inventory;
    }


    @Override
    public Optional<InventoryLineItem> findInInventoryByProductId(String productId) {
        return Optional.ofNullable(this.inventory.get(productId));
    }

    @Override
    public void save(InventoryLineItem product) {
        log.info("updating inventory of " + product.getProductId() + " with new quantity : " + product.getQuantity());
        this.inventory.put(product.getProductId(), product);
    }

}
