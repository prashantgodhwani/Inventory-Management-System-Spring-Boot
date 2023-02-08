package com.zipline.inventorymanagement.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@AllArgsConstructor
@Getter
@ToString
public class Order {

    @JsonProperty("order_id")
    @NotNull
    @NotBlank
    private String orderId;

    @JsonProperty("requested")
    @NotNull
    private @Valid List<Requested> requested;
}
