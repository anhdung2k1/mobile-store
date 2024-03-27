// Copyright [2024] <Anh Dung>
#pragma once

#include <iostream>
#include <vector>
#include "../model/UserClient.h"
#include "../view/StatusBar.h"
#include <thread>
#include <cstdlib>
#include <unistd.h>
#include <signal.h>
#include <termios.h>
#include <unistd.h>
#include <ncurses.h>
#include <map>
using std::map;
class ChatView
{
public:
    enum View
    {
        Login = 0,
        Main = 1,
        CreateRoom = 2,
        JoinRoom = 3,
        FindUser = 4,
        Update = 5,
        Chatting = 6,
        ChattingOption = 7,
        OrtherUserProfile = 8,
        LoginInput = 9,
        RegisterInput = 10
    };
    static void handleLogin(int sock, int selection);
    static void UserConsole(int sock);
    static void LoginMenu();
    static void UI();
    static void Menu();
    static int selectMenu();
    static void handleMenu(int sock, int selection);
    static void interactUserMenu(int count, map<int, UserClient> foundUser, int sock);
    static void UserUI(int sock, UserClient user, WINDOW *interactUserMenuWin);
    static View GetCurrentView();
    static void ReloadView(View view, int sock);
    static UserClient getUser();
    static WINDOW *getCurrentWin();
};
