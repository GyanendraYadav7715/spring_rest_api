package com.codewithmosh.store.services;

import com.codewithmosh.store.controllers.AuthService;
import com.codewithmosh.store.dtos.OrderDto;
import com.codewithmosh.store.mappers.OrderMapper;
import com.codewithmosh.store.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class OrderService {

    private  final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public List<OrderDto> getAllOrders(){
        var user=authService.getcurrentUser();
        var order = orderRepository.getAllByCustomer(user);
        return order.stream().map(orderMapper::toDto).toList();
    }
}
