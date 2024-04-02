// Copyright [2024] <Anh Dung>
#pragma once

#include "Payment.h"

using namespace std;

class Transaction {
    private:
        int transactionId;
        string transactionName;
        string transactionType;
        Payment payment;
    public:
        Transaction();
        Transaction(int transactionId, string transactionName, string transactionType);
        Transaction(string transactionName, string transactionType, Payment payment);
        int getTransactionId();
        string getTransactionName();
        string getTransactionType();
        Payment getPayment();
        void setTransactionName(string transactionName);
        void setTransactionType(string transactionType);
        void setPayment(Payment payment);
};