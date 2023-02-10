package com.zipline.inventorymanagement.repositories;

import com.zipline.inventorymanagement.entities.InventoryLineItem;
import com.zipline.inventorymanagement.entities.Product;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface InventoryAndCatalogRepository {

    void initCatalog(List<Product> productInfo);

    void initInventory(List<InventoryLineItem> inventoryInfo);

    Optional<Product> findInCatalogByProductId(String productId);

    Map<String, InventoryLineItem> getCurrentInventory();

    Optional<InventoryLineItem> findInInventoryByProductId(String productId);

    void save(InventoryLineItem product);
}
