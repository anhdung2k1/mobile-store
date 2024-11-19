package com.example.authentication.controller;

import com.example.authentication.model.Rating;
import com.example.authentication.service.interfaces.RatingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class RatingControlller {
    private final RatingService ratingService;
    public RatingControlller(RatingService ratingService) {
        this.ratingService = ratingService;
    }
    @PostMapping(value = "/ratings")
    public ResponseEntity<Boolean> createRating(@RequestBody Rating rating) throws Exception {
        return ResponseEntity.ok(ratingService.createRating(rating));
    }
    // Get rating with Mobile ID
    @GetMapping(value = "/ratings/mobiles/{mobileID}")
    public ResponseEntity<Map<String, Object>> getRatingWithMobileID(@PathVariable("mobileID") Long mobileID) throws Exception {
        return ResponseEntity.ok(ratingService.getRatingsWithMobileID(mobileID));
    }
    // Update rating with Mobile ID
    @PatchMapping(value = "/ratings/mobiles/{mobileID}")
    public ResponseEntity<Boolean> updateRatingWithMobileID(@PathVariable("mobileID") Long mobileID, @RequestBody Rating rating) throws Exception {
        return ResponseEntity.ok(ratingService.updateRatingWithMobileID(rating, mobileID));
    }
}
