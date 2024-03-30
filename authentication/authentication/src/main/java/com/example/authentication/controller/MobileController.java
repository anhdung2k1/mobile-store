package com.example.authentication.controller;

import com.example.authentication.model.Mobile;
import com.example.authentication.service.interfaces.MobileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MobileController {
    private final MobileService mobileService;
    public MobileController(MobileService mobileService) {
        this.mobileService = mobileService;
    }
    // Create new Mobile device
    @PostMapping(value = "/mobiles")
    public ResponseEntity<Mobile> createMobileDevice(@RequestBody Mobile mobile) throws Exception {
        return ResponseEntity.ok(mobileService.createMobile(mobile));
    }
    // Get all Mobiles by Mobile name
    @GetMapping(value = "/mobiles/products/query")
    public ResponseEntity<List<Map<String, Object>>> getAllMobilesByMobileName(@RequestParam("query") String mobileName) throws Exception {
        return ResponseEntity.ok(mobileService.getAllMobilesByName(mobileName));
    }
    // Get all Mobile by Mobile Type
    @GetMapping(value = "/mobiles/types/query")
    public ResponseEntity<List<Map<String, Object>>> getAllMobilesByMobileType(@RequestParam("query") String mobileType) throws Exception {
        return ResponseEntity.ok(mobileService.getAllMobilesByType(mobileType));
    }
    // Get Mobile by Mobile ID
    @GetMapping(value = "/mobiles/{mobileId}")
    public ResponseEntity<Map<String, Object>> getMobileByMobileId(@PathVariable("mobileId") Long mobileId) throws Exception {
        return ResponseEntity.ok(mobileService.getMobileById(mobileId));
    }
    // Update Mobile
    @PatchMapping(value = "/mobiles/{mobileId}")
    public ResponseEntity<Mobile> updateMobileDevice(@PathVariable("mobileId") Long mobileId, @RequestBody Mobile mobile) throws Exception{
        return ResponseEntity.ok(mobileService.updateMobileInformation(mobileId, mobile));
    }
    // Delete Mobile
    @DeleteMapping(value = "/mobiles/{mobileId}")
    public ResponseEntity<Map<String, Boolean>> deleteMobile(@PathVariable("mobileId") Long mobileId) throws Exception {
        return ResponseEntity.ok(new HashMap<>() {{
            put("deleted", mobileService.deleteMobile(mobileId));
        }});
    }
}
