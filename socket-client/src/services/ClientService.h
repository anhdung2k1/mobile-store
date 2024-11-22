// Copyright [2024] <Anh Dung>
#pragma once
#include "../model/UserClient.h"
#include "../model/Mobile.h"
#include "../view/ClientView.h"
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
#include <iomanip>
#include <cstdlib>

using namespace std;
class ClientService
{
private:
    static const int buffer_size = 8124;
public:
    struct Response;
    static void pushStackUp(string message);
    static void pushStackDown(string message);
    static int GetPort();
    static string GetAddress();
    // Socket and utils function handling processing
    static void clearStack();
    static bool RequestSend(string request, int sock);
    static bool ResponseReceive(int sock, char *buffer);
    static void HandleReceive(int sock, char *buffer);
    static bool ConvertToBool(string s);
    static void HandleInput();
    static int handleConnect(const char *address, int port);
    static void processPattern(char *buffer, string &pattern, string &value);
    static string processString(string msg);
    static string GetValueFromServer(int sock, string pattern);
    static bool HandlePattern(char *buffer, int sock);
    // User
    static void GetCurrentProfile(int sock, UserClient &user);
    static map<int, UserClient> GetFoundUser(int sock, UserClient user, int &count, WINDOW *finduserWin);
    static void GetUserProfile(int sock, UserClient &user, WINDOW *OrtherUserProfileWin);
    static bool HandleName(string username);
    static bool HandlePassword(string password);
    static bool IsAdminAuthenticated(int sock);
    static bool DeleteAccountUser(int sock, int userId);
    // Mobile Model
    static map<int,int> FindInventoryName(int sock, vector<Mobile> &mobile, string input, string pattern);
    static Mobile GetMobileInformation(int sock, int mobileId);
    static void CreateMobileDevice(int sock, Mobile& mobile);
    static void UpdateMobileDevice(int sock, Mobile& mobile);
    static bool DeleteMobileDevice(int sock, int mobileId);
    // Transaction
    static map<int,int> FindTransactionHistory(int sock, vector<Transaction> &transaction, string input);
    static map<int,int> GetTransactionHistoryWithCustomerId(int sock, vector<Transaction> &transaction, int customerId);
    static Transaction GetTransactionInformation(int sock, int transactionId);
    static void CreateTransaction(int sock, Transaction& transaction, int customerId);
    static void UpdateTransaction(int sock, Transaction& transaction);
    static bool DeleteTransaction(int sock, int transactionId);
    // Customer
    static void CreateCustomer(int sock, Customer& customer);
    static map<int,int> FindCustomerName(int sock, vector<Customer> &customers, string input); 
    static Customer GetCustomerInformation(int sock, int customerId);
    static void UpdateCustomer(int sock, Customer& customer);
    static bool DeleteCustomer(int sock, int customerId);
    static void exitAppChat(int sock);

};
