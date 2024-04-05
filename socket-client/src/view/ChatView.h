// Copyright [2024] <Anh Dung>
#pragma once

#include <iostream>
#include <vector>
#include <thread>
#include <cstdlib>
#include <unistd.h>
#include <signal.h>
#include <termios.h>
#include <unistd.h>
#include <ncurses.h>
#include <map>
#include <chrono>
#include <iomanip>
#include "locale.h"
#include "../model/UserClient.h"
#include "../model/Mobile.h"
#include "../model/Transaction.h"
#include "../view/StatusBar.h"

using std::map;
class ChatView
{
private:
    static const int buffer_size = 8124;
public:
    enum View
    {
        Login = 0,
        Main = 1,
        FindUser = 2,
        Update = 3,
        Chatting = 4,
        OrtherUserProfile = 5,
        LoginInput = 6,
        RegisterInput = 7,
        MobileInventory = 8,
        Transactions = 9,
        Customer = 10,
        MobileDetails = 11
    };
    static void handleLogin(int sock, int selection);
    static void UserConsole(int sock);
    static void LoginMenu();
    static void UI();
    static void Menu();
    static int selectMenu();
    static void handleMenu(int sock, int selection);
    static void interactUserMenu(int count, map<int, UserClient> foundUser, int sock);
    static void findMobileMenu(int sock, map<int, int>& idMobileInventory);
    static void UserUI(int sock, UserClient user, WINDOW *interactUserMenuWin);
    static void MobileUI(int sock, int mobileId, WINDOW *mobileInformation);
    static UserClient getUser();
    static WINDOW *getCurrentWin();
};
