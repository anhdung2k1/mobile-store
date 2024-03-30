package com.example.authentication.controller;

import com.example.authentication.model.Customers;
import com.example.authentication.service.interfaces.CustomerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CustomerController {
    private final CustomerService customerService;
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }
    // Create new Customer
    @PostMapping(value = "/customers")
    public ResponseEntity<Customers> createCustomer(@RequestBody Customers customers) throws Exception {
        return ResponseEntity.ok(customerService.createCustomer(customers));
    }
    // Get all customers
    @GetMapping(value = "/customers")
    public ResponseEntity<List<Map<String, Object>>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
    // Get all customers attached with mobile
    @GetMapping(value = "/customers/mobile/{mobileId}")
    public ResponseEntity<List<Map<String, Object>>> getAllCustomersWithMobile(@PathVariable("mobileId") Long mobileId) throws Exception{
        return ResponseEntity.ok(customerService.getAllCustomersWithMobileId(mobileId));
    }
    // Get all customers with customer Name
    @GetMapping(value = "/customers/query")
    public ResponseEntity<List<Map<String, Object>>> getAllCustomersWithName(@RequestParam("query") String customerName) throws Exception {
        return ResponseEntity.ok(customerService.getAllCustomersWithName(customerName));
    }
    // Get Customer by customer ID
    @GetMapping(value = "/customers/{customerId}")
    public ResponseEntity<Map<String, Object>> getCustomerByCustomerId(@PathVariable("customerId") Long customerId) throws Exception {
        return ResponseEntity.ok(customerService.getCustomerByCustomerId(customerId));
    }
    // Update Customer Information
    @PatchMapping(value = "/customers/{customerId}")
    public ResponseEntity<Customers> updateCustomerInformation(@PathVariable("customerId") Long customerId, @RequestBody Customers customers) throws Exception {
        return ResponseEntity.ok(customerService.updateCustomerInformation(customerId, customers));
    }
    // Delete Customer
    @DeleteMapping(value = "/customers/{customerId}")
    public ResponseEntity<Map<String, Boolean>> deleteCustomer(@PathVariable("customerId") Long customerId) throws Exception {
        return ResponseEntity.ok(new HashMap<>() {{
            put("deleted", customerService.deleteCustomer(customerId));
        }});
    }
}
