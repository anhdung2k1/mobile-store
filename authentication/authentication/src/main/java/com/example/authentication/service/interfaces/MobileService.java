package com.example.authentication.service.interfaces;

import com.example.authentication.model.Mobile;

import java.util.List;
import java.util.Map;

public interface MobileService {
    Boolean createMobile(Mobile mobile) throws Exception;
    List<Map<String, Object>> getAllMobiles() throws Exception;
    List<Map<String, Object>> getAllMobilesByName(String mobileName) throws Exception;
    List<Map<String, Object>> getAllMobilesByType(String mobileType) throws Exception;
    List<String> getMobileCategories() throws Exception;
    Map<String, Object> getMobileById(Long mobileId) throws Exception;
    Boolean updateMobileInformation(Long mobileId, Mobile mobile) throws Exception;
    Boolean deleteMobile(Long mobileId) throws Exception;
}
