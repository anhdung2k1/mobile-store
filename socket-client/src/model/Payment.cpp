#include "Payment.h"

Payment::Payment() : mobile(move(mobile)), paymentDate(Date(1,1,2000)),
                    paymentMethod("Cash"), paymentDescription("Cash method") {}

Payment::Payment(string paymentMethod, string paymentDescription) : mobile(move(mobile)), paymentDate(Date(1,1,2000)),
                    paymentMethod(paymentMethod), paymentDescription(paymentDescription) {}

Date Payment::getPaymentDate() { 
    return paymentDate; 
}

string Payment::getPaymentMethod() {
    return paymentMethod;
}

string Payment::getPaymentDescription() {
    return paymentDescription;
}

void Payment::setPaymentDate(Date paymentDate) {
    this->paymentDate = paymentDate;
}

void Payment::setPaymentMethod(string paymentMethod) {
    this->paymentMethod = paymentMethod;
}

void Payment::setPaymentDescription(string paymentDescription) {
    this->paymentDescription = paymentDescription;
}