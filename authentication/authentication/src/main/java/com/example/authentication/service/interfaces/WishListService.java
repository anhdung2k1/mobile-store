package com.example.authentication.service.interfaces;

import com.example.authentication.model.Mobile;

import java.util.Map;

public interface WishListService {
    Map<String, Object> getWishListItems(Long userId) throws Exception;
    Map<String, Object> addWishListItems(Long userId, Mobile mobile) throws Exception;
    Map<String, Object> removeWishListItems(Long userId, Mobile mobile) throws Exception;
}