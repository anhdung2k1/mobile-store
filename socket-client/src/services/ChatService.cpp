// Copyright [2024] <Anh Dung>
#define PORT 8000
#define ADDRESS "172.17.0.5"

#include "ChatService.h"

bool exitApp = false;
bool reconnect = false;
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
    bzero(buffer, buffer_size);
    int valread = recv(sock, buffer, buffer_size, 0);
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

bool ChatService::ConvertToBool(string s) {
    if (s == "true" || s == "1" || s == "yes" || s == "y" || s == "True" || s == "YES" || s == "Y") {
        return true;
    } else if (s == "false" || s == "0" || s == "no" || s == "n" || s == "False" || s == "NO" || s == "N") {
        return false;
    } else {
        // Handle invalid input
        // You might throw an exception, return a default value, or handle it in another way
        // For simplicity, let's return false for any unrecognized input
        return false;
    }
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
    user.setName(j.at("userName").get<string>());
    user.setAddress(j.at("address").get<string>());
    user.setGender(j.at("gender").get<string>());
 
    mvwprintw(OrtherUserProfileWin, 8, 1, "%s: %s", "Name", user.getName().c_str());
    mvwprintw(OrtherUserProfileWin, 9, 1, "%s: %s", "Address", user.getAddress().c_str());
    mvwprintw(OrtherUserProfileWin, 10, 1, "%s: %s", "Gender", user.getGender().c_str());
    wrefresh(OrtherUserProfileWin);
}

Mobile ChatService::GetMobileInformation(int sock, int mobileId)
{
    string str = to_string(mobileId);
    ChatService::RequestSend("MOBILE_INFORMATION|" + str, sock);
    string response = ChatService::GetValueFromServer(sock, "MOBILE_INFORMATION");
    nlohmann::json j = nlohmann::json::parse(response);
    Mobile mb(
        j.at("mobileID").get<int>(),
        j.at("mobileName").get<string>(),
        j.at("mobileModel").get<string>(),
        j.at("mobileType").get<string>(),
        j.at("mobileQuantity").get<int>(),
        j.at("mobilePrice").get<string>(),
        j.at("mobileDescription").get<string>()
    );
    return mb;
}

// Return two values [map<int, int> idMapping, vector<Mobile>mobile]
map<int, int> ChatService::FindInventoryName(int sock, vector<Mobile> &mobile, string input, string pattern)
{
    mobile.clear();
    int idx = 1;
    map<int, int> idMapping; // key: mobileID, value: index
    ChatService::RequestSend(pattern + "|" + input, sock);
    string response = ChatService::GetValueFromServer(sock, pattern);
    if (response.length() > 0) {
        nlohmann::json j = nlohmann::json::parse(response);
        mvprintw(5, 5, "%s", "Mobile Name");
        mvprintw(5, 40, "%s", "Mobile Type");
        mvprintw(5, 60, "%s", "Mobile Model");
        mvprintw(5, 80, "%s", "Mobile Quantity");
        mvprintw(5, 100, "%s", "Mobile Price");
        mvprintw(5, 120, "%s", "Mobile Description");

        for (auto it : j) {
            idMapping[it.at("mobileID").get<int>()] = idx++;
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
    return idMapping;
}

map<int, int> ChatService::FindTransactionHistory(int sock, vector<Transaction> &transaction, string input) {
    transaction.clear();
    int idx = 1;
    map<int, int> idTransMapping;
    ChatService::RequestSend("FIND_TRANSACTION_HISTORY|" + input, sock);
    string response = ChatService::GetValueFromServer(sock, "FIND_TRANSACTION_HISTORY");
    if(response.length() > 0) {
        nlohmann::json j = nlohmann::json::parse(response);
        mvprintw(5, 5, "%s", "Transaction Name");
        mvprintw(5, 40, "%s", "Transaction Type");
        mvprintw(5, 80, "%s", "Payment Method");
        
        for(auto tr : j) {
            idTransMapping[tr.at("transactionID").get<int>()] = idx++;
            Transaction trans(
                tr.at("transactionID").get<int>(),
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
    return idTransMapping; 
}

map<int, int> ChatService::GetTransactionHistoryWithCustomerId(int sock, vector<Transaction> &transaction, int customerId) {
    transaction.clear();
    int idx = 1;
    map<int, int> idTransMapping;
    ChatService::RequestSend("GET_TRANSACTION_HISTORY_WITH_CUSTOMER_ID|" + to_string(customerId), sock);
    string response = ChatService::GetValueFromServer(sock, "GET_TRANSACTION_HISTORY_WITH_CUSTOMER_ID");
    if (response.length() > 0) {
        nlohmann::json j = nlohmann::json::parse(response);
        for(auto tr : j) {
            idTransMapping[tr.at("transactionID").get<int>()] = idx++;
            Transaction trans(
                tr.at("transactionID").get<int>(),
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
    return idTransMapping;
}

Transaction ChatService::GetTransactionInformation(int sock, int transactionId) {
    ChatService::RequestSend("GET_TRANSACTION_INFORMATION|" + to_string(transactionId), sock);
    string response = ChatService::GetValueFromServer(sock, "GET_TRANSACTION_INFORMATION");
    nlohmann::json j = nlohmann::json::parse(response);
    Transaction transaction(
        j.at("transactionID").get<int>(),
        j.at("transactionName").get<string>(),
        j.at("transactionType").get<string>(),
        j.at("paymentMethod").get<string>()
    );
    return transaction;
}

void ChatService::CreateTransaction(int sock, Transaction& transaction, int customerId) {
    stringstream formData;
    formData << "{\"transactionName\": "
             << "\"" + transaction.getTransactionName() + "\", "
             << "\"transactionType\": "
             << "\"" + transaction.getTransactionType() + "\", "
             << "\"paymentMethod\": "
             << "\"" + transaction.getPaymentMethod() + "\", "
             << "\"customerId\": "
             << "\"" + to_string(customerId) + "\"}";
    ChatService::RequestSend("CREATE_TRANSACTION|" + formData.str(), sock);
    string response = ChatService::GetValueFromServer(sock, "CREATE_TRANSACTION");
    if (ConvertToBool(response)) {
        mvprintw(24, 3, "%s", "Transaction has been created successfully!!");
    }
    else {
        mvprintw(24, 3, "%s", "Failed to created Transaction !!");
    }
}

void ChatService::UpdateTransaction(int sock, Transaction& transaction) {
    stringstream formData;
    formData << "{\"transactionName\": "
             << "\"" + transaction.getTransactionName() + "\", "
             << "\"transactionType\": "
             << "\"" + transaction.getTransactionType() + "\", "
             << "\"paymentMethod\": "
             << "\"" + transaction.getPaymentMethod() + "\"}";
    ChatService::RequestSend("UPDATE_TRANSACTION|" + formData.str(), sock);
    string response = ChatService::GetValueFromServer(sock, "UPDATE_TRANSACTION");
    if(ChatService::ConvertToBool(response)) {
        mvprintw(24, 3, "%s", "Transaction has been updated successfully!!");
    } else {
        mvprintw(24, 3, "%s", "Failed to updated Transaction !!");
    }
}

bool ChatService::DeleteTransaction(int sock, int transactionId) {
    ChatService::RequestSend("DELETE_TRANSACTION|" + to_string(transactionId), sock);
    string response = ChatService::GetValueFromServer(sock, "DELETE_TRANSACTION");
    bool isDeleted = ChatService::ConvertToBool(response) ? true : false;
    return isDeleted;
}

//Create new Mobile Device
void ChatService::CreateMobileDevice(int sock, Mobile& mobile) {
    stringstream formData;
    formData << "{\"mobileName\": "
             << "\"" + mobile.getMobileName() + "\", " 
             << "\"mobileType\": "
             << "\"" + mobile.getMobileType() + "\", " 
             << "\"mobileModel\": "
             << "\"" + mobile.getMobileModel() + "\", "
             << "\"mobileQuantity\": "
             << "" + to_string(mobile.getMobileQuantity()) + ", "
             << "\"mobilePrice\": "
             << "\"" + mobile.getMobilePrice() + "\", "
             << "\"mobileDescription\": "
             << "\"" + mobile.getMobileDescription() + "\"}";

    ChatService::RequestSend("CREATE_MOBILE_DEVICE|" + formData.str(), sock);
    string response = ChatService::GetValueFromServer(sock, "CREATE_MOBILE_DEVICE");
    if(ChatService::ConvertToBool(response)) {
        mvprintw(24, 3, "%s", "Mobile has been created successfully!!");
    } else {
        mvprintw(24, 3, "%s", "Failed to created mobile device !!");
    }
}

void ChatService::UpdateMobileDevice(int sock, Mobile& mobile) {
    stringstream formData;
    formData << "{\"mobileName\": "
             << "\"" + mobile.getMobileName() + "\", " 
             << "\"mobileType\": "
             << "\"" + mobile.getMobileType() + "\", " 
             << "\"mobileModel\": "
             << "\"" + mobile.getMobileModel() + "\", "
             << "\"mobileQuantity\": "
             << "" + to_string(mobile.getMobileQuantity()) + ", "
             << "\"mobilePrice\": "
             << "\"" + mobile.getMobilePrice() + "\", "
             << "\"mobileDescription\": "
             << "\"" + mobile.getMobileDescription() + "\"}";

    ChatService::RequestSend("UPDATE_MOBILE_DEVICE|" + formData.str(), sock);
    string response = ChatService::GetValueFromServer(sock, "UPDATE_MOBILE_DEVICE");
    if(ChatService::ConvertToBool(response)) {
        mvprintw(24, 3, "%s", "Mobile has been updated successfully!!");
    } else {
        mvprintw(24, 3, "%s", "Failed to updated mobile device !!");
    }
}

bool ChatService::DeleteMobileDevice(int sock, int mobileId) {
    ChatService::RequestSend("DELETE_MOBILE_DEVICE|" + to_string(mobileId), sock);
    string response = ChatService::GetValueFromServer(sock, "DELETE_MOBILE_DEVICE");
    bool isDeleted = ChatService::ConvertToBool(response) ? true : false;
    return isDeleted;
}

map<int, int> ChatService::FindCustomerName(int sock, vector<Customer>& customers, string input) {
    customers.clear();
    int idx = 1;
    map<int, int> idMapping;
    ChatService::RequestSend("FIND_CUSTOMER_NAME|" + input, sock);
    string response = ChatService::GetValueFromServer(sock, "FIND_CUSTOMER_NAME");
    if(response.length() > 0) {
        nlohmann::json j = nlohmann::json::parse(response);
        mvprintw(5, 5, "%s", "Customer Name");
        mvprintw(5, 30, "%s", "Customer Gender");
        mvprintw(5, 60, "%s", "Customer Address");
        mvprintw(5, 100, "%s", "Customer Birthday");
        mvprintw(5, 120, "%s", "Customer Email");
        
        for(auto it : j) {
            idMapping[it.at("customerID").get<int>()] = idx++;
            Customer cust(
                it.at("customerID").get<int>(),
                it.at("customerName").get<string>(),
                it.at("customerAddress").get<string>(),
                it.at("customerGender").get<string>(),
                it.at("customerBirthDay").get<string>(),
                it.at("customerEmail").get<string>()
            ); 
            customers.push_back(cust);
        }
    }
    else {
        mvprintw(5, 20, "%s", "Could not found any customer!!");
    }
    return idMapping;
}

Customer ChatService::GetCustomerInformation(int sock, int customerId) {
    ChatService::RequestSend("GET_CUSTOMER_INFORMATION|" + to_string(customerId), sock);
    string response = ChatService::GetValueFromServer(sock, "GET_CUSTOMER_INFORMATION");
    nlohmann::json j = nlohmann::json::parse(response);
    Customer customer(
        j.at("customerID").get<int>(),
        j.at("customerName").get<string>(),
        j.at("customerAddress").get<string>(),
        j.at("customerGender").get<string>(),
        j.at("customerBirthDay").get<string>(),
        j.at("customerEmail").get<string>()
    );
    return customer;
}

void ChatService::CreateCustomer(int sock, Customer& customer) {
    stringstream formData;
    formData << "{\"customerName\": "
             << "\"" + customer.getCustomerName() + "\", " 
             << "\"customerAddress\": "
             << "\"" + customer.getCustomerAddress() + "\", " 
             << "\"customerGender\": "
             << "\"" + customer.getCustomerGender() + "\" , "
             << "\"customerBirthDay\": "
             << "\"" + customer.getCustomerBirthday() + "\", "
             << "\"customerEmail\": "
             << "\"" + customer.getCustomerEmail() + "\"}";

    ChatService::RequestSend("CREATE_CUSTOMER|" + formData.str(), sock);
    string response = ChatService::GetValueFromServer(sock, "CREATE_CUSTOMER");
    if(ChatService::ConvertToBool(response)) {
        mvprintw(24, 3, "%s", "Customer has been created successfully!!");
    } else {
        mvprintw(24, 3, "%s", "Failed to created customer!!");
    }
}
void ChatService::UpdateCustomer(int sock, Customer& customer) {
    stringstream formData;
    formData << "{\"customerName\": "
             << "\"" + customer.getCustomerName() + "\", " 
             << "\"customerAddress\": "
             << "\"" + customer.getCustomerAddress() + "\", " 
             << "\"customerGender\": "
             << "\"" + customer.getCustomerGender() + "\" , "
             << "\"customerBirthDay\": "
             << "\"" + customer.getCustomerBirthday() + "\", "
             << "\"customerEmail\": "
             << "\"" + customer.getCustomerEmail() + "\"}";

    ChatService::RequestSend("UPDATE_CUSTOMER|" + formData.str(), sock);
    string response = ChatService::GetValueFromServer(sock, "UPDATE_CUSTOMER");
    if(ChatService::ConvertToBool(response)) {
        mvprintw(24, 3, "%s", "Customer has been updated successfully!!");
    } else {
        mvprintw(24, 3, "%s", "Failed to created customer!!");
    }
}

bool ChatService::DeleteCustomer(int sock, int customerId) {
    ChatService::RequestSend("DELETE_CUSTOMER|" + to_string(customerId), sock);
    string response = ChatService::GetValueFromServer(sock, "DELETE_CUSTOMER");
    bool isDeleted = ChatService::ConvertToBool(response) ? true : false;
    return isDeleted;
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

// Check the account logged in is administrator
bool ChatService::IsAdminAuthenticated(int sock) {
    ChatService::RequestSend("ADMIN_ACCOUNT|", sock);
    string response = ChatService::GetValueFromServer(sock, "ADMIN_ACCOUNT");
    return ConvertToBool(response);
}

bool ChatService::DeleteAccountUser(int sock, int userId) {
    ChatService::RequestSend("DELETE_ACCOUNT|" + to_string(userId), sock);
    string response = ChatService::GetValueFromServer(sock, "DELETE_ACCOUNT");
    return ChatService::ConvertToBool(response);
}