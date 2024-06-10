package com.example.authentication.service.interfaces;

import java.util.List;
import java.util.Map;

public interface OrderService {
    // This use in ADMIN role
    List<Map<String, Object>> getAllOrders() throws Exception;
    // This use in USER role
    List<Map<String, Object>> getAllOrdersByUserID(Long userID) throws Exception;
}
