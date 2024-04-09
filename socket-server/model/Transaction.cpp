#include "Transaction.h"

Transaction::Transaction() : transactionName(""), transactionType(""), paymentMethod("Cash") {}
int Transaction::getTransactionId() {
    return transactionId;
}

string Transaction::getTransactionName() {
    return transactionName;
}

string Transaction::getTransactionType() {
    return transactionType;
}

string Transaction::getPaymentMethod() {
    return paymentMethod;
}

void Transaction::setTransactionId(int transactionId) {
    this->transactionId = transactionId;
}

void Transaction::setTransactionName(string transactionName) {
    this->transactionName = transactionName;
}

void Transaction::setTransactionType(string transactionType) {
    this->transactionType = transactionType;
}

void Transaction::setPaymentMethod(string paymentMethod) {
    this->paymentMethod = paymentMethod;
}

