package com.example.authentication.repository;

import com.example.authentication.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    @Query(value = "select cus.* from customers cus" +
            "where cus.mob_id =:mobileId",
            nativeQuery = true)
    Optional<List<CustomerEntity>> findAllCustomersByMobileId(Long mobileId);

    @Query(value = "select cus.* from customers cus" +
            "where cus.cus_name =:customerName", nativeQuery = true)
    Optional<List<CustomerEntity>> findAllCustomersByCustomerName(String customerName);
}
