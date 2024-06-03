package com.example.authentication.service.implement;

import com.example.authentication.entity.MobileEntity;
import com.example.authentication.entity.UserEntity;
import com.example.authentication.entity.WishListEntity;
import com.example.authentication.model.Mobile;
import com.example.authentication.repository.MobileRepository;
import com.example.authentication.repository.UserRepository;
import com.example.authentication.repository.WishListRepository;
import com.example.authentication.service.interfaces.WishListService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class WishListServiceImpl implements WishListService {

    private final WishListRepository wishListRepository;
    private final MobileRepository mobileRepository;
    private final UserRepository userRepository;

    private Map<String, Object> wishListMap(WishListEntity wishListEntity) {
        return new HashMap<>() {{
            put("wishListID", wishListEntity.getWishListId());
            put("mobiles", wishListEntity.getMobiles());
            put("userId", wishListEntity.getUser().getUser_id());
        }};
    }

    @Override
    public Map<String, Object> getWishListItems(Long userId) throws Exception {
        try {
            WishListEntity wishListEntity = wishListRepository.getWishListEntityWithUserId(userId).isPresent()
                    ? wishListRepository.getWishListEntityWithUserId(userId).get() : null;
            if (userRepository.findById(userId).isPresent()) {
                wishListEntity.setUser(userRepository.findById(userId).get());
            }
            assert wishListEntity != null;
            return wishListMap(wishListEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get wish list items" + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> addWishListItems(Long userId, Mobile mobile) throws Exception {
        try {
            UserEntity userEntity = userRepository.findById(userId).get();
            WishListEntity wishListEntity;
            if (wishListRepository.getWishListEntityWithUserId(userId).isPresent()) {
                // If the user already have the cart, use the current
                wishListEntity = wishListRepository.getWishListEntityWithUserId(userId).get();
            } else {
                // If the user did not have create the cart create one in DB
                wishListEntity = new WishListEntity(userEntity);
            }
            if (mobile != null) {
                List<MobileEntity> wishListMobiles = wishListEntity.getMobiles();
                if (wishListMobiles == null) {
                    wishListMobiles = new ArrayList<>();
                }
                if (mobileRepository.findById(mobile.getMobileID()).isPresent()) {
                    MobileEntity mobileAddEntity = mobileRepository.findById(mobile.getMobileID()).get();

                    if (wishListMobiles != null) {
                        // Update the quantity if the item has been created
                        int index = wishListMobiles.indexOf(mobileAddEntity);
                        if (index != -1) {
                            log.error("Current item already appended in the wishlist items");
                        }
                        else {
                            log.info("The mobile did not exists, add to wishlist item");
                            wishListMobiles.add(mobileAddEntity);
                        }
                    }
                    else {
                        // Add the mobile in the list if it did not present
                        log.info("Wish List mobile is current empty, create new item");
                        wishListMobiles.add(mobileAddEntity);
                    }
                    // Update the carts
                    wishListEntity.setMobiles(wishListMobiles);
                    wishListRepository.save(wishListEntity);
                }
                else {
                    log.warn("The mobile ID given is not present: " + mobile.getMobileID());
                }
            }
            return wishListMap(wishListEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get cart items " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> removeWishListItems(Long userId, Mobile mobile) throws Exception {
        try {
            WishListEntity wishListEntity = wishListRepository.getWishListEntityWithUserId(userId).isPresent()
                    ? wishListRepository.getWishListEntityWithUserId(userId).get() : null;
            assert wishListEntity != null;
            if (mobile != null) {
                List<MobileEntity> wishListMobiles = wishListEntity.getMobiles();
                if (mobileRepository.findById(mobile.getMobileID()).isPresent()) {
                    MobileEntity mobileRemoveEntity = mobileRepository.findById(mobile.getMobileID()).get();
                    // Remove the mobile in the list
                    wishListMobiles.remove(mobileRemoveEntity);
                    // Update the carts again
                    wishListEntity.setMobiles(wishListMobiles);
                    wishListRepository.save(wishListEntity);
                }
                else {
                    log.warn("The mobile ID given is not present: " + mobile.getMobileID());
                }
            }
            else {
                log.info("There is no items to remove");
            }
            return wishListMap(wishListEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get wish list items " + e.getMessage());
        }
    }
}
