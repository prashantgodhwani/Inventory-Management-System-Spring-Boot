package com.zipline.inventorymanagement;

import com.zipline.inventorymanagement.controllers.InventoryManagementController;
import com.zipline.inventorymanagement.entities.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class InventoryManagementApplicationTests {

	@Autowired
	private InventoryManagementController controller;

	private List<Product> productInfoList = List.of(new Product(500, "RBC A+ Adult", "1"),
			new Product(700, "RBC B+ Adult", "2"),
			new Product(400, "RBC AB+ Adult", "3"),
			new Product(1800, "Test-Max-weight", "120"));

	@Test
	public void testInitCatalog_Success() {
		controller.initCatalog(productInfoList);
		Map<String, InventoryLineItem> actualInventory = controller.getInventory();
		assertEquals(4, actualInventory.size());
		assertEquals(actualInventory.get("1").getQuantity(), 0);
		assertEquals(actualInventory.get("2").getQuantity(), 0);
		assertEquals(actualInventory.get("3").getQuantity(), 0);
		assertEquals(actualInventory.get("120").getQuantity(), 0);
	}

	@Test
	public void test_Items_In_Catalog_Are_Only_Added_In_Inventory_Success() {
		Map<String, InventoryLineItem> actualInventory = controller.getInventory();
		assertEquals(actualInventory.size(), 4);
	}

	@Test
	public void testProcessOrder_Success() {
		controller.initCatalog(productInfoList);
		controller.initInventory(List.of(new InventoryLineItem(10, "1"), new InventoryLineItem(10, "2"), new InventoryLineItem(10, "3")));
		List<Requested> requested = new ArrayList<>();
		requested.add(new Requested(2, "2"));
		requested.add(new Requested(2, "3"));
		Order order = new Order("123", requested);

		List<Shipment> shipmentList = controller.processOrder(order);

		List<ShippedItem> shippedProductList1 = new ArrayList<>();
		shippedProductList1.add(new ShippedItem(2, "2"));

		List<ShippedItem> shippedProductList2 = new ArrayList<>();
		shippedProductList2.add(new ShippedItem(2, "3"));

		List<Shipment> expectedShipmentList = Arrays.asList(new Shipment("123", shippedProductList1), new Shipment("123", shippedProductList2));

		assertEquals(expectedShipmentList.size(), shipmentList.size());
		assertEquals(expectedShipmentList.get(0).getShippedItems().size(), shipmentList.get(0).getShippedItems().size());
	}

	@Test
	public void testProcessOrder_RequestedItemNotInInventory_Failure() {
		controller.initCatalog(productInfoList);
		controller.initInventory(List.of(new InventoryLineItem(10, "1"), new InventoryLineItem(10, "2"), new InventoryLineItem(10, "3")));
		List<Requested> requested = new ArrayList<>();
		requested.add(new Requested(2, "20"));
		requested.add(new Requested(2, "3"));
		Order order = new Order("123", requested);

		List<Shipment> shipmentList = controller.processOrder(order);

		List<ShippedItem> shippedProductList1 = new ArrayList<>();
		shippedProductList1.add(new ShippedItem(2, "2"));

		List<Shipment> expectedShipmentList = Collections.singletonList(new Shipment("123", shippedProductList1));

		assertEquals(expectedShipmentList.size(), shipmentList.size());
		assertEquals(expectedShipmentList.get(0).getShippedItems().size(), shipmentList.get(0).getShippedItems().size());
	}

	@Test
	public void testProcessOrder_NoOfShipments_SingleProduct_Success() {
		controller.initCatalog(productInfoList);
		controller.initInventory(List.of(new InventoryLineItem(10, "1"), new InventoryLineItem(10, "2"), new InventoryLineItem(10, "120")));
		List<Requested> requested = new ArrayList<>();
		requested.add(new Requested(4, "120"));
		Order order = new Order("123", requested);

		List<Shipment> shipmentList = controller.processOrder(order);

		assertEquals(4, shipmentList.size());
		assertEquals(1, shipmentList.get(0).getShippedItems().size());
	}

	@Test
	public void testProcessOrder_CountOfShipments_MultipleProduct_Success() {
		controller.initCatalog(productInfoList);
		controller.initInventory(List.of( new InventoryLineItem(10, "2"), new InventoryLineItem(10, "3"),
				new InventoryLineItem(10, "120"), new InventoryLineItem(10, "1")));
		List<Requested> requested = new ArrayList<>();
		requested.add(new Requested(2, "2"));
		requested.add(new Requested(3, "1"));
		requested.add(new Requested(1, "3"));
		Order order = new Order("123", requested);

		List<Shipment> shipmentList = controller.processOrder(order);

		assertEquals(2, shipmentList.size());
	}

	@Test
	public void testProcessOrder_InventoryAfterOrder_MultipleProduct_Success() {
		controller.initCatalog(productInfoList);
		controller.initInventory(List.of( new InventoryLineItem(10, "2"), new InventoryLineItem(10, "3")
				, new InventoryLineItem(10, "1")));
		List<Requested> requested = new ArrayList<>();
		requested.add(new Requested(2, "2"));
		requested.add(new Requested(3, "1"));
		requested.add(new Requested(1, "3"));
		requested.add(new Requested(1, "120"));

		Order order = new Order("123", requested);

		controller.processOrder(order);
		Map<String, InventoryLineItem> actualInventory = controller.getInventory();

		assertEquals(8, actualInventory.get("2").getQuantity());
		assertEquals(0, actualInventory.get("120").getQuantity());
		assertEquals(9, actualInventory.get("3").getQuantity());
	}

}
