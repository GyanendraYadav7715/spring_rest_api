package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.dtos.CheckoutResponse;
import com.codewithmosh.store.dtos.OrderRepository;
import com.codewithmosh.store.entities.Order;
import com.codewithmosh.store.entities.OrderItem;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.repositories.CartRepository;
import com.codewithmosh.store.services.CartService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CartRepository cartRepository;
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request ){
     var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
     if(cart == null){
         return ResponseEntity.badRequest().body(
                 Map.of("error","cart not found")
         );
     }
     if(cart.getCartItems().isEmpty()){
         return ResponseEntity.badRequest().body(
                 Map.of("error","cart is empty.")
         );
     }
     var order = new Order();

     order.setTotalPrice(cart.getTotalPrice());
     order.setStatus(OrderStatus.PENDING);
     order.setCustomer(authService.getcurrentUser());

     cart.getCartItems().forEach(item-> {

         var orderItems = new OrderItem();
         orderItems.setOrder(order);
         orderItems.setProduct(item.getProduct());
         orderItems.setQuantity(item.getQuantity());
         orderItems.setUnitPrice(item.getProduct().getPrice());
         orderItems.setTotalPrice(item.getTotalPrice());
         order.getItmes().add(orderItems);
     });

     orderRepository.save(order);
     cartService.clearCart(cart.getId());
     return ResponseEntity.ok(new CheckoutResponse(order.getId()));
    }
}
