package com.codewithmosh.store.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemsToCartRequest {
    @NotNull(message = "ProductId  is not blank")
    private Long productId;
}
