package com.example.authentication.service.implement;

import com.example.authentication.entity.RatingEntity;
import com.example.authentication.model.Rating;
import com.example.authentication.repository.RatingRepository;
import com.example.authentication.service.interfaces.RatingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class RatingServiceImpl implements RatingService {

    private final RatingRepository ratingRepository;

    private Map<String, Object> ratingMap(RatingEntity ratingEntity) {
        return new HashMap<>() {{
            put("ratingID", ratingEntity.getRatingId());
            put("count", ratingEntity.getCount());
            put("rate", ratingEntity.getRate());
        }};
    }

    @Override
    public Boolean createRating(Rating rating) throws Exception {
        try {
            RatingEntity ratingEntity = new RatingEntity();
            BeanUtils.copyProperties(rating, ratingEntity);
            ratingRepository.save(ratingEntity);
            return null;
        } catch (Exception e) {
            throw new Exception("Could not create new Rating", e);
        }
    }

    @Override
    public Map<String, Object> getRatingsWithMobileID(Long mobileID) throws Exception {
        try{
            RatingEntity ratingEntity = ratingRepository.findRatingEntityWithMobileID(mobileID).isPresent()
                    ? ratingRepository.findRatingEntityWithMobileID(mobileID).get() : null;
            assert ratingEntity != null;
            return ratingMap(ratingEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Could not get any ratings", e);
        }
    }

    @Override
    public Boolean updateRatingWithMobileID(Rating rating, Long mobileID) throws Exception {
        try {
            RatingEntity ratingEntity = ratingRepository.findRatingEntityWithMobileID(mobileID).isPresent()
                    ? ratingRepository.findRatingEntityWithMobileID(mobileID).get() : null;
            assert ratingEntity != null;
            ratingEntity.setRate(rating.getRate());
            ratingEntity.setCount(rating.getCount());
            ratingRepository.save(ratingEntity);
            return true;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not find specific mobile", e);
        }
    }
}
