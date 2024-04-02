#include "Payment.h"

Payment::Payment() : mobile(Mobile()),
                    paymentMethod("Cash"), paymentDescription("Cash method") {}

Payment::Payment(string paymentMethod, string paymentDescription) : mobile(Mobile()),
                    paymentMethod(paymentMethod), paymentDescription(paymentDescription) {}

string Payment::getPaymentMethod() {
    return paymentMethod;
}

string Payment::getPaymentDescription() {
    return paymentDescription;
}

void Payment::setPaymentMethod(string paymentMethod) {
    this->paymentMethod = paymentMethod;
}

void Payment::setPaymentDescription(string paymentDescription) {
    this->paymentDescription = paymentDescription;
}