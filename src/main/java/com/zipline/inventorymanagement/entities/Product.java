package com.zipline.inventorymanagement.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.*;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class Product {

    @JsonProperty("mass_g")
    @Min(0)
    @NotNull
    @Max(1800)
    private int massG;

    @JsonProperty("product_name")
    @NotBlank
    private String productName;

    @JsonProperty("product_id")
    @NotNull
    @Min(0)
    private int productId;
}
