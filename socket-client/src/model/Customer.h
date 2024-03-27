// Copyright [2024] <Anh Dung>
#pragma once

#include<string>
#include "../utils/Date.cpp"
#include "Mobile.h"
#include<memory>

using namespace std;

class Customer {
    private:
        int customerId;
        unique_ptr<Mobile> mobile;
        string customerName;
        string customerEmail;
        string customerAddress;
        string customerGender;
        Date customerBirthday;
    public:
        Customer();
        Customer(string customerName);
        int getCustomerId();
        unique_ptr<Mobile> getMobile();
        string getCustomerName();
        string getCustomerEmail();
        string getCustomerAddress();
        string getCustomerGender();
        Date getCustomerBirthday();
        void setMobile(unique_ptr<Mobile> mobile);
        void setCustomerName(string customerName);
        void setCustomerEmail(string customerEmail);
        void setCustomerAddress(string customerAdress);
        void setCustomerGender(string customerGender);
        void setCustomerBirthday(Date customerBirthday);
};