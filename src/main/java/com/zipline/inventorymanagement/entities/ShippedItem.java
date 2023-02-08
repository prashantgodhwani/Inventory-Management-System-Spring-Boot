package com.zipline.inventorymanagement.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ShippedItem {

    @JsonProperty("quantity")
    @NotNull
    @Min(1)
    @Max(10000)
    private int quantity;

    @JsonProperty("product_id")
    @NotNull
    @NotBlank
    private String productId;
}
