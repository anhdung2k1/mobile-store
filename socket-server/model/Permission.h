// Copyright [2024] <Anh Dung>
#pragma once

#include "Roles.h"

using namespace std;

class Permission {
    private:
        int permissionId;
        string permissionName;
        string permissionModule;
    public:
        Permission();
        Permission(string permissionName, string permissionModule);
        int getPermissionId();
        string getPermissionName();
        string getPermissionModule();
        void setPermissionName(string permissionName);
        void setPermissionModule(string permissionModule);
};