package com.example.authentication.service.implement;

import com.example.authentication.entity.OrderEntity;
import com.example.authentication.repository.OrderRepository;
import com.example.authentication.service.interfaces.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;

    private Map<String, Object> orderMap(OrderEntity orderEntity) {
        return new HashMap<>() {{
            put("orderID", orderEntity.getOrderID());
            put("orderDate", orderEntity.getOrderDate());
            put("orderStatus", orderEntity.getOrderStatus());
            put("totalAmount", orderEntity.getTotalAmount());
            put("transactionID", orderEntity.getTransaction().getTransactionId());
            put("userID", orderEntity.getUser().getUser_id());
        }};
    }
    @Override
    public List<Map<String, Object>> getAllOrders() throws Exception {
        try {
            List<Map<String,Object>> orderList = new ArrayList<>();
            List<OrderEntity> orderEntities = orderRepository.findAll();
            orderEntities.forEach((orderEntity) -> orderList.add(orderMap(orderEntity)));
            return orderList;
        } catch (Exception e) {
            throw new Exception("Could not get orders", e);
        }
    }

    @Override
    public List<Map<String, Object>> getAllOrdersByUserID(Long userID) throws Exception {
        try {
            List<Map<String,Object>> orderList = new ArrayList<>();
            List<OrderEntity> orderEntities = orderRepository.findAllOrdersByUserID(userID).isPresent()
                    ? orderRepository.findAllOrdersByUserID(userID).get() : null;
            assert orderEntities != null;
            orderEntities.forEach((orderEntity) -> orderList.add(orderMap(orderEntity)));
            return orderList;
        } catch (Exception e) {
            throw new Exception("Could not get orders", e);
        }
    }
}
