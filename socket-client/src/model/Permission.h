// Copyright [2024] <Anh Dung>
#pragma once

#include<string>
#include<memory>
#include"Roles.h"
using namespace std;

class Permission {
    private:
        int permissionId;
        string permissionName;
        string permissionModule;
        unique_ptr<Roles> role;
    public:
        Permission();
        Permission(string permissionName);
        Permission(string permissionName, unique_ptr<Roles> role);
        int getPermissionId();
        unique_ptr<Roles> getRole();
        string getPermissionName();
        string getPermissionModule();
        void setPermissionName(string permissionName);
        void setPermissionModule(string permissionModule);
};