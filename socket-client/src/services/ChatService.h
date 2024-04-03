// Copyright [2024] <Anh Dung>
#pragma once
#include "../model/UserClient.h"
#include "../model/Mobile.h"
#include "../view/ChatView.h"
#include "../view/StatusBar.h"
#include <vector>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <string.h>
#include <sstream>
#include <iostream>
#include <signal.h>
#include <stdio.h>
#include <nlohmann/json.hpp>
#include <map>
#include <thread>
#include <ncurses.h>
#include <queue>
#include <stack>
using std::map;
using std::string;
using std::thread;
class ChatService
{
public:
    struct Response;
    static void pushStackUp(string message);
    static void pushStackDown(string message);
    static int GetPort();
    static string GetAddress();
    static void clearStack();
    static bool RequestSend(string request, int sock);
    static bool ResponseReceive(int sock, char *buffer);
    static void HandleReceive(int sock, char *buffer);
    static bool ConvertToBool(string s);
    static void GetCurrentProfile(int sock, UserClient &user);
    static void FindInventoryName(int sock, vector<Mobile> &mobile, string input, string pattern);
    static void GetTransactionHistory(int sock, vector<Transaction> &transaction);
    static map<int, UserClient> GetFoundUser(int sock, UserClient user, int &count, WINDOW *finduserWin);
    static void HandleInput();
    static int handleConnect(const char *address, int port);
    static void processPattern(char *buffer, string &pattern, string &value);
    static string processString(string msg);
    static void GetUserProfile(int sock, UserClient &user, WINDOW *OrtherUserProfileWin);
    static string GetValueFromServer(int sock, string pattern);
    static bool HandlePattern(char *buffer, int sock);
    static void exitAppChat(int sock);
    static bool HandleName(string username);
    static bool HandlePassword(string password);
    static void touchUpdate(bool F);
};
