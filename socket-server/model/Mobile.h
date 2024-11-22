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
        int mobileQuantity;
        double mobilePrice;
        string mobileDescription;
    public:
        Mobile();
        Mobile(string mobileName, string mobileModel, string mobileType,
                int mobileQuantity, string mobilePrice,string setMobileDescription);
        int getMobileId();
        string getMobileName();
        string getMobileModel();
        string getMobileType();
        int getMobileQuantity();
        double getMobilePrice();
        string getMobileDescription();
        void setMobileId(int mobileId);
        void setMobileName(string mobileName);
        void setMobileModel(string mobileModel);
        void setMobileType(string mobileType);
        void setMobileQuantity(int mobileQuantity);
        void setMobilePrice(double mobilePrice);
        void setMobileDescription(string mobileDescription);
};