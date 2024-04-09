package com.example.authentication.service.implement;

import com.example.authentication.entity.MobileEntity;
import com.example.authentication.model.Mobile;
import com.example.authentication.repository.MobileRepository;
import com.example.authentication.service.interfaces.MobileService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
public class MobileServiceImpl implements MobileService {

    private final MobileRepository mobileRepository;

    private HashMap<String, Object> mobileMap(MobileEntity mobileEntity) {
        return new HashMap<>() {{
            put("mobileID", mobileEntity.getMobileID());
            put("mobileName", mobileEntity.getMobileName());
            put("mobileModel", mobileEntity.getMobileModel());
            put("mobileType", mobileEntity.getMobileType());
            put("mobileQuantity", mobileEntity.getMobileQuantity());
            put("mobilePrice", mobileEntity.getMobilePrice());
            put("mobileDescription", mobileEntity.getMobileDescription());
        }};
    }
    @Override
    public Boolean createMobile(Mobile mobile) throws Exception {
        try {
            MobileEntity mobileEntity = new MobileEntity();
            // Copy all the properties into mobile Entity and save to repository
            mobile.setCreateAt(LocalDateTime.now());
            mobile.setUpdateAt(LocalDateTime.now());
            BeanUtils.copyProperties(mobile, mobileEntity);
            mobileRepository.save(mobileEntity);
            return true;
        } catch(Exception e) {
            throw new Exception("Could not create Mobile" + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllMobilesByName(String mobileName) throws Exception {
        try {
            List<Map<String, Object>> mobilesMapList = new ArrayList<>();
            List<MobileEntity> mobileEntities = mobileRepository.findAllByMobileNameContains(mobileName).isPresent()
                    ? mobileRepository.findAllByMobileNameContains(mobileName).get() : null;
            assert mobileEntities != null;
            mobileEntities.forEach((mobileEntity
                    -> mobilesMapList.add(mobileMap(mobileEntity))));
            return mobilesMapList;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not retrieve all mobiles by mobile Name: " + mobileName + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllMobilesByType(String mobileType) throws Exception {
        try {
            List<Map<String, Object>> mobilesMapList = new ArrayList<>();
            List<MobileEntity> mobileEntities = mobileRepository.findAllByMobileTypeContains(mobileType).isPresent()
                    ? mobileRepository.findAllByMobileTypeContains(mobileType).get() : null;
            assert mobileEntities != null;
            mobileEntities.forEach((mobileEntity
                    -> mobilesMapList.add(mobileMap(mobileEntity))));
            return mobilesMapList;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not retrieve all mobiles by mobile Type: " + mobileType + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getMobileById(Long mobileId) throws Exception {
        try {
            MobileEntity mobileEntity = mobileRepository.findById(mobileId).isPresent()
                    ? mobileRepository.findById(mobileId).get() : null;
            assert mobileEntity != null;
            return mobileMap(mobileEntity);
        } catch (NoSuchElementException e) {
            throw new Exception("Mobile is not found " + e.getMessage());
        }
    }

    @Override
    public Mobile updateMobileInformation(Long mobileId, Mobile mobile) throws Exception {
        try {
            MobileEntity mobileEntity = mobileRepository.findById(mobileId).isPresent()
                    ? mobileRepository.findById(mobileId).get() : null;
            assert mobileEntity != null;
            mobileEntity.setMobileName(mobile.getMobileName());
            mobileEntity.setMobileModel(mobile.getMobileModel());
            mobileEntity.setMobileType(mobile.getMobileType());
            mobileEntity.setMobileQuantity(mobile.getMobileQuantity());
            mobileEntity.setMobilePrice(mobile.getMobilePrice());
            mobileEntity.setMobileDescription(mobile.getMobileDescription());
            mobileEntity.setUpdateAt(LocalDateTime.now());
            mobileRepository.save(mobileEntity);
            BeanUtils.copyProperties(mobileEntity, mobile);
            return mobile;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not found Mobile Device " + e.getMessage());
        }
    }

    @Override
    public Boolean deleteMobile(Long mobileId) throws Exception {
        try {
            if (mobileRepository.findById(mobileId).isPresent()) {
                mobileRepository.delete(mobileRepository.findById(mobileId).get());
                return true;
            }
            return false;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not found Mobile Device " + e.getMessage());
        }
    }
}
