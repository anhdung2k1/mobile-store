// Copyright [2024] <Anh Dung>
#pragma once

#include<string>
#include "Payment.h"
#include <memory>

using namespace std;

class Transaction {
    private:
        int transactionId;
        string transactionName;
        string transactionType;
        unique_ptr<Payment> payment;
    public:
        Transaction();
        Transaction(string transactionName, string transactionType);
        Transaction(string transactionName, string transactionType, unique_ptr<Payment> payment);
        int getTransactionId();
        string getTransactionName();
        string getTransactionType();
        unique_ptr<Payment> getPayment();
        void setTransactionName(string transactionName);
        void setTransactionType(string transactionType);
        void setPayment(unique_ptr<Payment> payment);
};