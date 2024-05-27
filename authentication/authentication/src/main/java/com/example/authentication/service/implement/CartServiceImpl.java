package com.example.authentication.service.implement;

import com.example.authentication.entity.CartEntity;
import com.example.authentication.repository.CartRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.interfaces.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    private Map<String, Object> cartMap(CartEntity cartEntity) {
        return new HashMap<>() {{
            put("cartID", cartEntity.getCartId());
            put("mobiles", cartEntity.getMobiles());
            put("userId", cartEntity.getUser().getUser_id());
            put("date", cartEntity.getCartDate());
        }};
    }

    @Override
    public Map<String, Object> getCartItems(Long userId) throws Exception {
        try {
            CartEntity cartEntity = cartRepository.getCartEntityWithUserId(userId).isPresent()
                    ? cartRepository.getCartEntityWithUserId(userId).get() : null;
            if (userRepository.findById(userId).isPresent()) {
                cartEntity.setUser(userRepository.findById(userId).get());
            }
            assert cartEntity != null;
            return cartMap(cartEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get cart items" + e.getMessage());
        }
    }
}
