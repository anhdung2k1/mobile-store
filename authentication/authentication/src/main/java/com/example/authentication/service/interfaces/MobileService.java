package com.example.authentication.service.interfaces;

import com.example.authentication.model.Mobile;

import java.util.List;
import java.util.Map;

public interface MobileService {
    Mobile createMobile(Mobile mobile) throws Exception;
    List<Map<String, Object>> getAllMobilesByName(String mobileName);
    List<Map<String, Object>> getAllMobilesByType(String mobileType);
    Map<String, Object> getMobileById(Long mobileId) throws Exception;
    Mobile updateMobileInformation(Long mobileId, Mobile mobile) throws Exception;
    Boolean deleteMobile(Long mobileId) throws Exception;
}
