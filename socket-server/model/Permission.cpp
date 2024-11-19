// Copyright [2024] <Anh Dung>
#include<string>
#include "Permission.h"

Permission::Permission() {
    this->permissionName = "READ"; // Default permission set to USER has READ permission only
    this->permissionModule = "INVENTORY;SALES;CUSTOMER"; // Default permission Module access to system to read
}

Permission::Permission(string permissionName, string permissionModule) {
    this->permissionName = permissionName;
    this->permissionModule = permissionModule;
}

int Permission::getPermissionId() {
    return permissionId;
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