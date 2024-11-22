package com.example.authentication.service.implement;

import com.amazonaws.services.s3.AmazonS3;
import com.example.authentication.entity.MobileEntity;
import com.example.authentication.entity.RatingEntity;
import com.example.authentication.model.Mobile;
import com.example.authentication.repository.MobileRepository;
import com.example.authentication.repository.RatingRepository;
import com.example.authentication.service.interfaces.MobileService;
import com.example.authentication.utils.S3Utils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(rollbackOn = Exception.class)
@RequiredArgsConstructor
@Slf4j
public class MobileServiceImpl implements MobileService {

    private final MobileRepository mobileRepository;
    private final RatingRepository ratingRepository;

    private final S3Utils s3Utils;

    @Value("${bucket.name}")
    public String bucketName;
    @Autowired
    public AmazonS3 s3Client;

    private HashMap<String, Object> mobileMap(MobileEntity mobileEntity) {
        return new HashMap<>() {{
            put("mobileID", mobileEntity.getMobileID());
            put("mobileName", mobileEntity.getMobileName());
            put("mobileModel", mobileEntity.getMobileModel());
            put("mobileType", mobileEntity.getMobileType());
            put("mobileQuantity", mobileEntity.getMobileQuantity());
            put("mobilePrice", mobileEntity.getMobilePrice());
            put("mobileDescription", mobileEntity.getMobileDescription());
            put("imageUrl", mobileEntity.getImageUrl() != null ? mobileEntity.getImageUrl() : "");
            put("rating", mobileEntity.getRate());
        }};
    }
    @Override
    public Boolean createMobile(Mobile mobile) throws Exception {
        try {
            MobileEntity mobileEntity = new MobileEntity();
            // Copy all the properties into mobile Entity and save to repository
            mobile.setCreateAt(LocalDateTime.now());
            mobile.setUpdateAt(LocalDateTime.now());
            RatingEntity ratingEntity = new RatingEntity();
            ratingRepository.save(ratingEntity);
            mobile.setRate(ratingEntity);
            if (mobile.getImageUrl() != null) {
                if (!mobile.getImageUrl().isEmpty()) {
                    // Save to S3 Bucket
                    URL objectURL = s3Utils.getS3URL(mobile.getImageUrl());
                    mobile.setImageUrl(objectURL.toString());
                }
            }

            BeanUtils.copyProperties(mobile, mobileEntity);
            mobileRepository.save(mobileEntity);
            return true;
        } catch(Exception e) {
            throw new Exception("Could not create Mobile" + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> getAllMobiles() throws Exception {
        try {
            List<MobileEntity> mobilesEntities = mobileRepository.findAllMobiles();
            List<Map<String, Object>> listMobiles = new ArrayList<>();
            mobilesEntities.forEach((mobileEntity -> listMobiles.add(mobileMap(mobileEntity))));
            return listMobiles;
        } catch (NoSuchElementException e) {
            throw new Exception("No such devices found", e);
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
    public List<String> getMobileCategories() throws Exception {
        try {
            return mobileRepository.findMobileCategories().isPresent() ? mobileRepository.findMobileCategories().get() : null;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not retrieve any categories");
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
    public Boolean updateMobileInformation(Long mobileId, Mobile mobile) throws Exception {
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
            if (mobile.getImageUrl() != null) {
                if (!mobile.getImageUrl().isEmpty()) {
                    URL objectURL = s3Utils.getS3URL(mobile.getImageUrl());
                    mobileEntity.setImageUrl(objectURL.toString());
                }
            }
            mobileRepository.save(mobileEntity);
            BeanUtils.copyProperties(mobileEntity, mobile);
            return true;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not found Mobile Device " + e.getMessage());
        }
    }

    @Override
    public Boolean deleteMobile(Long mobileId) throws Exception {
        try {
            if (mobileRepository.findById(mobileId).isPresent()) {
                if (mobileRepository.findById(mobileId).get().getImageUrl() != null) {
                    if (!mobileRepository.findById(mobileId).get().getImageUrl().isEmpty()) {
                        // Delete image in S3 Bucket
                        String fileURI = mobileRepository.findById(mobileId).get().getImageUrl();
                        String [] fileURISplitted = fileURI.split("/");
                        log.info("fileURISplitted: {}", (Object) fileURISplitted);
                        String fileName = fileURISplitted[fileURISplitted.length-1];
                        log.info("FileName: {}", fileName);
                        s3Client.deleteObject(bucketName, fileName);
                        log.info("FileName: {} removed", fileName);
                    }
                }

                mobileRepository.delete(mobileRepository.findById(mobileId).get());
                return true;
            }
            return false;
        } catch (NoSuchElementException e) {
            throw new Exception("Could not found Mobile Device " + e.getMessage());
        }
    }
}
