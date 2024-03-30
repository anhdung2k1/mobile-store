// Copyright [2024] <Anh Dung>
#pragma once

#include<string>
#include "../utils/Date.cpp"

using namespace std;

class Mobile {
    private:
        int mobileId;
        string mobileName;
        string mobileModel;
        string mobileType;
        string mobileDescription;
    public:
        Mobile();
        Mobile(string mobileName, string mobileModel, string mobileType);
        int getMobileId();
        string getMobileName();
        string getMobileModel();
        string getMobileType();
        string getMobileDescription();
        void setMobileName(string mobileName);
        void setMobileModel(string mobileModel);
        void setMobileType(string mobileType);
        void setMobileDescription(string mobileDescription);
};