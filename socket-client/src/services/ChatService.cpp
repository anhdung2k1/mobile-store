// Copyright [2024] <Anh Dung>
#include "ChatService.h"
#include <iomanip>
#include <cstdlib>
#define PORT 8000
#define ADDRESS "172.17.0.5"

using std::cin;
using std::cout;
using std::endl;
using std::map;
using std::queue;
using std::stack;
using std::string;
using std::stringstream;
using std::thread;
using std::vector;

bool exitChat = false;
bool checkingUpdate;
bool exitApp = false;
bool reconnect = false;
int posMsg;
int overflowLength;
string tempMsg;
int latestMsgId;
queue<string> pendingMsg;
stack<string> hiddenMsgUp;
stack<string> hiddenMsgDown;

void ChatService::pushStackUp(string message)
{
    hiddenMsgUp.push(message);
}

void ChatService::pushStackDown(string message)
{
    hiddenMsgDown.push(message);
}

struct ChatService::Response
{
    string pattern;
    string value;
};

ChatService::Response response;

void ChatService::clearStack()
{
    stack<string>().swap(hiddenMsgUp);
    stack<string>().swap(hiddenMsgDown);
}

void ChatService::HandleInput()
{
    if (cin.fail())
    {
        cout << "ERROR \n";
        cin.clear();
        cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
    }
}

int ChatService::GetPort()
{
    return PORT;
}

string ChatService::GetAddress()
{
    return ADDRESS;
}

bool ChatService::RequestSend(string request, int sock)
{
    if (send(sock, request.c_str(), request.size(), 0))
    {
        return true;
    }
    return false;
}

void ChatService::processPattern(char *buffer, string &pattern, string &value)
{
    string convertedBuffer = (string)buffer;
    int pos = convertedBuffer.find("|");
    pattern = convertedBuffer.substr(0, pos);
    value = convertedBuffer.substr(pos + 1);
}

string ChatService::processString(string msg)
{
    msg.erase(remove_if(msg.begin(), msg.end(), [](char c) { return c == '"'; }), msg.end());
    return msg;
}

bool ChatService::HandlePattern(char *buffer, int sock)
{
    string pattern, value;
    processPattern(buffer, pattern, value);
    WINDOW *win = ChatView::getCurrentWin();
    int y, x;
    getyx(win, y, x);

    if (pattern == "RECONNECT")
    {
        nlohmann::json resendMsg = nlohmann::json::array();
        while (!pendingMsg.empty())
        {
            resendMsg.insert(resendMsg.end(), pendingMsg.front());
            pendingMsg.pop();
        }
        RequestSend("RESEND|" + resendMsg.dump(), sock);
    }
    else
    {
        response.pattern = pattern;
        response.value = value;
    }
    return true;
}

string ChatService::GetValueFromServer(int sock, string pattern)
{
    if (!reconnect)
    {
        while (1)
        {
            if (response.pattern == pattern)
            {
                response.pattern = "";
                string res = response.value;
                response.value = "";
                return res;
            }
        }
    }
    return "";
}

bool ChatService::ResponseReceive(int sock, char *buffer)
{
    bzero(buffer, 1024);
    int valread = recv(sock, buffer, 1024, 0);
    // cout << "DEBUG: ResponseReceive()\n";
    // cout << (string)buffer << endl;
    // cout << "ENDDEBUG: ResponseReceive()\n";
    if (valread <= 0)
    {

        return false;
    }
    return true;
}

void ChatService::HandleReceive(int sock, char *buffer)
{
    while (!exitApp)
    {
        if (!ResponseReceive(sock, buffer))
        {
            reconnect = true;
            close(sock);
            if (!exitApp)
            {
                sock = handleConnect(ADDRESS, PORT);
                nlohmann::json backupJson = nlohmann::json::object();
                UserClient backupUser = ChatView::getUser();
                backupJson.push_back({"userName", backupUser.getName()});
                backupJson.push_back({"userPassword", backupUser.getPassword()});
                string backupString = backupJson.dump();
                RequestSend("RECONNECT|" + backupString, sock);
            }
        }
        else
        {
            HandlePattern(buffer, sock);
        }
    }
}

bool ChatService::ConvertToBool(string s)
{
    bool b;
    std::istringstream(s) >> b;
    return b;
}

void ChatService::touchUpdate(bool F)
{
    checkingUpdate = F;
}

map<int, UserClient> ChatService::GetFoundUser(int sock, UserClient user, int &count, WINDOW *finduserWin)
{
    map<int, UserClient> foundUser;
    string response = GetValueFromServer(sock, "FIND_USER");
    int choice;
    int row = 2;
    int col = 2;
    if (response.length() != 0)
    {
        nlohmann::json j = nlohmann::json::parse(response);
        for (auto i : j)
        {
            int id = i.at("userId");
            string name = i.at("userName");
            if (name != user.getName())
            {
                UserClient newUser(id, name);
                foundUser[++count] = newUser;
                // cout << count << ". " << name << endl;
                // mvwprintw(finduserWin, counter, 2,"%d. %s", count,name);
                mvwprintw(finduserWin, row, col, "%d .%s", count, name.c_str());
                wrefresh(finduserWin);
                row++;
            }
            if (row == 11)
            {
                row = 2;
                col = col + 20;
            }
        }
        mvwprintw(finduserWin, row++, 2, "%d .%s", ++count, "Exit");
        wrefresh(finduserWin);
    }
    else
    {
        // cout << "No result matches your search!\n";
        mvwprintw(finduserWin, row, 2, "No result matches your search!");
        wrefresh(finduserWin);
        ChatView::handleMenu(sock, 102);
    }
    return foundUser;
}

void ChatService::GetCurrentProfile(int sock, UserClient &user)
{
    string response = GetValueFromServer(sock, "UPDATE_PROFILE");
    nlohmann::json j = nlohmann::json::parse(response);
    if (j.at("address").is_null())
    {
        j.at("address") = to_string(j.at("address"));
    }
    if (j.at("gender").is_null())
    {
        j.at("gender") = to_string(j.at("gender"));
    }
    user.setAddress(j.at("address"));
    user.setGender(j.at("gender"));
}

void ReconnectStatus()
{
    string notification;
    WINDOW *win = ChatView::getCurrentWin();
    int y, x;
    getyx(win, y, x);
    int count = 0;
    while (reconnect)
    {
        sleep(1);
        if (count == 0)
        {
            notification = "Waiting for connection.  ";
            count++;
        }
        else if (count == 1)
        {
            notification = "Waiting for connection.. ";
            count++;
        }
        else if (count == 2)
        {
            notification = "Waiting for connection...";
            count = 0;
        }
    }
}

int ChatService::handleConnect(const char *address, int port)
{
    int sock = 0;
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0)
    {
        // cout << "\nSocket creation error";
        return -1;
    }
    struct sockaddr_in serv_addr;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port);
    if (inet_pton(AF_INET, address, &serv_addr.sin_addr) <= 0)
    {
        // cout << "\nInvalid address/ Address not supported";
        return -1;
    }
    thread changeStatusBar;
    if (reconnect)
    {
        changeStatusBar = thread(ReconnectStatus);
    }
    int connection;
    while (1)
    {
        connection = connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr));
        if (connection >= 0)
        {
            if (reconnect)
            {
                reconnect = false;
                changeStatusBar.join();
            }
            break;
        }
    }
    return sock;
}

void ChatService::exitAppChat(int sock)
{
    RequestSend("USER_EXIT_APP|", sock);
    exitApp = true;
}

void ChatService::GetUserProfile(int sock, UserClient &user, WINDOW *OrtherUserProfileWin)
{
    stringstream ss;
    ss << user.getId();
    string str = ss.str();
    ChatService::RequestSend("USER_PROFILE|" + str, sock);
    string response = GetValueFromServer(sock, "USER_PROFILE");
    nlohmann::json j = nlohmann::json::parse(response);
    if (j.at("userName").is_null())
    {
        j.at("userName") = to_string(j.at("userName"));
    }
    if (j.at("address").is_null())
    {
        j.at("address") = to_string(j.at("address"));
    }
    if (j.at("gender").is_null())
    {
        j.at("gender") = to_string(j.at("gender"));
    }
    user.setName(j.at("userName"));
    user.setAddress(j.at("address"));
    user.setGender(j.at("gender"));
    // mvwprintw(OrtherUserProfileWin, 15, 1,"hello");
    mvwprintw(OrtherUserProfileWin, 8, 1, "%s :%s", "Name ", user.getName().c_str());
    mvwprintw(OrtherUserProfileWin, 9, 1, "%s :%s", "Address ", user.getAddress().c_str());
    mvwprintw(OrtherUserProfileWin, 10, 1, "%s :%s", "Gender ", user.getGender().c_str());
    wrefresh(OrtherUserProfileWin);
}

void ChatService::FindInventoryName(int sock, vector<Mobile> &mobile, string input, string pattern)
{
    ChatService::RequestSend(pattern + "|" + input, sock);
    string response = ChatService::GetValueFromServer(sock, pattern);
    if (response.length() > 0) {
        nlohmann::json j = nlohmann::json::parse(response);
        mvprintw(5, 20, "%s", "Mobile Name");
        mvprintw(5, 40, "%s", "Mobile Type");
        mvprintw(5, 60, "%s", "Mobile Model");
        mvprintw(5, 80, "%s", "Mobile Quantity");
        mvprintw(5, 100, "%s", "Mobile Price");
        mvprintw(5, 120, "%s", "Mobile Description");

        for (auto it : j) {
            Mobile mb(
                it.at("mobileID").get<int>(),
                it.at("mobileName").get<string>(),
                it.at("mobileModel").get<string>(),
                it.at("mobileType").get<string>(),
                it.at("mobileQuantity").get<int>(),
                it.at("mobilePrice").get<string>(),
                it.at("mobileDescription").get<string>()
            );
            mobile.push_back(mb);
        }
    } else {
        mvprintw(5, 20, "%s", "Could not found any mobile device!!");
    }
}

void ChatService::GetTransactionHistory(int sock, vector<Transaction> &transaction) {
    ChatService::RequestSend("GET_TRANSACTION_HISTORY|", sock);
    string response = ChatService::GetValueFromServer(sock, "GET_TRANSACTION_HISTORY");
    if(response.length() > 0) {
        nlohmann::json j = nlohmann::json::parse(response);
        mvprintw(0, 20, "%s", "Transaction Name");
        mvprintw(0, 40, "%s", "Transaction Type");
        mvprintw(0, 60, "%s", "Payment Method");
        
        for(auto tr : j) {
            Transaction trans(
                tr.at("transactionName").get<string>(),
                tr.at("transactionType").get<string>(),
                tr.at("paymentMethod").get<string>()
            ); 
            transaction.push_back(trans);
        }
    }
    else {
        mvprintw(5, 20, "%s", "Could not found any transaction history!!");
    }    
}

bool ChatService::HandleName(string username)
{
    if (username.length() < 2)
    {
        return false;
    }
    for (int i = 0; i <= username.length(); i++)
    {
        if (isspace(username[i]))
        {
            if (isspace(username[i + 1]))
            {
                return false;
            }
        }
    }
    for (int i = 0; i < username.length(); i++)
    {
        if ((username[i] >= 'A' && username[i] <= 'Z') || (username[i] >= 'a' && username[i] <= 'z') || (username[i] == ' '))
        {
            continue;
        }
        return false;
    }
    return true;
}

bool ChatService::HandlePassword(string password)
{
    bool isNumber = FALSE;
    bool isAlphabet = FALSE;
    bool isSpecial = FALSE;
    string specialChar = "!\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";

    for (int i = 0; i < password.length(); i++)
    {
        if (isalpha(password[i]) != 0)
        {
            isAlphabet = TRUE;
        }
        if (isdigit(password[i]) != 0)
        {
            isNumber = TRUE;
        }
        if (specialChar.find(password[i]) != std::string::npos)
        {
            isSpecial = TRUE;
        }
        if (isNumber && isAlphabet && isSpecial)
        {
            return true;
        }
    }
    return false;
}

