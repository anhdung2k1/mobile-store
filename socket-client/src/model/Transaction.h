// Copyright [2024] <Anh Dung>
#pragma once

#include "Payment.h"

using namespace std;

class Transaction {
    private:
        int transactionId;
        string transactionName;
        string transactionType;
        string paymentMethod;
    public:
        Transaction();
        Transaction(int transactionId, string transactionName, string transactionType, string paymentMethod);
        int getTransactionId();
        string getTransactionName();
        string getTransactionType();
        string getPaymentMethod();
        void setTransactionId(int transactionId);
        void setTransactionName(string transactionName);
        void setTransactionType(string transactionType);
        void setPaymentMethod(string paymentMethod);
};