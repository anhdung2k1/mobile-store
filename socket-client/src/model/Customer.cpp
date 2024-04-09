// Copyright [2024] <Anh Dung>
#include "Customer.h"

Customer::Customer() 
    : customerName(""), customerEmail(""),
    customerAddress(""), customerGender(""), customerBirthday("2000-1-1") {}

Customer::Customer(int customerId, string customerName, string customerAddress, string customerGender, string customerBirthday, string customerEmail)
    :customerId(customerId), customerName(customerName), customerAddress(customerAddress), 
    customerGender(customerGender), customerBirthday(customerBirthday), customerEmail(customerEmail) {}

int Customer::getCustomerId() {
    return customerId;
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

string Customer::getCustomerBirthday() {
    return customerBirthday;
}

void Customer::setCustomerId(int customerId) {
    this->customerId = customerId;
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

void Customer::setCustomerBirthday(string customerBirthday) {
    this->customerBirthday = customerBirthday;
}