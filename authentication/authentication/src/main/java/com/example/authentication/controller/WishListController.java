package com.example.authentication.controller;

import com.example.authentication.model.Mobile;
import com.example.authentication.service.interfaces.WishListService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class WishListController {
    private final WishListService wishListService;
    public WishListController(WishListService wishListService) {
        this.wishListService = wishListService;
    }
    @GetMapping(value = "/wishlist/user/{userId}")
    public ResponseEntity<Map<String, Object>> getWishListItemsByUserId(@PathVariable("userId") Long userId) throws Exception {
        return ResponseEntity.ok(wishListService.getWishListItems(userId));
    }
    @PostMapping(value = "/wishlist/user/{userId}")
    public ResponseEntity<Map<String, Object>> createWishListItems(@PathVariable("userId") Long userId, @RequestBody Mobile mobile) throws Exception {
        return ResponseEntity.ok(wishListService.addWishListItems(userId, mobile));
    }
    @PatchMapping(value = "/wishlist/user/{userId}")
    public ResponseEntity<Map<String, Object>> removeWishListItems(@PathVariable("userId") Long userId, @RequestBody Mobile mobile) throws Exception {
        return ResponseEntity.ok(wishListService.removeWishListItems(userId, mobile));
    }
}