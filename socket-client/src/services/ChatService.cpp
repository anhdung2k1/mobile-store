// Copyright [2024] <Anh Dung>
#include "ChatService.h"
#include <iomanip>
#include <cstdlib>
// #define PORT 31226
// #define ADDRESS "192.168.122.239"
#define PORT 8000
#define ADDRESS "172.17.0.5"

using std::string;
using std::stringstream;
using std::cout;
using std::cin;
using std::endl;
using std::vector;
using std::thread;
using std::map;
using std::queue;
using std::stack;

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
stack<MessageClient> history;

void ChatService::pushStackUp(string message) {
    hiddenMsgUp.push(message);
}

void ChatService::pushStackDown(string message) {
    hiddenMsgDown.push(message);
}

struct ChatService::Response {
    string pattern;
    string value;
};

ChatService::Response response;

void ChatService::clearStack() {
    stack<string>().swap(hiddenMsgUp);
    stack<string>().swap(hiddenMsgDown);
    stack<MessageClient>().swap(history);
}

void ChatService::HandleInput() {
    if(cin.fail()) {
        cout << "ERROR \n";
        cin.clear();
        cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
    }
}

int ChatService::GetPort() {
    return PORT;
}

int ChatService::GetPosMsg() {
    return posMsg;
}

void ChatService::SetPosMsg(int newPos) {
    posMsg = newPos;
}

void ChatService::setLatestId(int id) {
    latestMsgId = id;
}

string ChatService::GetAddress() {
    return ADDRESS;
}

// void sighandler(int signal) {
//     exitChat = true;
// }hattingView

void ChatService::MessageSend(int sock, WINDOW *win, int count) {
    // ChatView::ChangeBarText(std::to_string(latestMsgId));
    posMsg = count;
    // ChatView::ChangeBarText(std::to_string(count));
    exitChat = false;
    bool stillLoadHistory = !ChatView::GetIsEmptyChat();
    string prompt = "Enter message: ";
    init_pair(3, COLOR_CYAN, COLOR_BLACK);
    wattron(win, COLOR_PAIR(3));
    mvwprintw(win, win->_maxy - 1, 3, "%s", prompt.c_str());
    wattroff(win, COLOR_PAIR(3));
    overflowLength = 0;
    curs_set(1);
    while (!exitChat) {
        int curs_x = prompt.length() + 3;
        int curs_y = win->_maxy - 1;
        wmove(win, curs_y, curs_x);
        wclrtoeol(win);
        int n;
        char c = char(n);
        char *ch = &c;
        string input = "";
        char str[win->_maxx];
        do {
            // ChatView::ChangeBarText(std::to_string(posMsg));
            // ChatView::ChangeBarText(std::to_string(posMsg));
            // Get the key
            // noecho();
            n = wgetch(win);
            // if get Esc(27), it will turn back to login menu
            if(n == 27) // back
            {
                exitChat = true;
                // Call option menu
                clear();
                endwin();
                break;
            } else if (n == 8 || n == 127) { // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
                if (input.length() != 0) {// check if input length == 0 then no need to pop from string input
                    if (curs_x == prompt.length() + 3 && overflowLength > 0) {
                        bzero(str, win->_maxx);
                        mvwinstr(win, win->_maxy - 2, prompt.length() + 3, str);
                        mvwprintw(win, win->_maxy - 1, prompt.length() + 3,"%s", str);
                        wmove(win, win->_maxy - 2, prompt.length() + 3);
                        wclrtoeol(win);
                        ChatView::scrollScreenDown(posMsg, win, win->_maxy - 1 - overflowLength, tempMsg);
                        curs_y = win->_maxy - 1;
                        curs_x = win->_maxx - 3;
                        wmove(win, curs_y, curs_x);
                        overflowLength--;
                        // mvwprintw(win, j, prompt.length() + 3, str);
                    }
                    input.pop_back();
                    wmove(win, curs_y, --curs_x);
                    wclrtoeol(win);
                    wrefresh(win);
                } else {
                    wmove(win, curs_y, curs_x);
                    wclrtoeol(win);
                    wrefresh(win);
                }
            } else if(n == 10 && input.length() != 0) {
                if (overflowLength > 0) {
                    for (int i = win->_maxy - 1 - overflowLength; i < win->_maxy - 1; i++) {
                        wmove(win, i, 3);
                        wclrtoeol(win);
                    }
                    overflowLength = 0;
                }
                ChatView::displayMyMessage(input, win, posMsg, win->_maxy - 1 - overflowLength, tempMsg);
                if (reconnect || !pendingMsg.empty()) {
                    pendingMsg.push(input);
                } else {
                    RequestSend("USER_CHATTING|" + input, sock);
                }
                break;
            } else if (n == KEY_UP) {
                // ChatView::ChangeBarText(std::to_string(history.size()) + " | " + std::to_string(hiddenMsgUp.size()));
                if (!hiddenMsgUp.empty()) {
                    bzero(str, win->_maxx);
                    mvwinstr(win, win->_maxy - 2 - overflowLength, 3, str);
                    hiddenMsgDown.push((string)str);
                    ChatView::scrollScreenDownWithAction(win, win->_maxy - 1 - overflowLength, hiddenMsgUp.top());
                    hiddenMsgUp.pop();
                    wmove(win, curs_y, curs_x);
                } else if (hiddenMsgUp.empty() && !history.empty()) {
                    ChatView::displayMsgAtTop(history.top(), win, win->_maxy - 2 - overflowLength);
                    history.pop();
                } else if (hiddenMsgUp.empty() && history.empty() && stillLoadHistory) {
                    RequestSend("LOAD_HISTORY|" + std::to_string(latestMsgId), sock);
                    string response = GetValueFromServer(sock, "LOAD_HISTORY");
                    // cout << "DEBUG: " << response << endl;
                    if (response.length() == 0) {
                        stillLoadHistory = false;
                        wmove(win, curs_y, curs_x);
                        wclrtoeol(win);
                    } else {
                        nlohmann::json j = nlohmann::json::parse(response);
                        latestMsgId = j[0].at("id");
                        for (auto i: j) {
                            UserClient sender;
                            sender.setName(i.at("userName"));
                            MessageClient message(i.at("id"), sender, i.at("content"));
                            history.push(message);
                        }
                        ChatView::displayMsgAtTop(history.top(), win, win->_maxy - 2 - overflowLength);
                        history.pop();
                    }
                } else {
                    wmove(win, curs_y, curs_x);
                    wclrtoeol(win);
                }
            } else if (n == KEY_DOWN) {
                if (!hiddenMsgDown.empty()) {
                    bzero(str, win->_maxx);
                    mvwinstr(win, 3, 3, str);
                    hiddenMsgUp.push((string)str);
                    ChatView::scrollScreenUpWithAction(win, win->_maxy - 1 - overflowLength, hiddenMsgDown.top());
                    // posMsg--;
                    hiddenMsgDown.pop();
                    wmove(win, curs_y, curs_x);
                } else {
                    wmove(win, curs_y, curs_x);
                    wclrtoeol(win);
                }
            } else if (overflowLength != 2 && n != 10) {
                if (curs_x == win->_maxx - 3) {
                    overflowLength++;
                    if(overflowLength != 2) {
                        wmove(win, curs_y, curs_x);
                        wclrtoeol(win);
                        ChatView::scrollScreenUp(posMsg, win, win->_maxy - overflowLength, tempMsg);
                        for(int j = win->_maxy - 1 - overflowLength; j < win->_maxy - 1; j++) {
                            bzero(str, win->_maxx);
                            mvwinstr(win, j+1, prompt.length() + 3, str);
                            wmove(win, j, 3);
                            wclrtoeol(win);
                            mvwprintw(win, j, prompt.length() + 3,"%s", str);
                        }
                        curs_x = prompt.length() + 3;
                        curs_y = win->_maxy - 1;
                    }
                    wmove(win, curs_y, curs_x);
                    wclrtoeol(win);
                }
                if (overflowLength != 2) {
                    c = char(n);
                    input.push_back(c);
                    mvwprintw(win, curs_y, curs_x++,"%s", ch);// print the current key on screen and push it to input string
                    wmove(win, curs_y, curs_x);
                    wclrtoeol(win);
                    wrefresh(win);
                } else {
                    wmove(win, curs_y, curs_x);
                    wclrtoeol(win);
                }
            } else {
                wmove(win, curs_y, curs_x);
                wclrtoeol(win);
            }
        } while (true);
        wrefresh(win);
    }
    // readThread.join();
    RequestSend("END_READING|", sock);

}

bool ChatService::RequestSend(string request, int sock) {
    if (send(sock, request.c_str(), request.size(), 0)) {
        return true;
    }
    return false;
}

void ChatService::processPattern(char* buffer, string& pattern, string& value) {
    // ChatView::ChangeBarText((string)buffer);
    string convertedBuffer = (string)buffer;
    int pos = convertedBuffer.find("|");
    pattern = convertedBuffer.substr(0, pos);
    value = convertedBuffer.substr(pos + 1);
    // cout << pattern << "|" << value.length() << endl;
}

bool ChatService::HandlePattern(char* buffer, int sock) {
    string pattern, value;
    processPattern(buffer, pattern, value);
    ChatView::View currentView = ChatView::GetCurrentView();
    WINDOW *win = ChatView::getCurrentWin();
    int y, x;
    getyx(win, y, x);
    if (pattern == "USER_CHATTING") {
        if (currentView == 6) {
            ChatView::displayOrtherMessage(value, win, posMsg, win->_maxy - 1 - overflowLength, tempMsg);
            wmove(win, y, x);
            wrefresh(win);
        } else {
            // Handle notification here
            // Call changebartext
        }
    } else if (pattern == "RELOAD_ROOM") {
        // ChatView::ResetBar(20);
        // ChatView::ChangeBarText(std::to_string(currentView));
        if (currentView == 3) {
            int posRoom = ChatView::getRowRoom();
            int countRoom = ChatView::getNumRoom();
            nlohmann::json j = nlohmann::json::parse(value);
            ChatClient room(j.at("RoomId"), j.at("RoomName"));
            mvwprintw(win, posRoom++, ChatView::getColRoom(), "%d. %s" ,  ++countRoom, room.getRoomName().c_str());
            ChatView::setNumRoom(countRoom);
            ChatView::setRowRoom(posRoom);
            ChatView::appendListRoom(room);
            wmove(win, y, x);
            wrefresh(win);
        }
    } else if (pattern == "RECONNECT") {
        nlohmann::json resendMsg = nlohmann::json::array();
        while(!pendingMsg.empty()) {
            resendMsg.insert(resendMsg.end(), pendingMsg.front());
            pendingMsg.pop();
        }
        RequestSend("RESEND|" + resendMsg.dump(), sock);
    } else if (pattern == "INFORM") {
        if (currentView == 6) {
            ChatView::displayInform(value, win, posMsg, win->_maxy - 1 - overflowLength, tempMsg);
            wmove(win, y, x);
            wrefresh(win);
        }
    } else {
        response.pattern = pattern;
        response.value = value;
    }
    return true;
}

string ChatService::GetValueFromServer(int sock, string pattern) {
    if(!reconnect) {
        while(1) {
            if (response.pattern == pattern) {
                if (pattern == "CREATE_ROOM") {
                    if (response.value.length() != 0) {
                        return "You has created a room\n";
                    } else {
                        return "Create room failed\n";
                    }
                } else if (pattern == "LOAD_HISTORY") {
                    sleep(1);
                }
                response.pattern = "";
                string res = response.value;
                response.value = "";
                return res;
            }
        }
    }
    return "";
}

bool ChatService::ResponseReceive(int sock, char* buffer) {
    bzero(buffer, 1024);
    int valread = recv(sock, buffer, 1024, 0);
    // cout << "DEBUG: ResponseReceive()\n";
    // cout << (string)buffer << endl;
    // cout << "ENDDEBUG: ResponseReceive()\n";
    if (valread <= 0) {
        // ChatView::ChangeBarText("false");
        return false;
    }
    // ChatView::ChangeBarText((string)buffer);
    return true;
}

void ChatService::HandleReceive(int sock, char* buffer) {
    while(!exitApp) {
        if (!ResponseReceive(sock, buffer)) {
            reconnect = true;
            close(sock);
            if (!exitApp) {
                // ChatView::ChangeBarText("Caught!");
                sock = handleConnect(ADDRESS, PORT);
                nlohmann::json backupJson = nlohmann::json::object();
                UserClient backupUser = ChatView::getUser();
                ChatClient backupJoinedRoom = ChatView::getJoinedRoom();
                backupJson.push_back({"userName", backupUser.getName()});
                backupJson.push_back({"userPassword", backupUser.getPassword()});
                backupJson.push_back({"roomId", backupJoinedRoom.getId()});
                string backupString = backupJson.dump();
                RequestSend("RECONNECT|" + backupString, sock);
            }
        } else {
            HandlePattern(buffer, sock);
        }
    }
}

bool ChatService::ConvertToBool(string s) {
    bool b;
    std::istringstream(s) >> b;
    return b;
}

void ChatService::touchUpdate(bool F) {
    checkingUpdate = F;
}

bool ChatService::joinRoom(int sock, string id, ChatClient& room) {
    RequestSend("JOIN_ROOM|" + id, sock);
    string response = GetValueFromServer(sock, "JOIN_ROOM");
    if (response.length() == 0) {
        return false;
    } else {
        nlohmann::json j = nlohmann::json::parse(response);
        room.setId(j.at("roomId"));
        room.setRoomName(j.at("roomName"));
        return true;
    }
}

bool ChatService::joinPrivateRoom(int sock, string user_id, ChatClient& room){
    RequestSend("JOIN_ROOM_PRIVATE|"+ user_id, sock);
    string response = GetValueFromServer(sock, "JOIN_ROOM_PRIVATE");
    sleep(5);
    if(response.length() != 0) {
        nlohmann::json j = nlohmann::json::parse(response);
        room.setId(j.at("roomId"));
        room.setRoomName(j.at("roomName"));
        return true;
    }
    return false;
}

 map<int, ChatClient> ChatService::GetListRoom(int sock, bool& isSuccess, int& count) {
    //sua tu day
    map<int, ChatClient> foundRoom;
    RequestSend("GET_LIST_ROOM|", sock);
    string response = GetValueFromServer(sock, "GET_LIST_ROOM");
    if (response.length() == 0) {
        isSuccess = false;
    } else {
        isSuccess = true;
        nlohmann::json j = nlohmann::json::parse(response);
        for (auto i: j) {
            int roomId = i.at("roomId");
            string roomName = i.at("roomName");
            ChatClient newChat(roomId,roomName);
            foundRoom[++count] = newChat;
        }
    }
    return foundRoom;
}

void ChatService::GetListPrivateRoom(int sock, vector<ChatClient>& listChatRooms, bool& isSuccess){
    listChatRooms.clear();
    RequestSend("GET_LIST_PRIVATE_ROOM|", sock);
    string response = GetValueFromServer(sock, "GET_LIST_PRIVATE_ROOM");
    //ChatView::ChangBarText(response);
    if (response.length() == 0){
        isSuccess = false;
    }
    else{
        isSuccess = true;
        nlohmann::json j = nlohmann::json::parse(response);
        ChatClient room;
        for(auto i : j){
            room.setCountUnreadMessage(i.at("messageCount"));
            room.setId(i.at("roomId"));
            room.setRoomName(i.at("roomName"));
            listChatRooms.push_back(room);
        }
    }
}

void ChatService::GetListChat(int sock, ChatClient& room) {
    room.clearMessage();
    int roomId = room.getId();
    stringstream ss;
    ss << roomId;
    string str = ss.str();
    RequestSend("GET_LIST_CHAT|" + str, sock);
    string response = GetValueFromServer(sock, "GET_LIST_CHAT");
    if (response.length() != 2) {
        nlohmann::json j = nlohmann::json::parse(response);
        for (auto i: j) {
            UserClient sender;
            sender.setName(i.at("userName"));
            MessageClient message(i.at("id") ,sender, i.at("content"));
            room.addMessage(message);
        }
    }
}

map<int, UserClient> ChatService::GetFoundUser(int sock, UserClient user, int& count, WINDOW *finduserWin) {
    map<int, UserClient> foundUser;
    string response = GetValueFromServer(sock, "FIND_USER");
    int choice;
    int row = 2;
    int col = 2;
    if (response.length() != 0) {
        nlohmann::json j = nlohmann::json::parse(response);
        for (auto i: j) {
            int id = i.at("userId");
            string name = i.at("userName");
            if (name != user.getName()) {
                UserClient newUser(id, name);
                foundUser[++count] = newUser;
                //cout << count << ". " << name << endl;
                //mvwprintw(finduserWin, counter, 2,"%d. %s", count,name);
                mvwprintw(finduserWin, row, col,"%d .%s" ,count,name.c_str());
                wrefresh(finduserWin);
                row++;
            }
            if(row == 11){
                row = 2;
                col = col + 20;
            }
        }
        mvwprintw(finduserWin, row++, 2,"%d .%s" ,++count,"Exit");
        wrefresh(finduserWin);
    } else {
        //cout << "No result matches your search!\n";
        mvwprintw(finduserWin, row, 2,"No result matches your search!");
        wrefresh(finduserWin);
        ChatView::handleMenu(sock, 102);
    }
    return foundUser;
}


map<int, UserClient> ChatService::GetListUser(ChatClient joinedRoom,int sock,WINDOW *GetListUserWin, int& counter,UserClient user) {
    //int count = 0;
    int row = 1;
    int col = 1;
    map<int, UserClient> User;
    string response = GetValueFromServer(sock, "LIST_USER");
    if (response.length() != 0) {
        nlohmann::json j = nlohmann::json::parse(response);
        //ChatView::ChangeBarText(response);
        int count = 0;
        for (auto i:j ) {
            int id = i.at("memberId");
            string name = i.at("memberName");
            UserClient newUser(id, name);
            if (name != user.getName()){
                User[++counter] = newUser;
            }
            mvwprintw(GetListUserWin, row++, col, "%d. %s" , counter, name.c_str());
            wrefresh(GetListUserWin);
            if(row == 8){
                row = 1;
                col = col + 20;
            }
        }
        ++counter;
    }
    return User;
}

void ChatService::GetCurrentProfile(int sock, UserClient& user) {
    string response = GetValueFromServer(sock, "UPDATE_PROFILE");
    nlohmann::json j = nlohmann::json::parse(response);
    if (j.at("address").is_null()) {
        j.at("address") = to_string(j.at("address"));
    }
    if (j.at("gender").is_null()) {
        j.at("gender") = to_string(j.at("gender"));
    }
    user.setAddress(j.at("address"));
    user.setGender(j.at("gender"));
}

void ReconnectStatus() {
    string notification;
    WINDOW* win = ChatView::getCurrentWin();
    int y, x;
    getyx(win, y, x);
    int count = 0;
    while (reconnect) {
        sleep(1);
        if (count == 0) {
            notification = "Waiting for connection.  ";
            count ++;
        } else if (count == 1) {
            notification = "Waiting for connection.. ";
            count ++;
        } else if (count == 2) {
            notification = "Waiting for connection...";
            count = 0;
        }
        ChatView::ChangeBarText(notification);
        wmove(win, y, x);
    }
    ChatView::ResetBar(notification.length());
    wmove(win, y, x);
}

int ChatService::handleConnect(const char* address, int port) {
    int sock = 0;
    if ((sock = socket(AF_INET, SOCK_STREAM, 0)) < 0) {
        // cout << "\nSocket creation error";
        return -1;
    }
    struct sockaddr_in serv_addr;
    serv_addr.sin_family = AF_INET;
    serv_addr.sin_port = htons(port);
    if (inet_pton(AF_INET, address, &serv_addr.sin_addr) <= 0) {
        // cout << "\nInvalid address/ Address not supported";
        return -1;
    }
    thread changeStatusBar;
    if (reconnect) {
        changeStatusBar = thread(ReconnectStatus);
    }
    int connection;
    while (1) {
        connection = connect(sock, (struct sockaddr *)&serv_addr, sizeof(serv_addr));
        if (connection >= 0) {
            if (reconnect) {
                reconnect = false;
                changeStatusBar.join();
            }
            break;
        }
    }
    return sock;
}

void ChatService::exitAppChat(int sock) {
    RequestSend("USER_EXIT_APP|", sock);
    exitApp = true;
}

void ChatService::GetUserProfile(int sock, UserClient& user,WINDOW *OrtherUserProfileWin) {
    stringstream ss;
    ss << user.getId();
    string str = ss.str();
    ChatService::RequestSend("USER_PROFILE|"+ str, sock);
    string response = GetValueFromServer(sock, "USER_PROFILE");
    nlohmann::json j = nlohmann::json::parse(response);
    if (j.at("userName").is_null()) {
        j.at("userName") = to_string(j.at("userName"));
    }
    if (j.at("address").is_null()) {
        j.at("address") = to_string(j.at("address"));
    }
    if (j.at("gender").is_null()) {
        j.at("gender") = to_string(j.at("gender"));
    }
    user.setName(j.at("userName"));
    user.setAddress(j.at("address"));
    user.setGender(j.at("gender"));
    //mvwprintw(OrtherUserProfileWin, 15, 1,"hello");
    mvwprintw(OrtherUserProfileWin, 8, 1,"%s :%s","Name " , user.getName().c_str());
    mvwprintw(OrtherUserProfileWin, 9, 1,"%s :%s","Address ", user.getAddress().c_str());
    mvwprintw(OrtherUserProfileWin, 10, 1,"%s :%s","Gender ", user.getGender().c_str());
    wrefresh(OrtherUserProfileWin);
}

bool ChatService::HandleName(string username){
    if(username.length() < 2){
        return false;
    }
    for(int i = 0; i <= username.length(); i++){
        if(isspace(username[i]) ) {
            if(isspace(username[i+1])){
                return false;
            }
        }
    }
    for(int i = 0; i < username.length(); i++){
        if((username[i] >= 'A' && username[i] <= 'Z') || (username[i] >= 'a' && username[i] <= 'z') || (username[i] ==' ')){
            continue;
        }
        return false;
    }
    return true;
}

bool ChatService::HandlePassword(string password){
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