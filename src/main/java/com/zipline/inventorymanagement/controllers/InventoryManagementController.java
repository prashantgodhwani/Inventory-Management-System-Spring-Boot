package com.zipline.inventorymanagement.controllers;

import com.zipline.inventorymanagement.entities.*;
import com.zipline.inventorymanagement.logging.Traceable;
import com.zipline.inventorymanagement.repositories.InventoryAndCatalogRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@RestController
@Validated
@Log4j2
public class InventoryManagementController {

    @Autowired
    private InventoryAndCatalogRepository inventoryAndCatalogRepository;

    private static final int MAX_WEIGHT_IN_GMS = 1800;

    @PostMapping("/init_catalog")
    @Traceable
    public void initCatalog(@Valid @RequestBody List<Product> productInfo) {
        inventoryAndCatalogRepository.initCatalog(productInfo);
    }

    @PostMapping("/init_inventory")
    @Traceable
    public void initInventory(@Valid @RequestBody List<InventoryLineItem> inventoryInfo) {
        inventoryAndCatalogRepository.initInventory(inventoryInfo);
    }

    @PostMapping("/process_order")
    @Traceable
    public List<Shipment> processOrder(@Valid @RequestBody Order order){
        //Get the list of all the requested products
        List<Requested> requestedProducts = order.getRequested();
        List<Shipment> shipmentsForOrder = new ArrayList<>();

        while (!requestedProducts.isEmpty()) {
            //initialize the shipment cart, and set the shipmentWeight to 0
            List<ShippedItem> shippedItems = new ArrayList<>();
            int shipmentMass = 0;

            Iterator<Requested> iterator = requestedProducts.iterator();

            while (iterator.hasNext()) {
                Requested item = iterator.next();
                String productId = item.getProductId();

                //check if the product requested is in the inventory
                Optional<Product> product = inventoryAndCatalogRepository.findInCatalogByProductId(productId);

                //if product is present, add to cart else check next product
                if(product.isPresent()) {

                    //check if item present in catalog has inventory
                    Optional<InventoryLineItem> inventoryLineItem = inventoryAndCatalogRepository.findInInventoryByProductId(productId);

                    if(inventoryLineItem.isEmpty() || inventoryLineItem.get().getQuantity() == 0){
                        log.info("Product with id " + productId + " has 0 availability in inventory");
                        iterator.remove();
                        continue;
                    }

                    //find minimum quantity that can be fulfilled
                    int quantity = Math.min(inventoryLineItem.get().getQuantity(), item.getQuantity());
                    log.info("Quantity of requested product [" + productId + "] that can be fulfilled : " + quantity);

                    //get mass for quantity
                    int productMass = product.get().getMassG();

                    //check if the entire quantity can be sent in one shipment (<= 1800 GMS)
                    if (shipmentMass + (productMass * quantity) <= MAX_WEIGHT_IN_GMS) {
                        log.info("Entire Requested quantity of product : " + productId + " can be shipped in current shipment.");
                        shippedItems.add(ShippedItem.builder().productId(productId).quantity(quantity).build());
                        shipmentMass += productMass * quantity;

                        //update remaining quantity in inventory
                        int remainingQuantity = inventoryLineItem.get().getQuantity() - quantity;
                        log.info("Updating remaining quantity of product [" + productId + "] in inventory. Remaining quantity : " + remainingQuantity);
                        inventoryAndCatalogRepository.save(InventoryLineItem.builder()
                                .productId(productId)
                                .quantity(remainingQuantity)
                                .build());

                        //if entire quantity fulfilled, remove from list, else update remaining requested quantity for next shipment
                        if (quantity == item.getQuantity()) {
                            log.info("Entire requested quantity fulfilled of product [" + productId + "].");
                            iterator.remove();
                        } else {
                            log.info("Remaining requested quantity of product [" + productId + "] to be fulfilled.");
                            item.setQuantity(item.getQuantity() - quantity);
                        }
                    } else {
                        //get the quantity of the product that can be shipped
                        int shippedQuantity = (MAX_WEIGHT_IN_GMS - shipmentMass) / productMass;
                        log.info("Possible Requested quantity of product [ " + productId + " ] that can " +
                                "be fulfilled in current shipment : " + shippedQuantity);

                        if(shippedQuantity != 0) {
                            shippedItems.add(ShippedItem.builder().productId(productId).quantity(shippedQuantity).build());
                            inventoryAndCatalogRepository.save(InventoryLineItem.builder()
                                    .productId(productId)
                                    .quantity(inventoryLineItem.get().getQuantity() - shippedQuantity)
                                    .build());
                            item.setQuantity(item.getQuantity() - shippedQuantity);
                            break;
                        }
                    }
                }else{
                    iterator.remove();
                    log.info("Product with id " + productId + " not found in catalog. Continuing with other items.");
                }
            }

            //assign zip and ship the package
            if(!shippedItems.isEmpty()) {
                log.info("Shipping item(s) with order_id " + order.getOrderId());
                Shipment shipment = Shipment.builder().orderId(order.getOrderId()).shippedItems(shippedItems).build();
                shipmentsForOrder.add(shipment);
                shipPackage(shipment);
            }
        }

        return shipmentsForOrder;
    }

    @GetMapping("/ship_package")
    @ResponseBody
    @Traceable
    private void shipPackage(Shipment shipment) {
        log.info("Shipped order : " + shipment);
    }

    @GetMapping("/get_inventory")
    @ResponseBody
    @Traceable
    public Map<String, InventoryLineItem> getInventory(){
        return this.inventoryAndCatalogRepository.getCurrentInventory();
    }
}
