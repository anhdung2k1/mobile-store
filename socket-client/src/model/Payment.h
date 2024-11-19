// Copyright [2024] <Anh Dung>
#pragma once

#include "Mobile.h"

class Payment {
    private:
        int paymentId;
        string paymentDate;
        string paymentMethod;
        string paymentDescription;
    public:
        Payment();
        Payment(string paymentMethod, string paymentDescription);
        string getPaymentDate();
        string getPaymentMethod();
        string getPaymentDescription();
        void setPaymentDate(string paymentDate);
        void setPaymentMethod(string paymentMethod);
        void setPaymentDescription(string paymentDescription);
};