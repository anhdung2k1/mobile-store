package com.example.authentication.service.interfaces;

import java.util.Map ;

public interface CartService {
    Map<String, Object> getCartItems(Long userId) throws Exception;
}
