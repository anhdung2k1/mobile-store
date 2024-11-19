package com.example.authentication.controller;

import com.example.authentication.service.interfaces.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }
    @GetMapping(value = "/orders")
    public ResponseEntity<List<Map<String, Object>>> getAllOrders() throws Exception {
        return ResponseEntity.ok(orderService.getAllOrders());
    }
    @GetMapping(value = "/orders/{userID}")
    public ResponseEntity<List<Map<String, Object>>> getAllOrdersByUserID(@PathVariable("userID") Long userID) throws Exception {
        return ResponseEntity.ok(orderService.getAllOrdersByUserID(userID));
    }
    @GetMapping(value = "/orders/order/{orderID}")
    public ResponseEntity<Map<String, Object>> getOrderByOrderID(@PathVariable("orderID") Long orderID) throws Exception {
        return ResponseEntity.ok(orderService.getOrderByID(orderID));
    }
    @PatchMapping(value = "/orders/order/{orderID}")
    public ResponseEntity<Map<String, Object>> updateOrder(@PathVariable("orderID") Long orderID, @RequestBody String orderStatus) throws Exception {
        return ResponseEntity.ok(orderService.updateOrder(orderID, orderStatus));
    }
    @DeleteMapping(value = "/orders/order/{orderID}")
    public ResponseEntity<Map<String, Boolean>> deleteOrder(@PathVariable("orderID") Long orderID) throws Exception {
        return ResponseEntity.ok(new HashMap<>() {{
            put("deleted", orderService.deleteOrder(orderID));
        }});
    }
}
