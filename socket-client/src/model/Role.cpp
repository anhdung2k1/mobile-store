// Copyright [2024] <Anh Dung>
#include<string>
#include "Roles.h"

Roles::Roles() {
    this->roleName = "USER"; // Default role name is USER
    this->roleDescription = "USER Role has only read permission";
}
Roles::Roles(string roleName) : roleName(roleName) {
    this->roleDescription = "Default value role description";
}

int Roles::getRoleId() {
    return roleId;
}

string Roles::getRoleName() {
    return roleName;
}

string Roles::getRoleDescription() {
    return roleDescription;
}

void Roles::setRoleName(string roleName) {
    this->roleName = roleName;
}

void Roles::setRoleDescription(string roleDescription) {
    this->roleDescription = roleDescription;
}