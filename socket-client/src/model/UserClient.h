// Copyright [2024] <Anh Dung>
#pragma once

#include <string>
#include "Roles.h"

using namespace std;

class UserClient
{
private:
       int id;
       Roles role;
       string name;
       string password;
       string gender;
       string birthday;
       string address;

public:
       UserClient();
       UserClient(string name, string password);
       UserClient(int id, string name);
       UserClient(int id, string name, string password);
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
