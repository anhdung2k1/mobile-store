package com.example.authentication.controller;

import com.example.authentication.model.Transactions;
import com.example.authentication.service.interfaces.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TransactionController {
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    // Create new transaction
    @PostMapping(value="/transactions/{userId}")
    public ResponseEntity<Boolean> createTransaction(@RequestBody Transactions transactions, @PathVariable("userId") Long userId) throws Exception {
        return ResponseEntity.ok(transactionService.createTransaction(transactions, userId));
    }
    // Get all Transactions by PayID
    @GetMapping(value = "/transactions/query")
    public ResponseEntity<List<Map<String, Object>>> getAllTransactions(@RequestParam("query") String transactionName) throws Exception {
        return ResponseEntity.ok(transactionService.getAllTransactionsByName(transactionName));
    }
    // Get all Transactions by userId
    @GetMapping(value = "/transactions/user/{userId}")
    public ResponseEntity<List<Map<String, Object>>> userId(@PathVariable("userId") Long userId) throws Exception {
        return ResponseEntity.ok(transactionService.getAllTransactionByUserId(userId));
    }
    // Get Transaction Details
    @GetMapping(value = "/transactions/{transactionId}")
    public ResponseEntity<Map<String, Object>> getTransactionByTransactionId(@PathVariable("transactionId") Long transactionId) throws Exception {
        return ResponseEntity.ok(transactionService.getTransactionByTransactionId(transactionId));
    }
    // Update Transaction
    @PatchMapping(value = "/transactions/{transactionId}")
    public ResponseEntity<Transactions> updateTransaction(@PathVariable("transactionId") Long transactionId, @RequestBody Transactions transactions) throws Exception {
        return ResponseEntity.ok(transactionService.updateTransaction(transactionId, transactions));
    }
    // Delete Transaction
    @DeleteMapping(value = "/transactions/{transactionId}")
    public ResponseEntity<Map<String, Boolean>> deleteTransaction(@PathVariable("transactionId") Long transactionId) throws Exception {
        return ResponseEntity.ok(new HashMap<>() {{
            put("deleted", transactionService.deleteTransaction(transactionId));
        }});
    }
}
