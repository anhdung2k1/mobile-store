// Copyright [2024] <Anh Dung>
#pragma once

#include "Roles.h"

using namespace std;

class Permission {
    private:
        int permissionId;
        string permissionName;
        string permissionModule;
        Roles role;
    public:
        Permission();
        Permission(string permissionName);
        Permission(string permissionName, Roles role);
        int getPermissionId();
        Roles getRole();
        string getPermissionName();
        string getPermissionModule();
        void setPermissionName(string permissionName);
        void setPermissionModule(string permissionModule);
};