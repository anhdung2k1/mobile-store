#include "Transaction.h"

Transaction::Transaction() : transactionName(""), transactionType(""), payment(make_unique<Payment>()) {}

Transaction::Transaction(string transactionName, string transactionType) : transactionName(transactionName), transactionType(transactionType), payment(make_unique<Payment>()) {}

Transaction::Transaction(string transactionName, string transactionType, unique_ptr<Payment> payment) : transactionName(transactionName), transactionType(transactionType), payment(move(payment)) {}

int Transaction::getTransactionId() {
    return transactionId;
}

string Transaction::getTransactionName() {
    return transactionName;
}

string Transaction::getTransactionType() {
    return transactionType;
}

unique_ptr<Payment> Transaction::getPayment() {
    return move(payment);
}

void Transaction::setTransactionName(string transactionName) {
    this->transactionName = transactionName;
}

void Transaction::setTransactionType(string transactionType) {
    this->transactionType = transactionType;
}

void Transaction::setPayment(unique_ptr<Payment> payment) {
    this->payment = move(payment);
}

