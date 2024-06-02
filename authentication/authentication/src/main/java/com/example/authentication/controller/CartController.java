package com.example.authentication.controller;

import com.example.authentication.model.Mobile;
import com.example.authentication.service.interfaces.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping(value = "/carts/user/{userId}")
    public ResponseEntity<Map<String, Object>> createCartItems(@PathVariable("userId") Long userId, @RequestBody Mobile mobile) throws Exception {
        return ResponseEntity.ok(cartService.addCartItems(userId, mobile));
    }
    @PatchMapping(value = "/carts/user/{userId}")
    public ResponseEntity<Map<String, Object>> updateCartItems(@PathVariable("userId") Long userId, @RequestBody Mobile mobile) throws Exception {
        return ResponseEntity.ok(cartService.updateCartItems(userId, mobile));
    }
}
