#include "Transaction.h"

Transaction::Transaction() : transactionName(""), transactionType("") {}

Transaction::Transaction(int transactionId, string transactionName, string transactionType) 
    : transactionId(transactionId) ,transactionName(transactionName), transactionType(transactionType) {}

Transaction::Transaction(string transactionName, string transactionType, Payment payment) : transactionName(transactionName), transactionType(transactionType), payment(payment) {}

int Transaction::getTransactionId() {
    return transactionId;
}

string Transaction::getTransactionName() {
    return transactionName;
}

string Transaction::getTransactionType() {
    return transactionType;
}

Payment Transaction::getPayment() {
    return payment;
}

void Transaction::setTransactionName(string transactionName) {
    this->transactionName = transactionName;
}

void Transaction::setTransactionType(string transactionType) {
    this->transactionType = transactionType;
}

void Transaction::setPayment(Payment payment) {
    this->payment = payment;
}

