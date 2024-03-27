// Copyright [2024] <Anh Dung>
#pragma once

#include<iostream>
#include<vector>
#include"../model/MessageClient.h"
#include"../model/ChatClient.h"
#include"../services/ChatService.h"
#include"../model/UserClient.h"
#include"../view/StatusBar.h"
#include<thread>
#include<cstdlib>
#include<unistd.h>
#include <signal.h>
#include <termios.h>
#include <unistd.h>
#include <ncurses.h>
#include <map>
using std::map;
class ChatView {
 public:
    enum View{Login=0, Main=1, CreateRoom=2, JoinRoom=3, FindUser=4,
     Update=5, Chatting=6, ChattingOption = 7, OrtherUserProfile = 8,
     LoginInput=9, RegisterInput=10};
    static bool GetIsEmptyChat();
    static void appendListRoom(ChatClient newRoom);
    static void scrollScreenDownAtTop(WINDOW* win, int limit);
    static void scrollScreenDownWithAction(WINDOW *win, int limit, string message);
    static void scrollScreenUpWithAction(WINDOW *win, int limit, string message);
    static void scrollScreenUp(int& count, WINDOW *win, int limit, string& tempMsg);
    static void scrollScreenDown(int& count, WINDOW *win, int limit, string message);
    static void displayMyMessage(string content, WINDOW* win, int& y, int limit, string& tempMsg);
    static void displayInform(string message, WINDOW* win, int& y, int limit, string& tempMsg);
    static void displayOrtherMessage(string message, WINDOW* win, int& y, int limit, string& tempMsg);
    static void displayMyMessageAtTop(string content, WINDOW* win, int& y, int limit);
    static void displayOrtherMessageAtTop(string message, WINDOW* win, int& y, int limit);
    static void displayMsgAtTop(MessageClient message, WINDOW* win, int limit);
    static void displayMessage(MessageClient message, WINDOW* win, int& y);
    static void displayChat(ChatClient chat, WINDOW* win, int& count);
    static void handleLogin(int sock, int selection);
    static void UserConsole(int sock);
    static void ChatRoomConsole(ChatClient room, WINDOW* win);
    static void LoginMenu();
    static void UI();
    static void Menu();
    static int selectMenu();
    static void handleMenu(int sock, int selection);
    static void ChatConsole(ChatClient joinedRoom, int sock);
    static void interactUserMenu(int count, map<int, UserClient> foundUser, int sock);
    static void ChattingView(int sock, ChatClient joinedRoom);
    //static void ChooseRoom(vector<ChatClient> listChatRooms, int sock);
    static void ChooseRoom(int sock, int count);
    static void ChoosePrivateRoom(vector<ChatClient> listChatRooms, int sock);
    static void UserUI( int sock, UserClient user,WINDOW *interactUserMenuWin);
    static View GetCurrentView();
    static void ReloadView(View view, int sock);
    static void ChangeBarText(string text);
    static void ResetBar(int length);
    static UserClient getUser();
    static ChatClient getJoinedRoom();
    static WINDOW *getCurrentWin();
    static int getRowRoom();
    static void setRowRoom(int pos);
    static int getColRoom();
    static void setColRoom(int pos);
    static int getNumRoom();
    static void setNumRoom(int pos);
};

