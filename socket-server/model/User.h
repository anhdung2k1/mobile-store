// Copyright [2024] <Anh Dung>
#pragma once

#include"Roles.h"

class User {
 private:
       int id;
       Roles role;
       string name;
       string password;
       string gender;
       string birthday;
       string address;
 public:
       User();
       User(string name, string password);
       User(int id, string name);
       User(int id, string name, string password);
       int getId();
       Roles getRole();
       string getName();
       string getPassword();
       string getGender();
       string getBirthday();
       string getAddress();
       void setId(int id);
       void setRole(Roles role);
       void setName(string name);
       void setPassword(string password);
       void setGender(string gender);
       void setBirthday(string birthday);
       void setAddress(string address);
};