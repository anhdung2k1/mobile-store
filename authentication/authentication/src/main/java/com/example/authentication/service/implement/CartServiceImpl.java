package com.example.authentication.service.implement;

import com.example.authentication.entity.CartEntity;
import com.example.authentication.entity.MobileEntity;
import com.example.authentication.entity.UserEntity;
import com.example.authentication.model.Mobile;
import com.example.authentication.repository.CartRepository;
import com.example.authentication.repository.MobileRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.service.interfaces.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final MobileRepository mobileRepository;
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

    @Override
    public Map<String, Object> addCartItems(Long userId, Mobile mobile) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(userId).get();
            CartEntity cartEntity;
            if (cartRepository.getCartEntityWithUserId(userId).isPresent()) {
                // If the user already have the cart, use the current
                cartEntity = cartRepository.getCartEntityWithUserId(userId).get();
            } else {
                // If the user did not have create the cart create one in DB
                cartEntity = new CartEntity(userEntity);
            }
            if (mobile != null) {
                List<MobileEntity> cartMobiles = cartEntity.getMobiles();
                if (mobileRepository.findById(mobile.getMobileID()).isPresent()) {
                    MobileEntity mobileAddEntity = mobileRepository.findById(mobile.getMobileID()).get();
                    // Add the mobile in the list
                    cartMobiles.add(mobileAddEntity);
                    // Update the carts
                    cartEntity.setMobiles(cartMobiles);
                    cartRepository.save(cartEntity);
                }
                else {
                    log.warn("The mobile ID given is not present: " + mobile.getMobileID());
                }
            }
            return cartMap(cartEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get cart items " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> updateCartItems(Long userId, Mobile mobile) throws Exception {
        try {
            CartEntity cartEntity = cartRepository.getCartEntityWithUserId(userId).isPresent()
                    ? cartRepository.getCartEntityWithUserId(userId).get() : null;
            assert cartEntity != null;
            if (mobile != null) {
                List<MobileEntity> cartMobiles = cartEntity.getMobiles();
                if (mobileRepository.findById(mobile.getMobileID()).isPresent()) {
                    MobileEntity mobileRemoveEntity = mobileRepository.findById(mobile.getMobileID()).get();
                    // Remove the mobile in the list
                    cartMobiles.remove(mobileRemoveEntity);
                    // Update the carts again
                    cartEntity.setMobiles(cartMobiles);
                    cartRepository.save(cartEntity);
                }
                else {
                    log.warn("The mobile ID given is not present: " + mobile.getMobileID());
                }
            }
            else {
                log.info("There is no items to remove");
            }
            return cartMap(cartEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get cart items " + e.getMessage());
        }
    }
}
