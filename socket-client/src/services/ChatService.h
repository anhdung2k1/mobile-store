// Copyright [2024] <Anh Dung>
#pragma once
#include "../model/UserClient.h"
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
    static void setLatestId(int id);
    static string GetAddress();
    static int GetPosMsg();
    static void SetPosMsg(int newPos);
    static void clearStack();
    // static void MessageSend(int sock, WINDOW *win, int count);
    static bool RequestSend(string request, int sock);
    static bool ResponseReceive(int sock, char *buffer);
    static void HandleReceive(int sock, char *buffer);
    static bool ConvertToBool(string s);
    // static bool selectRoom(int sock, ChatClient& room, int exitValue,string roomSelect);
    // static bool selectPrivateRoom(int sock, ChatClient &room, int exitValue);
    // static map<int, ChatClient> GetListRoom(int sock, bool &isSuccess, int &count);
    // static void GetListPrivateRoom(int sock, vector<ChatClient> &listChatRooms, bool &isSuccess);
    // static bool joinRoom(int sock, string id, ChatClient &room);
    // static bool joinPrivateRoom(int sock, string id, ChatClient &room);
    // static void GetListChat(int sock, ChatClient &room);
    // static map<int, UserClient> GetListUser(ChatClient joinedRoom, int sock, WINDOW *GetListUserWin, int &counter, UserClient user);
    static void GetCurrentProfile(int sock, UserClient &user);
    static map<int, UserClient> GetFoundUser(int sock, UserClient user, int &count, WINDOW *finduserWin);
    static void HandleInput();
    static int handleConnect(const char *address, int port);
    static void processPattern(char *buffer, string &pattern, string &value);
    // static void checkConnect(int sock, char* buffer);
    // static void waitConnect();
    static void GetUserProfile(int sock, UserClient &user, WINDOW *OrtherUserProfileWin);
    static string GetValueFromServer(int sock, string pattern);
    // static void ProcessUpdateValueFromServer(int sock, string pattern, thread& thisThread);
    static bool HandlePattern(char *buffer, int sock);
    static void exitAppChat(int sock);
    static bool HandleName(string username);
    static bool HandlePassword(string password);
    static void touchUpdate(bool F);
};
