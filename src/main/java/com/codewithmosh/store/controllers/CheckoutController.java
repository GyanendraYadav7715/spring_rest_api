package com.codewithmosh.store.controllers;

import com.codewithmosh.store.dtos.CheckoutRequest;
import com.codewithmosh.store.dtos.CheckoutResponse;
import com.codewithmosh.store.dtos.ErrorDto;
import com.codewithmosh.store.entities.OrderStatus;
import com.codewithmosh.store.exceptions.CartEmptyException;
import com.codewithmosh.store.exceptions.CartNotFoundException;
import com.codewithmosh.store.exceptions.PaymentException;
import com.codewithmosh.store.repositories.OrderRepository;
import com.codewithmosh.store.services.CheckoutService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/checkout")
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;

    // ✅ FIXED
    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("stripe-signature") String signature,
            @RequestBody String payload
    ) {
        try {
            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);

            var stripeObject = event.getDataObjectDeserializer()
                    .getObject()
                    .orElse(null);

            switch (event.getType()) {

                case "payment_intent.succeeded" -> {
                    var paymentIntent = (PaymentIntent) stripeObject;

                    if (paymentIntent != null &&
                            paymentIntent.getMetadata() != null &&
                            paymentIntent.getMetadata().containsKey("order_id")) {

                        var orderId = paymentIntent.getMetadata().get("order_id");

                        var order = orderRepository.findById(Long.valueOf(orderId))
                                .orElseThrow(() -> new RuntimeException("Order not found"));

                        order.setStatus(OrderStatus.PAID);
                        orderRepository.save(order);
                    }
                }

                // ✅ FIXED EVENT NAME
                case "payment_intent.payment_failed" -> {
                    var paymentIntent = (PaymentIntent) stripeObject;

                    if (paymentIntent != null &&
                            paymentIntent.getMetadata() != null &&
                            paymentIntent.getMetadata().containsKey("order_id")) {

                        var orderId = paymentIntent.getMetadata().get("order_id");

                        var order = orderRepository.findById(Long.valueOf(orderId))
                                .orElseThrow(() -> new RuntimeException("Order not found"));

                        order.setStatus(OrderStatus.FAILED);
                        orderRepository.save(order);
                    }
                }
            }

            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Error creating a checkout session"));
    }

    @ExceptionHandler({CartNotFoundException.class, CartEmptyException.class})
    public ResponseEntity<ErrorDto> handleException(Exception ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorDto(ex.getMessage()));
    }
}