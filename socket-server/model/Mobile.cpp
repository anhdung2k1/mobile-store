// Copyright [2024] <Anh Dung>
#include<string>
#include "Mobile.h"

Mobile::Mobile()
        : mobileName(""), mobileModel(""), mobileType(""), mobileDescription("") {}

Mobile::Mobile(string mobileName, string mobileModel, string mobileType) 
    : mobileName(mobileName), mobileModel(mobileModel), mobileType(mobileType), mobileDescription("") {}

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

void Mobile::setMobileDescription(string mobileDescription) {
    this->mobileDescription = mobileDescription;
}