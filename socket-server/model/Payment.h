// Copyright [2024] <Anh Dung>
#pragma once

#include "Mobile.h"

class Payment {
    private:
        int paymentId;
        Mobile mobile;
        Date paymentDate;
        string paymentMethod;
        string paymentDescription;
    public:
        Payment();
        Payment(string paymentMethod, string paymentDescription);
        Date getPaymentDate();
        string getPaymentMethod();
        string getPaymentDescription();
        void setPaymentDate(Date paymentDate);
        void setPaymentMethod(string paymentMethod);
        void setPaymentDescription(string paymentDescription);
};