// Copyright [2024] <Anh Dung>
#include "Customer.h"

Customer::Customer() 
    :mobile(make_unique<Mobile>()), customerName(""), customerEmail(""),
    customerAddress(""), customerGender(""), customerBirthday(Date(1,1,2000)) {}

Customer::Customer(string customerName)
    :mobile(make_unique<Mobile>()), customerName(customerName), customerEmail(""), 
    customerAddress(""), customerGender(""), customerBirthday(Date(1,1,2000)) {}

int Customer::getCustomerId() {
    return customerId;
}

unique_ptr<Mobile> Customer::getMobile() {
    return move(mobile);
}

string Customer::getCustomerName() {
    return customerName;
}

string Customer::getCustomerEmail() {
    return customerEmail;
}

string Customer::getCustomerAddress() {
    return customerAddress;
}

string Customer::getCustomerGender() {
    return customerGender;
}

Date Customer::getCustomerBirthday() {
    return customerBirthday;
}

void Customer::setMobile(unique_ptr<Mobile> mobile) {
    this->mobile = move(mobile);
}

void Customer::setCustomerName(string customerName) {
    this->customerName = customerName;
}

void Customer::setCustomerEmail(string customerEmail) {
    this->customerEmail = customerEmail;
}

void Customer::setCustomerAddress(string customerAddress) {
    this->customerAddress = customerAddress;
}

void Customer::setCustomerGender(string customerGender) {
    this->customerGender = customerGender;
}

void Customer::setCustomerBirthday(Date customerBirthday) {
    this->customerBirthday = customerBirthday;
}