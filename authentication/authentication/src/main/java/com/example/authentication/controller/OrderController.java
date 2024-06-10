package com.example.authentication.controller;

import com.example.authentication.service.interfaces.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
