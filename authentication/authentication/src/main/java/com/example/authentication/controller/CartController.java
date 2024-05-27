package com.example.authentication.controller;

import com.example.authentication.service.interfaces.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class CartController {
    private final CartService cartService;
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    @GetMapping(value = "/carts/user/{userId}")
    public ResponseEntity<Map<String, Object>> getCartItemsByUserId(@PathVariable("userId") Long userId) throws Exception {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }
}
