#include "Payment.h"

Payment::Payment() : paymentDate("2000-1-1"),
                    paymentMethod("Cash"), paymentDescription("Cash method") {}

Payment::Payment(string paymentMethod, string paymentDescription) : paymentDate("2000-1-1"),
                    paymentMethod(paymentMethod), paymentDescription(paymentDescription) {}

string Payment::getPaymentDate() { 
    return paymentDate; 
}

string Payment::getPaymentMethod() {
    return paymentMethod;
}

string Payment::getPaymentDescription() {
    return paymentDescription;
}

void Payment::setPaymentDate(string paymentDate) {
    this->paymentDate = paymentDate;
}

void Payment::setPaymentMethod(string paymentMethod) {
    this->paymentMethod = paymentMethod;
}

void Payment::setPaymentDescription(string paymentDescription) {
    this->paymentDescription = paymentDescription;
}