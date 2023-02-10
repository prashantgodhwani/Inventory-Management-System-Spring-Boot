package com.zipline.inventorymanagement.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class Shipment {

    @NotBlank
    @NotNull
    @NotBlank
    private String orderId;

    @NotNull
    private List<ShippedItem> shippedItems;
}
