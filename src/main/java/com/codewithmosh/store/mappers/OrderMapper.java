package com.codewithmosh.store.mappers;

import com.codewithmosh.store.dtos.OrderDto;
import com.codewithmosh.store.dtos.OrderItemDto; // Import your Item DTO
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderItem; // Import your Item Entity
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    // Maps the main Order
    OrderDto toDto(Order order);

    // ADD THIS: Maps individual items inside the list
    OrderItemDto toOrderItemDto(OrderItem orderItem);
}