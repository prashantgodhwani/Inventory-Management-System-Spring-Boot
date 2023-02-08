package com.zipline.inventorymanagement.repositories;

import com.zipline.inventorymanagement.entities.InventoryLineItem;
import com.zipline.inventorymanagement.entities.Product;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface InventoryAndCatalogRepository {

    void initCatalog(List<Product> productInfo);

    void initInventory(List<InventoryLineItem> inventoryInfo);

    Optional<Product> findInCatalogByProductId(int productId);

    Map<Integer, InventoryLineItem> getCurrentInventory();

    Optional<InventoryLineItem> findInInventoryByProductId(int productId);

    void save(InventoryLineItem product);
}
