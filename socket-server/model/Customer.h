// Copyright [2024] <Anh Dung>
#pragma once

#include "Mobile.h"

using namespace std;

class Customer {
    private:
        int customerId;
        Mobile mobile;
        string customerName;
        string customerEmail;
        string customerAddress;
        string customerGender;
        Date customerBirthday;
    public:
        Customer();
        Customer(string customerName);
        int getCustomerId();
        Mobile getMobile();
        string getCustomerName();
        string getCustomerEmail();
        string getCustomerAddress();
        string getCustomerGender();
        Date getCustomerBirthday();
        void setMobile(Mobile mobile);
        void setCustomerName(string customerName);
        void setCustomerEmail(string customerEmail);
        void setCustomerAddress(string customerAdress);
        void setCustomerGender(string customerGender);
        void setCustomerBirthday(Date customerBirthday);
};