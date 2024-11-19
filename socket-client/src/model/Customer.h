// Copyright [2024] <Anh Dung>
#pragma once

#include "Mobile.h"

using namespace std;

class Customer {
    private:
        int customerId;
        string customerName;
        string customerEmail;
        string customerAddress;
        string customerGender;
        string customerBirthday;
    public:
        Customer();
        Customer(int customerId, string customerName, string customerAddress, string customerGender, string customerBirthday, string customerEmail);
        int getCustomerId();
        string getCustomerName();
        string getCustomerEmail();
        string getCustomerAddress();
        string getCustomerGender();
        string getCustomerBirthday();
        void setCustomerId(int customerId);
        void setCustomerName(string customerName);
        void setCustomerEmail(string customerEmail);
        void setCustomerAddress(string customerAdress);
        void setCustomerGender(string customerGender);
        void setCustomerBirthday(string customerBirthday);
};