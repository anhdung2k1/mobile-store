package com.example.authentication.service.interfaces;

import com.example.authentication.model.Mobile;

import java.util.Map;

public interface CartService {
    Map<String, Object> getCartItems(Long userId) throws Exception;
    Map<String, Object> addCartItems(Long userId, Mobile mobile) throws Exception;
    Map<String, Object> updateCartItems(Long userId, Mobile mobile) throws Exception;
}
