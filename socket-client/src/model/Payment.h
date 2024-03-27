// Copyright [2024] <Anh Dung>
#pragma once

#include<string>
#include "Mobile.h"
#include "../utils/Date.cpp"
using std::string;

class Payment {
    private:
        int paymentId;
        unique_ptr<Mobile> mobile;
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