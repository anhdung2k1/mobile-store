// Copyright [2024] <Anh Dung>
#include<string>
#include "Mobile.h"

Mobile::Mobile()
        : mobileName(""), mobileModel(""), mobileType(""), mobileQuantity(0), mobilePrice("0 VND"), mobileDescription("") {}

Mobile::Mobile(string mobileName, string mobileModel, string mobileType, int mobileQuantity, string mobilePrice, string mobileDescription) 
    : mobileName(mobileName), mobileModel(mobileModel), mobileType(mobileType),
        mobileQuantity(mobileQuantity), mobilePrice(mobilePrice),mobileDescription(mobileDescription) {}

Mobile::Mobile(int mobileId, string mobileName, string mobileModel, string mobileType, int mobileQuantity, string mobilePrice, string mobileDescription) 
    : mobileId(mobileId) ,mobileName(mobileName), mobileModel(mobileModel), mobileType(mobileType),
        mobileQuantity(mobileQuantity), mobilePrice(mobilePrice),mobileDescription(mobileDescription) {}

int Mobile::getMobileId() {
    return mobileId;
}

string Mobile::getMobileName() {
    return mobileName;
}

string Mobile::getMobileModel() {
    return mobileModel;
}

string Mobile::getMobileType() {
    return mobileType;
}

int Mobile::getMobileQuantity() {
    return mobileQuantity;
}

string Mobile::getMobilePrice() {
    return mobilePrice;
}

string Mobile::getMobileDescription() {
    return mobileDescription;
}

void Mobile::setMobileName(string mobileName) {
    this->mobileName = mobileName;
}

void Mobile::setMobileModel(string mobileModel) {
    this->mobileModel = mobileModel;
}

void Mobile::setMobileType(string mobileType) {
    this->mobileType = mobileType;
}

void Mobile::setMobileQuantity(int mobileQuantity) {
    this->mobileQuantity = mobileQuantity;
}

void Mobile::setMobilePrice(string mobilePrice) {
    this->mobilePrice = mobilePrice;
}
void Mobile::setMobileDescription(string mobileDescription) {
    this->mobileDescription = mobileDescription;
}