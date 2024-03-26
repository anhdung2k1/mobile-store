// Copyright [2024] <Anh Dung>
#pragma once

#include<string>
using std::string;

class Roles {
    private:
        int roleId;
        string roleName;
        string roleDescription;
    public:
        Roles();
        Roles(string roleName);
        int getRoleId();
        string getRoleName();
        string getRoleDescription();
        void setRoleName(string roleName);
        void setRoleDescription(string roleDescription);
};