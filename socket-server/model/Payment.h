// Copyright [2024] <Anh Dung>
#pragma once

#include "Mobile.h"

class Payment {
    private:
        int paymentId;
        Mobile mobile;
        string paymentMethod;
        string paymentDescription;
    public:
        Payment();
        Payment(string paymentMethod, string paymentDescription);
        string getPaymentMethod();
        string getPaymentDescription();
        void setPaymentMethod(string paymentMethod);
        void setPaymentDescription(string paymentDescription);
};