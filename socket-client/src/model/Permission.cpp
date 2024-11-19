// Copyright [2024] <Anh Dung>
#include<string>
#include "Permission.h"

Permission::Permission() 
            : role(Roles("USER")) {
    this->permissionName = "READ"; // Default permission set to USER has READ permission only
    this->permissionModule = "INVENTORY;SALES;CUSTOMER"; // Default permission Module access to system to read
}

Permission::Permission(string permissionName) 
            : role(Roles("USER")), permissionName(permissionName) {
    this->permissionModule = "INVENTORY;SALES;CUSTOMER"; // Default permission Module access to system to
}

Permission::Permission(string permissionName, Roles role) : role(role), permissionName(permissionName) {
    if (this->role.getRoleName() == "ADMIN") {
        this->permissionModule = "INVENTORY;SALES;CUSTOMER;SETTINGS;MANAGE"; //ADMIN role
    } else {
        this->permissionModule = "INVENTORY;SALES;CUSTOMER"; // USER role
    }
}

int Permission::getPermissionId() {
    return permissionId;
}

Roles Permission::getRole() {
    return role;
}

string Permission::getPermissionName() {
    return permissionName;
}

string Permission::getPermissionModule() {
    return permissionModule;
}

void Permission::setPermissionName(string permissionName) {
    this->permissionName = permissionName;
}

void Permission::setPermissionModule(string permissionModule) {
    this->permissionModule = permissionModule;
}