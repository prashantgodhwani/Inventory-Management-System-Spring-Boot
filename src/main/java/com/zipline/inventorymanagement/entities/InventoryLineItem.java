package com.zipline.inventorymanagement.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Builder
@AllArgsConstructor
@ToString
public class InventoryLineItem {

    @JsonProperty("quantity")
    @Min(1)
    @NotNull
    @Max(10000)
    private int quantity;

    @JsonProperty("product_id")
    @NotNull
    @Min(0)
    private int productId;
}
