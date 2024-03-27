// Copyright [2024] <Anh Dung>
#include "ChatView.h"
#include <chrono>
#include <iomanip>
#include "locale.h"
#include "ncurses.h"

using std::thread;
using std::vector;
using std::cout;
using std::cin;
using std::endl;
using std::string;

#define ctrl(x) (x & 0x1F)
bool endInput;
thread readThread;
ChatView::View currentView;
UserClient currentUser;
StatusBar statusBar;
ChatClient joinedRoom;
string addr = ChatService::GetAddress();
const char* ADDRESS = addr.c_str();
const int PORT = ChatService::GetPort();
int rowRoom;
int colRoom;
int numRoom;
WINDOW *currentWin;
bool atTop = false;
bool isEmptyChat = true;
map<int, ChatClient> foundRoom;

void ChatView::appendListRoom(ChatClient newRoom) {
   foundRoom[numRoom] = newRoom;
}

bool ChatView::GetIsEmptyChat() {
   return isEmptyChat;
}

void ChatView::scrollScreenDownWithAction(WINDOW *win, int limit, string message) {
   char str[win->_maxx];
   for(int i = limit - 1; i >= 4; i--) {
      bzero(str, win->_maxx);
      mvwinstr(win, i-1, 3, str);
      mvwaddstr(win, i, 3, str);
   }
   mvwaddstr(win, 3, 3, message.c_str());
}

void ChatView::scrollScreenUpWithAction(WINDOW *win, int limit, string message) {
   char str[win->_maxx];
   for(int i = 3; i < limit - 1; i++) {
      bzero(str, win->_maxx);
      mvwinstr(win, i+1, 3, str);
      mvwaddstr(win, i, 3, str);
   }
   mvwaddstr(win, limit - 1, 3, message.c_str());
}

void ChatView::scrollScreenUp(int& count, WINDOW *win, int limit, string& tempMsg) {
   char str[win->_maxx];
   if (count == limit) {
      count--;
      bzero(str, win->_maxx);
      mvwinstr(win, 3, 3, str);
      tempMsg = (string)str;
      ChatService::pushStackUp((string)str);
      for(int i = 3; i < count; i++) {
         bzero(str, win->_maxx);
         mvwinstr(win, i+1, 3, str);
         mvwaddstr(win, i, 3, str);
      }
      wmove(win, count, 3);
      wclrtoeol(win);
   }
}

void ChatView::scrollScreenDown(int& count, WINDOW *win, int limit, string message) {
   char str[win->_maxx];
   if (count == limit) {
      count++;
      for(int i = limit; i >= 4; i--) {
         bzero(str, win->_maxx);
         mvwinstr(win, i-1, 3, str);
         mvwaddstr(win, i, 3, str);
      }
      mvwaddstr(win, 3, 3, message.c_str());
   }
   // wmove(win, limit, 3);
   // wclrtoeol(win);
}

void ChatView::displayMyMessage(string content, WINDOW* win, int& y, int limit, string& tempMsg) {
   int maxLengthMsg = win->_maxx*2 / 3;
   string smallContent;
   if (content.length() / maxLengthMsg > 1 || (content.length() / maxLengthMsg == 1 && content.length() % maxLengthMsg != 0)) {
      for (int i = 0; i <= content.length() / maxLengthMsg; i++) {
         if (i != content.length() / maxLengthMsg) {
            smallContent = content.substr(i * maxLengthMsg, maxLengthMsg);
            scrollScreenUp(y, win, limit, tempMsg);
            mvwaddstr(win, y++, win->_maxx - smallContent.length() - 3, smallContent.c_str());
         } else {
            smallContent = content.substr(i * maxLengthMsg);
            scrollScreenUp(y, win, limit, tempMsg);
            mvwaddstr(win, y++, win->_maxx - smallContent.length() - 3, smallContent.c_str());
         }
      }
   } else {
      scrollScreenUp(y, win, limit, tempMsg);
      // string icon = "✔";
      mvwaddstr(win, y++, win->_maxx - content.length() - 3, content.c_str());
      // mvwaddstr(win, y++, win->_maxx - 3, icon.c_str());
   }
}

void ChatView::displayInform(string message, WINDOW* win, int& y, int limit, string& tempMsg) {
   scrollScreenUp(y, win, limit, tempMsg);
   mvwaddstr(win, y++, (win->_maxx / 2) - (message.length()/2), message.c_str());
}

void ChatView::displayOrtherMessage(string message, WINDOW* win, int& y, int limit, string& tempMsg) {
   int maxLengthMsg = win->_maxx*2 / 3;
   int pos = message.find(":");
   string smallContent;
   string userName = message.substr(0, pos + 1);
   string content = message.substr(pos + 2);
   scrollScreenUp(y, win, limit, tempMsg);
   mvwaddstr(win, y, 3, userName.c_str());
   if (content.length() / maxLengthMsg > 1 || (content.length() / maxLengthMsg == 1 && content.length() % maxLengthMsg != 0)) {
      for (int i = 0; i <= content.length() / maxLengthMsg; i++) {
         if (i != content.length() / maxLengthMsg) {
            smallContent = content.substr(i * maxLengthMsg, maxLengthMsg);
            scrollScreenUp(y, win, limit, tempMsg);
            mvwaddstr(win, y++, userName.length() + 3, smallContent.c_str());
         } else {
            smallContent = content.substr(i * maxLengthMsg);
            scrollScreenUp(y, win, limit, tempMsg);
            mvwaddstr(win, y++, userName.length() + 3, smallContent.c_str());
         }
      }
   } else {
      mvwaddstr(win, y++, userName.length() + 3, content.c_str());
   }
}

void ChatView::displayMyMessageAtTop(string content, WINDOW* win, int& y, int limit) {
   int maxLengthMsg = win->_maxx*2 / 3;
   string smallContent;
   if (content.length() / maxLengthMsg > 1 || (content.length() / maxLengthMsg == 1 && content.length() % maxLengthMsg != 0)) {
      for (int i = content.length() / maxLengthMsg; i >= 0; i--) {
         if (i != content.length() / maxLengthMsg) {
            smallContent = content.substr(i * maxLengthMsg, maxLengthMsg);
         } else {
            smallContent = content.substr(i * maxLengthMsg);
         }
         scrollScreenDownAtTop(win, limit);
         wmove(win, y, 3);
         wclrtoeol(win);
         mvwaddstr(win, y, win->_maxx - smallContent.length() - 3, smallContent.c_str());
      }
   } else {
      scrollScreenDownAtTop(win, limit);
      wmove(win, y, 3);
      wclrtoeol(win);
      mvwaddstr(win, y, win->_maxx - content.length() - 3, content.c_str());
   }
}

void ChatView::displayOrtherMessageAtTop(string message, WINDOW* win, int& y, int limit) {
   int maxLengthMsg = win->_maxx*2 / 3;
   int pos = message.find(":");
   string smallContent;
   string userName = message.substr(0, pos + 1);
   string content = message.substr(pos + 2);
   if (content.length() / maxLengthMsg > 1 || (content.length() / maxLengthMsg == 1 && content.length() % maxLengthMsg != 0)) {
      for (int i = content.length() / maxLengthMsg; i >= 0; i--) {
         scrollScreenDownAtTop(win, limit);
         wmove(win, y, 3);
         wclrtoeol(win);
         if (i == content.length() / maxLengthMsg) {
            smallContent = content.substr(i * maxLengthMsg);
         } else if (i == 0) {
            mvwaddstr(win, y, 3, userName.c_str());
            smallContent = content.substr(i * maxLengthMsg, maxLengthMsg);
         } else {
            smallContent = content.substr(i * maxLengthMsg, maxLengthMsg);
         }
         mvwaddstr(win, y, userName.length() + 3, smallContent.c_str());
      }
   } else {
      scrollScreenDownAtTop(win, limit);
      mvwaddstr(win, y, 3, userName.c_str());
      wmove(win, y, userName.length() + 3);
      wclrtoeol(win);
      mvwaddstr(win, y, userName.length() + 3, content.c_str());
   }
}

void ChatView::scrollScreenDownAtTop(WINDOW* win, int limit) {
   char str[win->_maxx];
   bzero(str, win->_maxx);
   mvwinstr(win, limit, 3, str);
   ChatService::pushStackDown((string)str);
   for(int i = limit; i >= 4; i--) {
      bzero(str, win->_maxx);
      mvwinstr(win, i-1, 3, str);
      mvwaddstr(win, i, 3, str);
   }
}

void ChatView::displayMsgAtTop(MessageClient message, WINDOW* win, int limit) {
   curs_set(0);
   int topPos = 3;
   string userName, content, smallContent;
   int maxLengthMsg = win->_maxx*2 / 3;
   if(message.getSender().getName() != currentUser.getName()) {
      string ortherMessage = message.getSender().getName() + ": " + message.getText();
      displayOrtherMessageAtTop(ortherMessage, win, topPos, limit);
   } else {
      displayMyMessageAtTop(message.getText(), win, topPos, limit);
   }
   int x, y;
   getyx(win, y, x);
   curs_set(1);
   wmove(win, y, x);
}

void ChatView::displayMessage(MessageClient message, WINDOW* win, int& y) {
   string userName, content, smallContent;
   string tempMsg;
   int maxLengthMsg = win->_maxx*2 / 3;
   int limit = win->_maxy - 1;
   if(message.getSender().getName() != currentUser.getName()) {
      string ortherMessage = message.getSender().getName() + ": " + message.getText();
      displayOrtherMessage(ortherMessage, win, y, limit, tempMsg);
   } else {
      displayMyMessage(message.getText(), win, y, limit, tempMsg);
   }
}

void ChatView::displayChat(ChatClient chat, WINDOW* win, int& count) {
   vector<MessageClient> messages = chat.getMessage();
   count = 3;
   if (messages.size() != 0) {
      isEmptyChat = false;
      ChatService::setLatestId(messages.front().getId());
      for(auto& message : messages) {
         displayMessage(message, win, count);
      }
   }
}

void ChatView::UI() {
   char buffer[1024];
   int sock = ChatService::handleConnect(ADDRESS, PORT);
   readThread = thread(ChatService::HandleReceive, sock, buffer);
   ChatService::RequestSend("ACTIVATE_THREAD", sock);
   while(true) {
      ChatView::handleLogin(sock, -1);
   }
}

void ChatView::LoginMenu() {
   currentView = Login;
   initscr();
   keypad(stdscr, TRUE);
   cbreak();
   if(has_colors() == FALSE)
   {
      clear();
      endwin();
      printf("Your terminal does not support color\n");
      exit(1);
   }
   start_color();
   init_pair(1, COLOR_YELLOW, COLOR_BLACK);
   init_pair(2, COLOR_RED, COLOR_BLACK);
   init_pair(3, COLOR_GREEN, COLOR_BLACK);
   init_pair(4, COLOR_CYAN, COLOR_BLACK);
   init_pair(5, COLOR_WHITE, COLOR_BLACK);
   noecho();
   refresh();
   statusBar.setParent(stdscr);
   int height, width, start_y, start_x;
   height = 13;
   width = 41;
   start_y = start_x = 0;

   WINDOW * loginMenu = newwin(height, width, start_y, start_x);
   currentWin = loginMenu;
   wattron(loginMenu, COLOR_PAIR(3));
   box(loginMenu, 0, 0);
   mvwprintw(loginMenu, 4, 2, "-------------------------------------");
   wattroff(loginMenu, COLOR_PAIR(3));
   curs_set(0);
   refresh();
   wattron(loginMenu, COLOR_PAIR(4));
   mvwprintw(loginMenu, 2, (loginMenu->_maxx - 24) / 2, ">>>>====Messager====<<<<");
   mvwprintw(loginMenu, 6, 3, "Register");
   mvwprintw(loginMenu, 8, 3, "Login");
   mvwprintw(loginMenu, 6, (loginMenu->_maxx - 12), "(Press R)");
   mvwprintw(loginMenu, 8, (loginMenu->_maxx - 12), "(Press L)");
   wattroff(loginMenu, COLOR_PAIR(4));
   wattron(loginMenu, COLOR_PAIR(2));
   mvwprintw(loginMenu, 10, 3, "Exit");
   mvwprintw(loginMenu, 10, (loginMenu->_maxx - 14), "(Press Esc)");
   wattroff(loginMenu, COLOR_PAIR(2));
   wrefresh(loginMenu);
}

void sigHandlerView(int signal) {
   endInput = true;
}

void ChatView::handleLogin(int sock, int selection) {
   int n;
   int test;
   LoginMenu();
   // sleep(3);
   // ChatView::ChangBarText("...");
   // cout << "\033[1;32m\nSelect your choice: \033[0m";
   n = (selection != -1 ? selection: getch());
   printw("%d", n);
   noecho();

   switch(n) {
      case 114: // 114 is R key
      {
         clear();
         endwin();
         currentView = LoginInput;
         std::system ("clear");
         string username;
         string password;
         //sendrequest with partern for register
         bool isSuccess = false;
         bool isUserAvailable = true;
         //PDHM
         std::string input;
         // Start regigter window design
         initscr();
         keypad(stdscr, TRUE);
         cbreak();
         if(has_colors() == FALSE)
         {
            clear();
            endwin();
            printf("Your terminal does not support color\n");
            exit(1);
         }
         start_color();
         init_pair(1, COLOR_YELLOW, COLOR_BLACK);
         init_pair(2, COLOR_RED, COLOR_BLACK);
         init_pair(3, COLOR_GREEN, COLOR_BLACK);
         init_pair(4, COLOR_CYAN, COLOR_BLACK);
         init_pair(5, COLOR_WHITE, COLOR_BLACK);
         noecho();

         // Format the register window
         int height, width, start_x = 0, start_y = 0, boxStyle_x = 0, boxStyle_y = 0;
         height = 25;
         width = 70;
         WINDOW *registerWin = newwin(height, width, start_x, start_y);
         WINDOW *nameWin = subwin(registerWin, 3, 56, 6, 12);
         WINDOW *passWin = subwin(registerWin, 3, 56, 11, 12);
         wattron(registerWin, COLOR_PAIR(3));
         box(registerWin, boxStyle_x, boxStyle_y);
         mvwprintw(registerWin, 5, 2, "------------------------------------------------------------------");
         mvwprintw(registerWin, 17, 2, "------------------------------------------------------------------");
         wattroff(registerWin, COLOR_PAIR(3));
         wattron(nameWin, COLOR_PAIR(5));
         box(nameWin, 0, 0);
         wattroff(nameWin, COLOR_PAIR(5));
         wattron(passWin, COLOR_PAIR(5));
         box(passWin, 0, 0);
         wattroff(passWin, COLOR_PAIR(5));
         currentWin = registerWin;
         refresh();

         int curs_x, curs_y;
         // WINDOW *usernameWin = subwin(registerWin, 3, 40, 5, 14);
         // box(usernameWin, 0, 0);
         // wrefresh(registerWin);
         wattron(registerWin, COLOR_PAIR(4));
         mvwprintw(registerWin, registerWin->_begy + 2, (registerWin->_maxx - 16) / 2, "*** REGISTER ***");
         mvwprintw(registerWin, 7, 2, "Username: ");
         mvwprintw(registerWin, 12, 2, "Password: ");
         mvwprintw(registerWin, 18, 2, "Notification:");
         wmove(nameWin, 1, 1);
         getyx(nameWin, curs_y, curs_x);
         wattroff(registerWin, COLOR_PAIR(4));
         wattron(registerWin, COLOR_PAIR(1));
         mvwprintw(registerWin, 9, 5, "(Username just contains A-Z, a-z, 0-9, '_', can't start");
         mvwprintw(registerWin, 10, 5, "with a number, and must have more than 3 characters)");
         mvwprintw(registerWin, 14, 5, "(Password must atleast 8 characters and include alphabet,");
         mvwprintw(registerWin, 15, 5, "special char !\"#$%%'()*+,-./:;<=>?@[\\ ]^_`{|}~ and number,");
         mvwprintw(registerWin, 16, 5, "ex: 4vQj*SFp)");
         wattroff(registerWin, COLOR_PAIR(1));
         mvwprintw(registerWin, registerWin->_begy + 3, (registerWin->_maxx - 19) / 2, "(Press Esc to back)");
         wrefresh(registerWin);
         // End register window design
         wrefresh(nameWin);
         wrefresh(passWin);
         // Handle user name
         // Move the cursor to behind the Username:
         getyx(nameWin, curs_y, curs_x);
         wmove(nameWin, curs_y, curs_x);
         // printw("Y = %d, X = %d", registerWin->_cury, registerWin->_curx);
         // refresh();
         // change int to char *
         int n;
         char c = char(n);
         char *ch = &c;
         int sig;
         bool isAppear = FALSE;
         // Handle user name
         while (true)
         {
            // This do while loop will handle user name input
            do
            {
               // printw("Y = %d, X = %d", registerWin->_cury, registerWin->_curx);
               // refresh();
               // Get the key
               curs_set(1);
               n = wgetch(nameWin);
               // printw("Y = %d, X = %d", registerWin->_cury, registerWin->_curx);
               // refresh();
               // if get Esc(27), it will turn back to login menu
               if(n == 27) // back to login menu
               {
                  curs_set(0);
                  clear();
                  endwin();
                  handleLogin(sock, -1);
                  break;
               } else if (n == 8 || n == 127) { // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
                  if (input.length() != 0) // check if input length == 0 then no need to pop from string input
                  {
                     input.pop_back();
                     wprintw(nameWin, "\b \b");
                     wrefresh(nameWin);
                     curs_x--;
                  }
               } else if(n >= 97 && n <= 122 || n >= 65 && n <= 90 || n == 95 || n == 124 || n >= 48 && n <= 57 && input.length() != 0) { // User name just contains A-Z, a-z, 0-9, '_', can't start with a number, and must >= 3 characters
                  c = char(n);
                  mvwprintw(nameWin, curs_y, curs_x, "%c", c);// print the current key on screen and push it to input string
                  wrefresh(nameWin);
                  input.push_back(c);
                  curs_x++;
               } else if (n == 10 && input.length() >= 3) { // User name must be at least 3 characters
                  break;
               } else if (n >= 48 && n <= 57 && input.length() == 0) { // User name can't start with number
                  getyx(nameWin, curs_y, curs_x);
                  curs_set(0);
                  attron(COLOR_PAIR(1));
                  mvprintw(20, 2, "User name can't start with number! Press ENTER to continue.");
                  attroff(COLOR_PAIR(1));
                  refresh();
                  while (getch() != 10) {}
                  move(20, 0);
                  clrtoeol();
                  refresh();
                  wattron(registerWin, COLOR_PAIR(3));
                  box(registerWin, boxStyle_x, boxStyle_y);
                  wattroff(registerWin, COLOR_PAIR(3));
                  wattron(nameWin, COLOR_PAIR(3));
                  box(nameWin, 0, 0);
                  wattroff(nameWin, COLOR_PAIR(3));
                  wattron(passWin, COLOR_PAIR(5));
                  box(passWin, 0, 0);
                  wattroff(passWin, COLOR_PAIR(5));
                  refresh();
                  wmove(nameWin, curs_y, curs_x);
                  wrefresh(registerWin);
               }
               else if (n == 10 && input.length() < 3) { // User name must be at least 3 characters
                  getyx(nameWin, curs_y, curs_x);
                  curs_set(0);
                  attron(COLOR_PAIR(1));
                  mvprintw(20, 2, "User name must be at least 3 characters! Press ENTER to continue.");
                  attroff(COLOR_PAIR(1));
                  refresh();
                  while (getch() != 10) {}
                  move(20, 0);
                  clrtoeol();
                  refresh();
                  wattron(registerWin, COLOR_PAIR(3));
                  box(registerWin, boxStyle_x, boxStyle_y);
                  wattroff(registerWin, COLOR_PAIR(3));
                  wrefresh(registerWin);
                  wattron(nameWin, COLOR_PAIR(5));
                  box(nameWin, 0, 0);
                  wattroff(nameWin, COLOR_PAIR(5));
                  wattron(passWin, COLOR_PAIR(5));
                  box(passWin, 0, 0);
                  wattroff(passWin, COLOR_PAIR(5));
                  wmove(nameWin, curs_y, curs_x);
                  getyx(nameWin, curs_y, curs_x);
               }
            } while (true);

            // if we press ESC key, it will break out the do while loop above and back to the log in menu
            // but we have 1 more while loop so this will handle it
            if (n == 27)
            {
               break;
            }
            // if we don't press ESC, then we will continue to handle the whole user name input
            // get the current coordinate of cursor
            getyx(nameWin, curs_y, curs_x);
            if (input.length() == 0) // User name must not blank
            {
               attron(COLOR_PAIR(1));
               mvprintw(20, 2, "Don't input blank username!");
               attroff(COLOR_PAIR(1));
               refresh();
               sleep(2);
               move(20, 0);
               clrtoeol();
               refresh();
               wattron(registerWin, COLOR_PAIR(3));
               box(registerWin, boxStyle_x, boxStyle_y);
               wattroff(registerWin, COLOR_PAIR(3));
               wrefresh(registerWin);
               wattron(nameWin, COLOR_PAIR(5));
               box(nameWin, 0, 0);
               wattroff(nameWin, COLOR_PAIR(5));
               wattron(passWin, COLOR_PAIR(5));
               box(passWin, 0, 0);
               wattroff(passWin, COLOR_PAIR(5));
               refresh();
            }
            else // if user name is legal then send request to the server
            {
               username = input;
               ChatService::RequestSend("REGISTER_USER|" + username, sock);
               isUserAvailable = ChatService::ConvertToBool(ChatService::GetValueFromServer(sock, "REGISTER_USER"));
               // check if there are already a user with that user name in database
               if (!isUserAvailable) {
                  curs_set(0);
                  attron(COLOR_PAIR(1));
                  mvprintw(20, 2, "Username is already available! Press ENTER to continue\n");
                  attroff(COLOR_PAIR(1));
                  refresh();
                  while (getch() != 10) {}
                  move(20, 0);
                  clrtoeol();
                  refresh();
                  wattron(registerWin, COLOR_PAIR(3));
                  box(registerWin, boxStyle_x, boxStyle_y);
                  wattroff(registerWin, COLOR_PAIR(3));
                  wattron(nameWin, COLOR_PAIR(5));
                  box(nameWin, 0, 0);
                  wattroff(nameWin, COLOR_PAIR(5));
                  wattron(passWin, COLOR_PAIR(5));
                  box(passWin, 0, 0);
                  wattroff(passWin, COLOR_PAIR(5));
                  refresh();
                  // make the cursor back to the end of user name input position and we can change it, the while
                  wmove(nameWin, curs_y, curs_x);
                  wrefresh(registerWin);
               }
               else // if there is no user with that user name then create user name has success
               {
                  break;
               }
            }
         }
         if (n != 27) // If not press ESC in handle user name, continue to password
         {
            // move the cursor to after Password:
            wmove(passWin, 1, 1);
            getyx(passWin, curs_y, curs_x); // set the current cursor coordinate
            input = ""; // empty th input
            c = '*'; // hide password
            while (true)
            {
               // Handle input password
               do{
                  curs_set(1);
                  n = wgetch(passWin);
                  // if press escape then return back to login menu
                  if(n == 27)
                  {
                     curs_set(0);
                     clear();
                     endwin();
                     handleLogin(sock, -1);
                     break;
                  } else if (n == 8 || n == 127) { // backspace
                     if (input.length() != 0)
                        {
                           input.pop_back();
                           wprintw(passWin, "\b \b");
                           wrefresh(passWin);
                           curs_x--;
                        }
                  }  else if (n == 10 && input.length() >= 8)
                  {
                     break;
                  }
                  else if (n == 10 && input.length() < 8)
                  {
                     getyx(passWin, curs_y, curs_x);
                     curs_set(0);
                     attron(COLOR_PAIR(1));
                     mvprintw(20, 2, "Password must be at least 8 characters! Press ENTER to continue.");
                     attroff(COLOR_PAIR(1));
                     refresh();
                     while (getch() != 10) {}
                     move(20, 0);
                     clrtoeol();
                     refresh();
                     wattron(registerWin, COLOR_PAIR(3));
                     box(registerWin, boxStyle_x, boxStyle_y);
                     wattroff(registerWin, COLOR_PAIR(3));
                     wattron(nameWin, COLOR_PAIR(5));
                     box(nameWin, 0, 0);
                     wattroff(nameWin, COLOR_PAIR(5));
                     wattron(passWin, COLOR_PAIR(5));
                     box(passWin, 0, 0);
                     wattroff(passWin, COLOR_PAIR(5));
                     refresh();
                     wmove(passWin, curs_y, curs_x);
                     getyx(passWin, curs_y, curs_x);
                     wrefresh(registerWin);
                  } else if (n == ctrl('a')) {
                     isAppear = TRUE;
                     wmove(passWin, curs_y, curs_x - input.length());
                     wclrtoeol(passWin);
                     box(passWin, 0, 0);
                     wrefresh(passWin);
                     mvwprintw(passWin, curs_y, curs_x - input.length(), "%s", input.c_str());
                  } else if (n == ctrl('d'))
                  {
                     isAppear = FALSE;
                     wmove(passWin, curs_y, curs_x - input.length());
                     wclrtoeol(passWin);
                     box(passWin, 0, 0);
                     wrefresh(passWin);
                     mvwprintw(passWin, curs_y, curs_x - input.length(), "%s", string(input.length(), '*').c_str());
                  } else {
                     if (isAppear)
                     {
                        c = char(n);
                     }
                     else
                     {
                        c = '*';
                     }
                     mvwprintw(passWin, curs_y, curs_x,"%s", ch);
                     wrefresh(passWin);
                     input.push_back(char(n));
                     curs_x++;
                  }

               } while (true);

               if (n == 27) // if press ESC then break out of this loop
               {
                  break;
               }
               else // continue to handle the whole password input
               {
                  password = input;
                  getyx(passWin, curs_y, curs_x);
                  if (ChatService::HandlePassword(password) == FALSE)
                  {
                     curs_set(0);
                     attron(COLOR_PAIR(1));
                     mvprintw(registerWin->_begy + 20, 5, "Password must atleast 8 characters and include alphabet, ");
                     mvprintw(registerWin->_begy + 21, 5, "special char !\"#$%%'()*+,-./:;<=>?@[\\]^_`{|}~ and number,");
                     mvprintw(registerWin->_begy + 22, 5, "ex: 4vQj*SFp. Press ENTER to continue.");
                     attroff(COLOR_PAIR(1));
                     refresh();
                     while (getch() != 10) {}
                     move(20, 0);
                     clrtobot();
                     refresh();
                     wattron(registerWin, COLOR_PAIR(3));
                     box(registerWin, boxStyle_x, boxStyle_y);
                     wattroff(registerWin, COLOR_PAIR(3));
                     wrefresh(registerWin);
                     wattron(nameWin, COLOR_PAIR(5));
                     box(nameWin, 0, 0);
                     wattroff(nameWin, COLOR_PAIR(5));
                     wattron(passWin, COLOR_PAIR(5));
                     box(passWin, 0, 0);
                     wattroff(passWin, COLOR_PAIR(5));
                     refresh();
                     wmove(passWin, curs_y, curs_x);
                  }
                  else
                  {
                     ChatService::RequestSend("REGISTER_PASSWORD|" + password, sock);
                     isSuccess = ChatService::ConvertToBool(ChatService::GetValueFromServer(sock, "REGISTER_PASSWORD"));
                     if (!isSuccess) {
                        mvprintw(20, 2, "Register Failed!");
                        mvprintw(21, 2, "Press any key to continue!");
                        clear();
                        endwin();
                        handleLogin(sock, -1);
                        break;
                     }
                     else {
                        currentUser.setName(username);
                        currentUser.setPassword(password);
                        handleMenu(sock, -1);
                        break;
                     }
                  }
               }
            }
         }
         break;
      }
      case 108: // 108 is L key
      {
         clear();
         endwin();
         currentView = RegisterInput;
         std::system ("clear");
         string username;
         string password;
         //sendrequest with partern for register
         bool isSuccess = false;
         bool isUserAvailable = true;
         // pdhm
         initscr();
         keypad(stdscr, TRUE);
         cbreak();
         noecho();
         curs_set(1);
         if(has_colors() == FALSE)
         {
            clear();
            endwin();
            printf("Your terminal does not support color\n");
            exit(1);
         }
         start_color();
         init_pair(1, COLOR_YELLOW, COLOR_BLACK);
         init_pair(2, COLOR_RED, COLOR_BLACK);
         init_pair(3, COLOR_GREEN, COLOR_BLACK);
         init_pair(4, COLOR_CYAN, COLOR_BLACK);
         init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
         init_pair(6, COLOR_GREEN, COLOR_BLACK);
         init_pair(7, COLOR_WHITE, COLOR_BLACK);
         int height, width, start_x, start_y;
         height = stdscr->_begy + 12;
         width = stdscr->_begx + 61;
         start_x = start_y = 0;
         WINDOW *loginWin = newwin(19, 60, 0, 0);
         WINDOW *nameWin = subwin(loginWin, 3, 42, 6, 14);
         WINDOW *passWin = subwin(loginWin, 3, 42, 9, 14);
         currentWin = loginWin;
         wattron(loginWin, COLOR_PAIR(3));
         box(loginWin, 0, 0);
         mvwprintw(loginWin, 5, 2, "--------------------------------------------------------");
         mvwprintw(loginWin, 12, 2, "--------------------------------------------------------");
         wattroff(loginWin, COLOR_PAIR(3));
         wattron(nameWin, COLOR_PAIR(7));
         box(nameWin, 0, 0);
         wattroff(nameWin, COLOR_PAIR(7));
         wattron(passWin, COLOR_PAIR(7));
         box(passWin, 0, 0);
         wattroff(passWin, COLOR_PAIR(7));
         refresh();
         wattron(loginWin, COLOR_PAIR(4));
         mvwprintw(loginWin, 2, 27, "LOGIN");
         mvwprintw(loginWin, 7, 4, "Username: ");
         mvwprintw(loginWin, 10, 4, "Password: ");
         wattroff(loginWin, COLOR_PAIR(4));
         mvwprintw(loginWin, 3, 20, "(Press Esc to back)");
         wrefresh(loginWin);

         int sig;
         char c;
         char *ch = &c;
         int curs_y, curs_x;
         curs_set(0);
         string input = "";
         wmove(nameWin, 1, 1);
         getyx(nameWin, curs_y, curs_x);
         curs_set(1);
         // Handle user name
         while (true)
         {
            // This do while loop will handle user name input
            do
            {
               // Get the key
               sig = wgetch(nameWin);
               // if get Esc(27), it will turn back to login menu
               if(sig == 27) // back to login menu
               {
                  clear();
                  endwin();
                  handleLogin(sock, -1);
                  break;
               } else if (sig == 8 || sig == 127) { // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
                  if (input.length() != 0) // check if input length == 0 then no need to pop from string input
                  {
                     curs_set(1);
                     input.pop_back();
                     wprintw(nameWin, "\b \b");
                     wrefresh(nameWin);
                     curs_x--;
                  }
               } else if(sig >= 97 && sig <= 122 || sig >= 65 && sig <= 90 || sig == 95 || sig == 124 || sig >= 48 && sig <= 57 && input.length() != 0) { // User name just contains A-Z, a-z, 0-9, '_', can't start with a number, and must >= 3 characters
                  curs_set(1);
                  c = char(sig);
                  mvwprintw(nameWin, curs_y, curs_x, "%c", c);// print the current key on screen and push it to input string
                  wrefresh(nameWin);
                  input.push_back(c);
                  curs_x++;
               } else if (sig == 10 && input.length() >= 3) { // User name must be at least 3 characters
                  break;
               } else if (sig >= 48 && sig <= 57 && input.length() == 0) { // User name can't start with number
                  curs_set(0);
                  attron(COLOR_PAIR(1));
                  mvprintw(14, 3, "User name can't start with number!");
                  attroff(COLOR_PAIR(1));
                  refresh();
                  sleep(2);
                  move(13, 0);
                  clrtobot();
                  refresh();
                  curs_set(1);
                  wattron(loginWin, COLOR_PAIR(3));
                  box(loginWin, 0, 0);
                  wattroff(loginWin, COLOR_PAIR(3));
                  wrefresh(loginWin);
               }
               else if (sig == 10 && input.length() < 3) { // User name must be at least 3 characters
                  curs_set(0);
                  getyx(nameWin, curs_x, curs_y);
                  attron(COLOR_PAIR(1));
                  mvprintw(14, 3, "User name must be at least 3 characters!");
                  attroff(COLOR_PAIR(1));
                  refresh();
                  sleep(1);
                  move(13, 0);
                  clrtobot();
                  refresh();
                  wattron(loginWin, COLOR_PAIR(3));
                  box(loginWin, 0, 0);
                  wattroff(loginWin, COLOR_PAIR(3));
                  wrefresh(loginWin);
                  curs_set(1);
                  wmove(nameWin, curs_y, curs_x);
                  getyx(nameWin, curs_y, curs_x);
               }
            } while (true);

            // if we press ESC key, it will break out the do while loop above and back to the log in menu
            // but we have 1 more while loop so this will handle it
            if (sig == 27)
            {
               break;
            }
            // if we don't press ESC, then we will continue to handle the whole user name input
            // get the current coordinate of cursor
            getyx(nameWin, curs_y, curs_x);
            if (input.length() == 0) // User name must not blank
            {
               curs_set(0);
               attron(COLOR_PAIR(1));
               mvprintw(14, 3, "Don't input blank username!");
               attroff(COLOR_PAIR(1));
               refresh();
               sleep(2);
               move(13, 0);
               clrtobot();
               wattron(loginWin, COLOR_PAIR(3));
               box(loginWin, 0, 0);
               wattroff(loginWin, COLOR_PAIR(3));
               refresh();
               curs_set(1);
               wrefresh(loginWin);
            }
            else // if user name is legal then send request to the server
            {
               username = input;
               ChatService::RequestSend("LOGIN_USER|" + username, sock);
               isUserAvailable = ChatService::ConvertToBool(ChatService::GetValueFromServer(sock, "LOGIN_USER"));
               // check if there are already a user with that user name in database
               if (!isUserAvailable) {
                  curs_set(0);
                  attron(COLOR_PAIR(1));
                  mvprintw(14, 3, "Username is unavailable! Press Enter to continue\n");
                  attroff(COLOR_PAIR(1));
                  refresh();
                  if (getch()== 10)
                  {
                     move(13, 0);
                     clrtobot();
                     wattron(loginWin, COLOR_PAIR(3));
                     box(loginWin, 0, 0);
                     wattroff(loginWin, COLOR_PAIR(3));
                     refresh();
                     wrefresh(loginWin);
                     curs_set(1);
                     // make the cursor back to the end of user name input position and we can change it, the while
                     wmove(nameWin, curs_y, curs_x);
                     }
               }
               else{
                  curs_set(0);
                  break;
               }
            }
         }

         if (sig != 27) // If not press ESC in handle user name, continue to password
         {
            // move the cursor to after Password:
            int countFailure = 1;
            wmove(passWin, 1, 1);
            getyx(passWin, curs_y, curs_x); // set the current cursor coordinate
            input = ""; // empty th input
            c = '*'; // hide password
            bool isAppear = false;
            while (true)
            {
               // Handle input password
               do{
                  curs_set(1);
                  sig = wgetch(passWin);
                  // if press escape then return back to login menu
                  // if(sig == 27)
                  // {
                  //    clear();
                  //    endwin();
                  //    handleLogin(sock, -1);
                  //    break;
                  // } else if (sig == 8 || sig == 127) { // backspace
                  //    if (input.length() != 0)
                  //       {
                  //          input.pop_back();
                  //          wprintw(loginWin, "\b \b");
                  //          wrefresh(loginWin);
                  //          curs_x--;
                  //       }
                  // } else if(sig >= 97 && sig <= 122 || sig >= 65 && sig <= 90 || sig >= 48 && sig <= 57) {
                  //    mvwprintw(loginWin, curs_y, curs_x, "%c", c);
                  //    wrefresh(loginWin);
                  //    input.push_back(char(sig));
                  //    curs_x++;
                  // } else if (sig == 10)
                  // {
                  //    break;
                  // }
                  if(sig == 27)
                  {
                     curs_set(0);
                     clear();
                     endwin();
                     handleLogin(sock, -1);
                     break;
                  } else if (sig == 8 || sig == 127) { // backspace
                     if (input.length() != 0)
                        {
                           input.pop_back();
                           wprintw(passWin, "\b \b");
                           wrefresh(passWin);
                           curs_x--;
                        }
                  } else if (sig == 10) {
                     break;
                  } else if (sig == ctrl('a')) {
                     isAppear = TRUE;
                     wmove(passWin, curs_y, curs_x - input.length());
                     wclrtoeol(passWin);
                     box(passWin, 0, 0);
                     wrefresh(passWin);
                     mvwprintw(passWin, curs_y, curs_x - input.length(), "%s", input.c_str());
                  } else if (sig == ctrl('d')) {
                     isAppear = FALSE;
                     wmove(passWin, curs_y, curs_x - input.length());
                     wclrtoeol(passWin);
                     box(passWin, 0, 0);
                     wrefresh(passWin);
                     mvwprintw(passWin, curs_y, curs_x - input.length(), "%s", string(input.length(), '*').c_str());
                  } else {
                     if (isAppear)
                     {
                        c = char(sig);
                     }
                     else
                     {
                        c = '*';
                     }
                     mvwprintw(passWin, curs_y, curs_x, "%c", c);
                     wrefresh(passWin);
                     input.push_back(char(sig));
                     curs_x++;
                  }
               } while (true);

               if (sig == 27) // if lress ESC then break out of this loop
               {
                  break;
               }
               else // continue to handle the whole password input
               {
                  getyx(passWin, curs_y, curs_x);
                  if (input.length() == 0)
                  {
                     curs_set(0);
                     attron(COLOR_PAIR(1));
                     mvprintw(14, 3, "Don't input blank password!");
                     attroff(COLOR_PAIR(1));
                     refresh();
                     sleep(2);
                     move(13, 0);
                     clrtobot();
                     wattron(loginWin, COLOR_PAIR(3));
                     box(loginWin, 0, 0);
                     wattroff(loginWin, COLOR_PAIR(3));
                     refresh();
                     wrefresh(loginWin);
                     curs_set(1);
                     wmove(passWin, curs_y, curs_x);
                  }
                  else
                  {
                     password = input;
                     ChatService::RequestSend("LOGIN_PASSWORD|" + password, sock);
                     isSuccess = ChatService::ConvertToBool(ChatService::GetValueFromServer(sock, "LOGIN_PASSWORD"));
                     if (!isSuccess && countFailure < 3) {
                        curs_set(0);
                        attron(COLOR_PAIR(1));
                        mvprintw(14, 3, "Wrong password. Please try again");
                        mvprintw(15, 3, "Press ENTER to continue.");
                        attroff(COLOR_PAIR(1));
                        refresh();
                        while (getch() != 10) {}
                        move(14, 0);
                        clrtobot();
                        wattron(loginWin, COLOR_PAIR(3));
                        box(loginWin, 0, 0);
                        wattroff(loginWin, COLOR_PAIR(3));
                        refresh();
                        wrefresh(loginWin);
                        wmove(passWin, curs_y, curs_x);
                        curs_set(1);
                        countFailure++;
                     }
                     else if (!isSuccess && countFailure == 3)
                     {
                        curs_set(0);
                        attron(COLOR_PAIR(1));
                        mvprintw(14, 3, "You have enter the wrong password 3 times");
                        mvprintw(15, 3, "The app will be closed now!");
                        attroff(COLOR_PAIR(1));
                        refresh();
                        sleep(2);
                        clear();
                        endwin();
                        handleLogin(sock, -1);
                        break;
                     }
                     else if (isSuccess) {
                        currentUser.setName(username);
                        currentUser.setPassword(password);
                        handleMenu(sock, -1);
                        break;
                     }
                  }
               }
            }
         }

         if (getch() == 27)
         {
            clear();
            endwin();
            handleLogin(sock, -1);
         }
         break;
      }
      case 27: // 27 is Esc key
         refresh();
         endwin();
         ChatService::exitAppChat(sock);
         readThread.join();
         cout << "Exit chat app!\n";
         break;
         exit(0);
      default: // If not right key then it will not do anything
         // cout<<"Invalid choice!\n";
         ChatView::handleLogin( sock, -1);
   }
}

void ChatView::Menu() {
   currentView = Main;
   clear();
   endwin();
   initscr();
   keypad(stdscr, TRUE);
   cbreak();
   if(has_colors() == FALSE)
   {
      clear();
      endwin();
      printf("Your terminal does not support color\n");
      exit(1);
   }
   start_color();
   init_pair(1, COLOR_YELLOW, COLOR_BLACK);
   init_pair(2, COLOR_RED, COLOR_BLACK);
   init_pair(3, COLOR_GREEN, COLOR_BLACK);
   init_pair(4, COLOR_CYAN, COLOR_BLACK);
   init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
   init_pair(6, COLOR_WHITE, COLOR_BLACK);
   refresh();
   noecho();
   curs_set(0);
   int height, width, start_X, start_Y;
   height = 19;
   width = 70;
   start_X = start_Y = 0;
   WINDOW * userMenu = newwin(height, width, start_Y, start_X);
   currentWin = userMenu;
   wattron(userMenu, COLOR_PAIR(3));
   box(userMenu, 0, 0);
   mvwprintw(userMenu, 4, 2, "------------------------------------------------------------------");
   wattroff(userMenu, COLOR_PAIR(3));
   wattron(userMenu, COLOR_PAIR(4));
   mvwprintw(userMenu, 2, (userMenu->_maxx - 28) / 2, ">>>>> Chat Application <<<<<");
   mvwprintw(userMenu, 6, 3, "Create new chat room");
   mvwprintw(userMenu, 8, 3, "Join room");
   mvwprintw(userMenu, 10, 3, "Find user(s)");
   mvwprintw(userMenu, 12, 3, "Update profile");
   mvwprintw(userMenu, 14, 3, "Log out");
   mvwprintw(userMenu, 6, (userMenu->_maxx - 11), "(Press N)");
   mvwprintw(userMenu, 8, (userMenu->_maxx - 11), "(Press J)");
   mvwprintw(userMenu, 10, (userMenu->_maxx - 11), "(Press F)");
   mvwprintw(userMenu, 12, (userMenu->_maxx - 11), "(Press U)");
   mvwprintw(userMenu, 14, (userMenu->_maxx - 11), "(Press O)");
   wattroff(userMenu, COLOR_PAIR(4));
   wattron(userMenu, COLOR_PAIR(2));
   mvwprintw(userMenu, 16, 3, "Exit");
   mvwprintw(userMenu, 16, (userMenu->_maxx - 13), "(Press Esc)");
   wattroff(userMenu, COLOR_PAIR(2));
   wrefresh(userMenu);
}

void ChatView::ChatRoomConsole(ChatClient room, WINDOW* win) {
   string output = "Room name: " + room.getRoomName();
   mvwprintw(win, 2, (win->_maxx / 2) - (output.length()/2),"%s", output.c_str());
}

void ChatView::UserConsole(int sock) {
   clear();
   endwin();
   system("clear");
   currentView = Update;
   initscr();
   keypad(stdscr, TRUE);
   cbreak();
   refresh();
   noecho();
   curs_set(0);
   if(has_colors() == FALSE)
   {
      clear();
      endwin();
      printf("Your terminal does not support color\n");
      exit(1);
   }
   start_color();
   init_pair(1, COLOR_YELLOW, COLOR_BLACK);
   init_pair(2, COLOR_RED, COLOR_BLACK);
   init_pair(3, COLOR_GREEN, COLOR_BLACK);
   init_pair(4, COLOR_CYAN, COLOR_BLACK);
   init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
   init_pair(6, COLOR_WHITE, COLOR_BLACK);
   init_pair(7, COLOR_BLUE, COLOR_WHITE);
   string input;
   WINDOW *updateWin = newwin(22, 60, 0, 0);
   wattron(updateWin, COLOR_PAIR(3));
   box(updateWin, 0, 0);
   mvwprintw(updateWin, 4, 2, "--------------------------------------------------------");
   mvwprintw(updateWin, 12, 2, "--------------------------------------------------------");
   wattroff(updateWin, COLOR_PAIR(3));
   ChatService::RequestSend("UPDATE_PROFILE|", sock);
   ChatService::GetCurrentProfile(sock, currentUser);
   refresh();
   wattron(updateWin, COLOR_PAIR(4));
   mvwprintw(updateWin, 2, (updateWin->_maxx - (22 + currentUser.getName().length())) / 2, "*** Current user: %s ***", currentUser.getName().c_str());
   mvwprintw(updateWin, 6, 3, "1. Address: ");
   mvwprintw(updateWin, 8, 3, "2. Gender: ");
   mvwprintw(updateWin, 10, 3, "3. Change password ");
   mvwprintw(updateWin, 6, (updateWin->_maxx - 6), "(A)");
   mvwprintw(updateWin, 8, (updateWin->_maxx - 6), "(G)");
   mvwprintw(updateWin, 10, (updateWin->_maxx - 6), "(P)");
   mvwprintw(updateWin, 14, (updateWin->_maxx - 40) / 2, "Select an option that you want to change");
   wattroff(updateWin, COLOR_PAIR(4));
   wattron(updateWin, COLOR_PAIR(6));
   mvwprintw(updateWin, 6, 15, "%s", currentUser.getAddress().c_str());
   mvwprintw(updateWin, 8, 15, "%s", currentUser.getGender().c_str());
   wattroff(updateWin, COLOR_PAIR(6));
   wrefresh(updateWin);
   int curs_x, curs_y;
   // curs_x = 16;
   // curs_y = 15;
   wmove(updateWin, 43, 13);
   int n;
   char c = char(n);
   char *ch = &c;
   do {
      input = "";
      n = wgetch(updateWin);
      if (n == 97) {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 16, 3, "New Address: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do {
            n = wgetch(updateWin);
            if (n == 27) {
               return ChatView::UserConsole(sock);
            }
            else if (n == 8 || n == 127) {
               if (input.length() != 0) {
                  input.pop_back();
                  wprintw(updateWin, "\b \b");
                  wrefresh(updateWin);
                  curs_x--;
               }
            }
            else if (n == 10) {
               ChatService::RequestSend("UPDATE_ADDRESS|" + input, sock);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 18, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 19, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               while (getch() != 10) {}
               refresh();
               printf("\n");
               return ChatView::UserConsole(sock);
            }
            else {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x, "%c", c);// print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(c);
               curs_x++;
            }
         } while(true);
      }
      if (n == 103) {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 16, 3, "New gender: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do {
            n = wgetch(updateWin);
            if (n == 27) {
               return ChatView::UserConsole(sock);
            }
            else if (n == 8 || n == 127) {
               if (input.length() != 0) {
                  input.pop_back();
                  wprintw(updateWin, "\b \b");
                  wrefresh(updateWin);
                  curs_x--;
               }
            }
            else if (n == 10) {
               curs_set(0);
               ChatService::RequestSend("UPDATE_GENDER|" + input, sock);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 18, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 19, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               while (getch() != 10) {}
               refresh();
               printf("\n");
               return ChatView::UserConsole(sock);
            }
            else {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x, "%c", c);// print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(c);
               curs_x++;
            }
         } while(true);
      }
      if (n == 112) {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 16, 3, "New password: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do {
            n = wgetch(updateWin);
            if (n == 27) {
               return ChatView::UserConsole(sock);
            }
            else if (n == 8 || n == 127) {
               if (input.length() != 0) {
                  input.pop_back();
                  wprintw(updateWin, "\b \b");
                  wrefresh(updateWin);
                  curs_x--;
               }
            }
            else if (n == 10) {
               ChatService::RequestSend("CHANGE_PASSWORD|" + input, sock);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 18, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 19, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               while (getch() != 10) {}
               refresh();
               printf("\n");
               return ChatView::UserConsole(sock);
            }
            else {
               c = '*';
               mvwprintw(updateWin, curs_y, curs_x+1, "%c", c);// print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(char(n));
               curs_x++;
            }
         } while(true);
      }
      if (n == 27) {
         clear();
         endwin();
         break;
      }
      if (n == 10) {
         break;
      }
   } while(true);
}

int ChatView::selectMenu() {
   int n;
   Menu();
   // cout << "\n\033[1;32mSelect your choice: \033[0m";
   // cin >> n;
   n = getch();
   noecho();
   printw("%d", n);
   // ChatService::HandleInput();
   // cin.ignore();
   if(n > 0 || n < 99){
      return n;
   }
   else return selectMenu();

}

void ChatView::interactUserMenu(int count, map<int, UserClient> foundUser, int sock) {
   int choice;
   bool reSelect;
   refresh();
   endwin();
   string input;
   string selectValue;
   // Start regigter window design
   initscr();
   start_color();
   init_pair(1, COLOR_YELLOW, COLOR_BLACK);
   init_pair(2, COLOR_RED, COLOR_BLACK);
   init_pair(3, COLOR_GREEN, COLOR_BLACK);
   init_pair(4, COLOR_CYAN, COLOR_BLACK);
   init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
   init_pair(6, COLOR_WHITE, COLOR_BLACK);
   noecho();
   if(has_colors() == FALSE)
   {
      clear();
      endwin();
      printf("Your terminal does not support color\n");
      exit(1);
   }
   echo();
   WINDOW *interactUserMenuWin = newwin(5, 61, 16, 0);
   wattron(interactUserMenuWin, COLOR_PAIR(3));
   box(interactUserMenuWin, 0, 0);
   wattroff(interactUserMenuWin, COLOR_PAIR(3));
   refresh();
   wattron(interactUserMenuWin, COLOR_PAIR(4));
   mvwprintw(interactUserMenuWin, 1, 1, "Select user you want to interact: ");
   wattroff(interactUserMenuWin, COLOR_PAIR(4));
   wrefresh(interactUserMenuWin);
   int curs_x, curs_y;
   wmove(interactUserMenuWin, 1, 34);
   // change int to char *
   int n;
   char c = char(n);
   char *ch = &c;
   input = "";
   do
   {
      // Get the key
      n = wgetch(interactUserMenuWin);
      if(n == 27) // back to login menu
      {
         clear();
         endwin();
         break;
      } else if (n == 8 || n == 127) { // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
         // ChatView::ChangeBarText(std::to_string(input.length()));
         if (input.length() != 0 ) // check if input length == 0 then no need to pop from string input
         {
            input.pop_back();
            wprintw(interactUserMenuWin, " \b");
            wrefresh(interactUserMenuWin);
            curs_x--;
         }
         else if(input.length() == 0){
            wattron(interactUserMenuWin, COLOR_PAIR(4));
            mvwprintw(interactUserMenuWin, 1, 1, "Select user you want to interact: ");
            wattroff(interactUserMenuWin, COLOR_PAIR(4));
            wrefresh(interactUserMenuWin);
         }
      } else if (n == 10 && input.length() == 0) {
         move(1, 1);
         clrtoeol();
         wattron(interactUserMenuWin, COLOR_PAIR(4));
         mvwprintw(interactUserMenuWin, 1, 1, "Select user you want to interact: ");
         wattroff(interactUserMenuWin, COLOR_PAIR(4));
         wrefresh(interactUserMenuWin);
         continue;
      } else if (n == 10 && input.length() != 0) {
         break;
      }else  {
         c = char(n);
         mvwprintw(interactUserMenuWin, curs_y, curs_x, "%c",c);
         wrefresh(interactUserMenuWin);
         input.push_back(c);
         curs_x++;
      }
   } while (true);

   if (n == 27)
   {
      ChatView::handleMenu(sock, -1);
   }
   else{
      selectValue = input;
      int choice;
      choice = stoi(selectValue);
      if (choice == count) {
         handleMenu(sock, -1);
      } else if (choice <= 0 || choice > count) {
         //cout << "Invalid choice!\n";
         wattron(interactUserMenuWin, COLOR_PAIR(1));
         mvwprintw(interactUserMenuWin, 2, 2, "Invalid choice! ");
         wattroff(interactUserMenuWin, COLOR_PAIR(1));
         wrefresh(interactUserMenuWin);
         // sleep(1);
         ChatView::interactUserMenu(count, foundUser, sock);
      } else {
         UserUI(sock, foundUser[choice],interactUserMenuWin);
      }
   }
}

void ChatView::handleMenu(int sock, int selection) {
   //vector<ChatClient>listChatRooms;
   // sua tu day
   bool isSuccess = false;
   do {
      if (selection < 0) {
         selection = selectMenu();
      }
      switch (selection) {
         case 110: // N
         {
            clear();
            refresh();
            endwin();
            currentView = CreateRoom;
            system("clear");
            string input;
            // Start regigter window design
            initscr();
            keypad(stdscr, TRUE);
            cbreak();
            refresh();
            start_color();
            init_pair(1, COLOR_YELLOW, COLOR_BLACK);
            init_pair(2, COLOR_RED, COLOR_BLACK);
            init_pair(3, COLOR_GREEN, COLOR_BLACK);
            init_pair(4, COLOR_CYAN, COLOR_BLACK);
            init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
            init_pair(6, COLOR_WHITE, COLOR_BLACK);
            noecho();
            if(has_colors() == FALSE)
            {
               clear();
               endwin();
               printf("Your terminal does not support color\n");
               exit(1);
            }
            //echo();
            WINDOW *createroomWin = newwin(5, 61, 0, 0);
            currentWin = createroomWin;
            wattron(createroomWin, COLOR_PAIR(3));
            box(createroomWin, 0, 0);
            wattroff(createroomWin, COLOR_PAIR(3));
            refresh();
            wattron(createroomWin, COLOR_PAIR(4));
            mvwprintw(createroomWin, 2, 1, "Enter room name: ");
            wattroff(createroomWin, COLOR_PAIR(4));
            wrefresh(createroomWin);
            int curs_x, curs_y;
            getyx(createroomWin, curs_y, curs_x);
            wmove(createroomWin, curs_y, curs_x);
            // change int to char *
            int n;
            char c = char(n);
            char *ch = &c;
            input = "";
            do
            {
               // Get the key
               n = wgetch(createroomWin);
               if(n == 27)
               {
                  clear();
                  endwin();
                  break;
               } else if (n == 8 || n == 127) {
                  if (input.length() != 0){
                     input.pop_back();
                     wprintw(createroomWin, "\b \b");
                     wrefresh(createroomWin);
                     curs_x--;
                  }
               } else if (n == 10 && input.length() >= 3 ) {
                  break;
               }
               else if (n == 10 && input.length() < 3) {
                  attron(COLOR_PAIR(1));
                  mvprintw(7, 0, "Room name must be at least 3 characters!");
                  attroff(COLOR_PAIR(1));
                  refresh();
                  sleep(2);
                  move(16, 0);
                  clrtoeol();
                  refresh();
                  wmove(createroomWin, curs_y, curs_x);
                  wrefresh(createroomWin);
                  ChatView::handleMenu(sock, 110);
               }
               else  {
                  c = char(n);
                  mvwprintw(createroomWin, curs_y, curs_x, "%c",c);
                  wrefresh(createroomWin);
                  input.push_back(c);
                  curs_x++;
               }
               getyx(createroomWin, curs_y, curs_x);
            } while (true);

            if (n == 27)
            {
               break;
            }
            else{
               //cout << "Enter room Name: ";
               string roomName;
               //getline(cin, roomName);
               //ChatService::HandleInput();
               roomName = input;
               ChatService::RequestSend("CREATE_ROOM|" + roomName, sock);
               // cout << "  " <<ChatService::GetValueFromServer(sock, "CREATE_ROOM") << endl;
               // cout << "\033[1;32mPress any key to continue!\033[0m" << endl;
               mvprintw(16, 0, "%s", ChatService::GetValueFromServer(sock, "CREATE_ROOM").c_str());
               mvprintw(17, 0, "Press any key to continue!");
               //std::getchar();
               //break;
               int back = getch();
               if (back == 27)
               {
                  clear();
                  endwin();
                  break;
               }
               // getch();
               endwin();
            }
         }
         case 106: // J
         {
            refresh();
            endwin();
            // ChatView::ChangeBarText("REFRESH_ROOM");
            std::system("clear");
            int n = 0;
            if (foundRoom.size() == 0) {
               foundRoom = ChatService::GetListRoom(sock, isSuccess, n);
               if(isSuccess) {
                  ChooseRoom(sock, n);
               }
            } else {
               ChooseRoom(sock, foundRoom.size());
            }
            break;
         }
         case 102: // F
            {
            clear();
            refresh();
            endwin();
            currentView = FindUser;
            system("clear");
            string input;
            string searchValue;
            // Start regigter window design
            initscr();
            start_color();
            init_pair(1, COLOR_YELLOW, COLOR_BLACK);
            init_pair(2, COLOR_RED, COLOR_BLACK);
            init_pair(3, COLOR_GREEN, COLOR_BLACK);
            init_pair(4, COLOR_CYAN, COLOR_BLACK);
            init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
            init_pair(6, COLOR_WHITE, COLOR_BLACK);
            noecho();
            if(has_colors() == FALSE)
            {
               clear();
               endwin();
               printf("Your terminal does not support color\n");
               exit(1);
            }
            //echo();
            WINDOW *finduserWin = newwin(15, 61, 0, 0);
            wattron(finduserWin, COLOR_PAIR(3));
            box(finduserWin, 0, 0);
            wattroff(finduserWin, COLOR_PAIR(3));
            refresh();
            wattron(finduserWin, COLOR_PAIR(4));
            mvwprintw(finduserWin, 1, 1, "Enter search value: ");
            wattroff(finduserWin, COLOR_PAIR(4));
            wrefresh(finduserWin);
            int curs_x, curs_y;
            getyx(finduserWin, curs_y, curs_x);
            wmove(finduserWin, curs_y, curs_x);
            // change int to char *
            int n;
            char c = char(n);
            char *ch = &c;
            input = "";
            // Handle user name
            do
            {
               // Get the key
               n = wgetch(finduserWin);
               // if get Esc(27), it will turn back to login menu
               if(n == 27) // back to login menu
               {
                  clear();
                  endwin();
                  break;
               } else if (n == 8 || n == 127) { // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
                  if (input.length() != 0) {
                     input.pop_back();
                     wprintw(finduserWin, "\b \b");
                     wrefresh(finduserWin);
                     curs_x--;
                  }
                  else if(input.length() == 0){
                     wattron(finduserWin, COLOR_PAIR(4));
                     mvwprintw(finduserWin, 1, 1, "Enter search value: ");
                     wattroff(finduserWin, COLOR_PAIR(4));
                     wrefresh(finduserWin);
                  }
               } else if(n == 10 && input.length() != 0){
                  break;
               } else  {
                  c = char(n);
                  mvwprintw(finduserWin, curs_y, curs_x, "%c",c);// print the current key on screen and push it to input string
                  wrefresh(finduserWin);
                  input.push_back(c);
                  curs_x++;
               }
               // int testY, testX;
               getyx(finduserWin, curs_y, curs_x);
               // printw("Y = %d, X = %d", testY, testX);
               // refresh();
            } while (true);

            if (n == 27)
            {
               break;
            }
            else{
               searchValue = input;
               ChatService::RequestSend("FIND_USER|" + searchValue, sock);
               int count = 0;
               //mvwprintw(finduserWin, 2, 2, "countname");
               map<int, UserClient> foundUser = ChatService::GetFoundUser(sock, currentUser, count,finduserWin);
               ChatView::interactUserMenu(count, foundUser, sock);
            }

               // cout << "Enter search value: ";
               // getline(cin, searchValue);
               // ChatService::HandleInput();
               // ChatService::RequestSend("FIND_USER|" + searchValue, sock);
               // int count = 0;
               // map<int, UserClient> foundUser = ChatService::GetFoundUser(sock, currentUser, count);
               // ChatView::interactUserMenu(count, foundUser, sock);
               // //ChatView::UserProfile(sock);
               // // ChatService::GetListUser(sock);
               // break;
            }
         case 117: // U
            refresh();
            endwin();
            std::system("clear");
            ChatService::RequestSend("UPDATE_PROFILE|", sock);
            ChatService::GetCurrentProfile(sock, currentUser);
            ChatView::UserConsole(sock);
            break;
         case 111: // O
            refresh();
            clear();
            endwin();
            std::system("clear");
            ChatService::RequestSend("LOGOUT_USER|", sock);
            currentUser.setId(-1);
            currentUser.setPassword("");
            currentUser.setName("");
            handleLogin(sock, -1);
            break;
         case 27: // ESC
            refresh();
            endwin();
            ChatService::exitAppChat(sock);
            readThread.join();
            std::system("clear");
            cout << "Exit chat app!\n";
            exit(0);
         default:
            // cout << "Invalid choice!\n";
            break;
      }
      selection = -1;
   } while(!isSuccess);
}


void ChatView::ChatConsole(ChatClient joinedRoom, int sock) {
   clear();
   refresh();
   endwin();
   currentView = ChattingOption;
   int exitValue;
   system("clear");
   string input;
   // Start regigter window design
   initscr();
   start_color();
   init_pair(1, COLOR_YELLOW, COLOR_BLACK);
   init_pair(2, COLOR_RED, COLOR_BLACK);
   init_pair(3, COLOR_GREEN, COLOR_BLACK);
   init_pair(4, COLOR_CYAN, COLOR_BLACK);
   init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
   init_pair(6, COLOR_WHITE, COLOR_BLACK);
   noecho();
   if(has_colors() == FALSE)
   {
      clear();
      endwin();
      printf("Your terminal does not support color\n");
      exit(1);
   }
   echo();
   curs_set(1);
   WINDOW *ChatConsoleWin = newwin(15, 61, 0, 0);
   currentWin = ChatConsoleWin;
   wattron(ChatConsoleWin, COLOR_PAIR(3));
   box(ChatConsoleWin, 0, 0);
   wattroff(ChatConsoleWin, COLOR_PAIR(3));
   refresh();
   int curs_x, curs_y;
   wmove(ChatConsoleWin, curs_x, curs_y);

   // change int to char *
   int n;
   char c = char(n);
   char *ch = &c;
   wattron(ChatConsoleWin, COLOR_PAIR(4));
   mvwprintw(ChatConsoleWin, 1, 22, "Room Options");
   wattroff(ChatConsoleWin, COLOR_PAIR(4));
   mvwprintw(ChatConsoleWin, 2, 2, "1. Back to chatting");
   mvwprintw(ChatConsoleWin, 3, 2, "2. Back to menu");
   mvwprintw(ChatConsoleWin, 4, 2, "3. List users in room");
   wattron(ChatConsoleWin, COLOR_PAIR(4));
   mvwprintw(ChatConsoleWin, 7, 2, "Your choice:");
   wattroff(ChatConsoleWin, COLOR_PAIR(4));
   wrefresh(ChatConsoleWin);
   bool F;
   //back lai day
   do
   {
      n = wgetch(ChatConsoleWin);
      if(n == 27) {
         clear();
         endwin();
         ChatView::handleMenu(sock, -1);
      } else if (n == 8 || n == 127) {
         if (input.length() != 0){
            input.pop_back();
            wprintw(ChatConsoleWin, " \b");
            wrefresh(ChatConsoleWin);
            curs_x--;
         }
         else if(input.length() == 0){
            wattron(ChatConsoleWin, COLOR_PAIR(4));
            mvwprintw(ChatConsoleWin, 7, 2, "Your choice:");
            wattroff(ChatConsoleWin, COLOR_PAIR(4));
            wrefresh(ChatConsoleWin);
         }
      } else if (n == 10 ) {
         move(13, 2);
         clrtoeol();
         wattron(ChatConsoleWin, COLOR_PAIR(4));
         mvwprintw(ChatConsoleWin, 7, 2, "Your choice:");
         wattroff(ChatConsoleWin, COLOR_PAIR(4));
         wrefresh(ChatConsoleWin);
         continue;
      }else if(n == 49){
         ChattingView(sock, joinedRoom);
      }
      else if(n == 50){
         ChatService::RequestSend("USER_EXIT|", sock);
         ChatView::handleMenu(sock, -1);
      }
      else if(n == 51){
         ChatService::RequestSend("LIST_USER|", sock);
         clear();
         refresh();
         endwin();
         int exitValue;
         system("clear");
         string input;
         int counter =0;
         // Start regigter window design
         initscr();
         start_color();
         init_pair(1, COLOR_YELLOW, COLOR_BLACK);
         init_pair(2, COLOR_RED, COLOR_BLACK);
         init_pair(3, COLOR_GREEN, COLOR_BLACK);
         init_pair(4, COLOR_CYAN, COLOR_BLACK);
         init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
         init_pair(6, COLOR_WHITE, COLOR_BLACK);
         noecho();
         if(has_colors() == FALSE)
         {
            clear();
            endwin();
            printf("Your terminal does not support color\n");
            exit(1);
         }
         echo();
         curs_set(1);
         WINDOW *GetListUserWin = newwin(15, 120, 0, 0);
         wattron(GetListUserWin, COLOR_PAIR(3));
         box(GetListUserWin, 0, 0);
         wattroff(GetListUserWin, COLOR_PAIR(3));
         refresh();
         int curs_x, curs_y;
         wmove(GetListUserWin, curs_x, curs_y);

         // change int to char *
         int n;
         char c = char(n);
         char *ch = &c;
         map<int, UserClient> User = ChatService::GetListUser(joinedRoom,sock,GetListUserWin,counter,currentUser);
         //mvwprintw(GetListUserWin, 27, 2, "Create chat");
         wattron(GetListUserWin, COLOR_PAIR(4));
         mvwprintw(GetListUserWin, 10, 50, "-------Option-------");
         wattroff(GetListUserWin, COLOR_PAIR(4));
         mvwprintw(GetListUserWin, 11, 2, "1.Interact with another user");
         mvwprintw(GetListUserWin, 12, 2, "2.Back");
         wrefresh(GetListUserWin);

         do
         {
            wattron(GetListUserWin, COLOR_PAIR(4));
            mvwprintw(GetListUserWin, 13, 2, "Your choice:");
            wattroff(GetListUserWin, COLOR_PAIR(4));
            wrefresh(GetListUserWin);
            n = wgetch(GetListUserWin);
            if(n == 27)
            {
               clear();
               endwin();
               ChatView::handleMenu(sock, -1);
               break;
            } else if (n == 8 || n == 127) {
               if (input.length() != 0) {
                  input.pop_back();
                  wprintw(GetListUserWin, " \b");
                  wrefresh(GetListUserWin);
                  curs_x--;
               }
               else if(input.length() == 0) {
                  wattron(GetListUserWin, COLOR_PAIR(4));
                  mvwprintw(GetListUserWin, 13, 2, "Your choice:");
                  wattroff(GetListUserWin, COLOR_PAIR(4));
                  wrefresh(GetListUserWin);
               }
            } else if (n == 10) {
               move(13, 2);
               clrtoeol();
               continue;
            } else if (n == 49)
            {
               ChatView::interactUserMenu(counter, User, sock);
               ChatView::ChatConsole(joinedRoom,sock);
            }
            else if (n == 50)
            {
               ChatView::ChatConsole(joinedRoom,sock);
            }
            else  {
               c = char(n);
               mvwprintw(GetListUserWin, curs_y, curs_x,"%s", ch);
               wrefresh(GetListUserWin);
               input.push_back(c);
               curs_x++;
            }
         } while (true);
      }
      else  {
         c = char(n);
         mvwprintw(ChatConsoleWin, curs_y, curs_x,"%s", ch);
         wrefresh(ChatConsoleWin);
         input.push_back(c);
         curs_x++;
      }
   } while (true);
}

void popup(string text) {
   ChatView::ChangeBarText(text.c_str());
   sleep(1);
   ChatView::ResetBar(text.length());
}

void ChatView::ChooseRoom(int sock, int count) {
   clear();
   refresh();
   endwin();
   currentView = JoinRoom;
   int exitValue;
   system("clear");
   string input;
   string room;
   // Start regigter window design
   initscr();
   start_color();
   if(has_colors() == FALSE)
   {
      clear();
      endwin();
      printf("Your terminal does not support color\n");
      exit(1);
   }
   init_pair(1, COLOR_YELLOW, COLOR_BLACK);
   init_pair(2, COLOR_RED, COLOR_BLACK);
   init_pair(3, COLOR_GREEN, COLOR_BLACK);
   init_pair(4, COLOR_CYAN, COLOR_BLACK);
   init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
   init_pair(6, COLOR_WHITE, COLOR_BLACK);
   noecho();

   WINDOW *joinroomWin = newwin(15, 61, 0, 0);
   currentWin = joinroomWin;
   wattron(joinroomWin, COLOR_PAIR(3));
   box(joinroomWin, 0, 0);
   wattroff(joinroomWin, COLOR_PAIR(3));
   refresh();
   mvwprintw(joinroomWin, 1, 1, "1.Public room");
   mvwprintw(joinroomWin, 2, 1, "2.Private room");
   wattron(joinroomWin, COLOR_PAIR(4));
   mvwprintw(joinroomWin, 4, 1, "Select room type you want to join");
   wattroff(joinroomWin, COLOR_PAIR(4));
   wrefresh(joinroomWin);
   int curs_x, curs_y;
   curs_x = 34;
   curs_y = 12;
   int sig;
   char sigchar = char(sig);
   sig = wgetch(joinroomWin);
   if(sig == 27) {
         clear();
         endwin();
         ChatView::handleMenu(sock, -1);
   }
   else if(sig == 49){
      // for (auto i: foundRoom) {
      //    cout << i.first << "|" << i.second.getRoomName() << endl;
      // }
      wclear(joinroomWin);
      wattron(joinroomWin, COLOR_PAIR(3));
      box(joinroomWin, 0, 0);
      wattroff(joinroomWin, COLOR_PAIR(3));
      refresh();
      rowRoom = 1;
      colRoom = 1;
      for(int i = 1; i<= foundRoom.size(); i++) {
         numRoom = i;
         mvwprintw(joinroomWin, rowRoom++, colRoom, "%d. %s" , i, foundRoom[i].getRoomName().c_str());
         wrefresh(joinroomWin);
         if(rowRoom == 10){
            rowRoom = 1;
            colRoom = colRoom + 30;
         }
      }

      // ChatService::touchUpdate(true);
      // thread checkUpdate(ChatService::GetValueFromServer, sock, "RELOAD_ROOM");
      wmove(joinroomWin, curs_x, curs_y);
      wattron(joinroomWin, COLOR_PAIR(4));
      mvwprintw(joinroomWin, 12, 1, "Select room you want to join : ");
      wattroff(joinroomWin, COLOR_PAIR(4));
      wrefresh(joinroomWin);
      int n;
      char c;
      bool F;
      do
      {
         curs_set(1);
         n = wgetch(joinroomWin);
         if(n == 27)
         {
            clear();
            endwin();
            ChatView::ChooseRoom(sock,  count);
         } else if (n == 8 || n == 127) { // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
            if (input.length() != 0){
               input.pop_back();
               wprintw(joinroomWin, "\b \b");
               wrefresh(joinroomWin);
               curs_x--;
            }
            // else if(input.length() == 0){
            //    wattron(joinroomWin, COLOR_PAIR(4));
            //    mvwprintw(joinroomWin, 12, 1, "Select room you want to join : ");
            //    wattroff(joinroomWin, COLOR_PAIR(4));
            //    wrefresh(joinroomWin);
            // }
         } else if (n == 10 && input.length() == 0) {
            move(12, 1);
            clrtoeol();
            wattron(joinroomWin, COLOR_PAIR(4));
            mvwprintw(joinroomWin, 12, 1, "Select room you want to join : ");
            wattroff(joinroomWin, COLOR_PAIR(4));
            wrefresh(joinroomWin);
            continue;
         } else if(n == 10 && input.length() != 0){
            int No;
            No = stoi(input);
            room = std::to_string(foundRoom[No].getId());
            F = ChatService::joinRoom(sock, room, joinedRoom);
            if(!F) {
               //cout << "\033[0;31mYour option is not valid. Please select again\033[0m" << endl;
               wattron(joinroomWin, COLOR_PAIR(1));
               mvwprintw(joinroomWin, 13, 1, "Your option is not valid. Please select again");
               wattroff(joinroomWin, COLOR_PAIR(1));
               wrefresh(joinroomWin);
               sleep(2);
               wmove(joinroomWin, 13, 1);
               wclrtoeol(joinroomWin);
               wmove(joinroomWin, 12, 1);
               wclrtoeol(joinroomWin);
               wattron(joinroomWin, COLOR_PAIR(3));
               box(joinroomWin, 0, 0);
               wattroff(joinroomWin, COLOR_PAIR(3));
               wattron(joinroomWin, COLOR_PAIR(4));
               mvwprintw(joinroomWin, 12, 1, "Select room you want to join : ");
               wattroff(joinroomWin, COLOR_PAIR(4));
               curs_x = 34;
               curs_y = 12;
               input = "";
               wmove(joinroomWin, curs_y, curs_x);
               wrefresh(joinroomWin);
               // ChatView::ChangeBarText(std::to_string(foundRoom.size()));
               // ChatView::ChooseRoom(sock , count);
            } else {
               F = true;
               break;
            }
         } else if ( n >= 48 && n <= 57)  {
            c = char(n);
            mvwprintw(joinroomWin, curs_y, curs_x, "%c", c);// print the current key on screen and push it to input string
            wrefresh(joinroomWin);
            input.push_back(c);
            curs_x++;
         }
      } while (!F);

      if (n == 27)
      {
         ChatView::handleMenu(sock, -1);
      }

      // do {
      //    int No;
      //    No = stoi(input);
      //    room = std::to_string(foundRoom[No].getId());
      //    F = ChatService::joinRoom(sock, room, joinedRoom);
      //    if(!F) {
      //       //cout << "\033[0;31mYour option is not valid. Please select again\033[0m" << endl;
      //       wattron(joinroomWin, COLOR_PAIR(1));
      //       mvwprintw(joinroomWin, 13, 1, "Your option is not valid. Please select again");
      //       wattroff(joinroomWin, COLOR_PAIR(1));
      //       wrefresh(joinroomWin);
      //       sleep(2);
      //       move(13, 1);
      //       clrtoeol();
      //       refresh();
      //       // ChatView::ChangeBarText(std::to_string(foundRoom.size()));
      //       // ChatView::ChooseRoom(sock , count);
      //    }
      // } while(!F);
      // ChatService::touchUpdate(false);
      // checkUpdate.join();
      ChattingView(sock, joinedRoom);
   }
   else if(sig == 50){
      wclear(joinroomWin);
      wattron(joinroomWin, COLOR_PAIR(3));
      box(joinroomWin, 0, 0);
      wattroff(joinroomWin, COLOR_PAIR(3));
      refresh();
      bool isSuccess = false;
      int countP = 0;
      int rowp = 1;
      int colp = 1;
      vector<ChatClient> listChatRooms;
      ChatService::GetListPrivateRoom(sock,listChatRooms,isSuccess);
      map<int, ChatClient> foundPrivateRoom;
      if(isSuccess){
        for(auto& listChatRooms : listChatRooms){
            foundPrivateRoom[++countP] = listChatRooms;
        }
         for(int i = 1; i<= countP;i++) {
            mvwprintw(joinroomWin, rowp++, colp, "%d. %s" , i, foundPrivateRoom[i].getRoomName().c_str());
            wrefresh(joinroomWin);
            if(rowp == 10){
            rowp = 1;
            colp = colp + 30;
            }
         }
      }
      else{
         mvwprintw(joinroomWin, 13, 1, "Your option is not valid");
      }
         wmove(joinroomWin, curs_x, curs_y);
         wattron(joinroomWin, COLOR_PAIR(4));
         mvwprintw(joinroomWin, 12, 1, "Select room you want to join : ");
         wattroff(joinroomWin, COLOR_PAIR(4));
         wrefresh(joinroomWin);
         int np;
         char cp ;
         char *chp = &cp;
         input ="";
         do
         {
            np = wgetch(joinroomWin);
            if(np == 27)
            {
               clear();
               endwin();
               ChatView::ChooseRoom(sock,  count);
            } else if (np == 8 || np == 127) { // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
               if (input.length() != 0){
                  input.pop_back();
                  wprintw(joinroomWin, "\b \b");
                  wrefresh(joinroomWin);
                  curs_x--;
               }
               else if(input.length() == 0){
                  wattron(joinroomWin, COLOR_PAIR(4));
                  mvwprintw(joinroomWin, 12, 1, "Select room you want to join : ");
                  wattroff(joinroomWin, COLOR_PAIR(4));
                  wrefresh(joinroomWin);
               }
            } else if (np == 10 && input.length() == 0) {
               move(12, 1);
               clrtoeol();
               wattron(joinroomWin, COLOR_PAIR(4));
               mvwprintw(joinroomWin, 12, 1, "Select room you want to join : ");
               wattroff(joinroomWin, COLOR_PAIR(4));
               wrefresh(joinroomWin);
               continue;
            } else if(np == 10 && input.length() != 0){
               break;
            } else if ( np >= 48 && np <= 57)  {
               cp = char(np);
               mvwprintw(joinroomWin,  curs_y, curs_x, "%c", cp);// print the current key on screen and push it to input string
               wrefresh(joinroomWin);
               input.push_back(cp);
               curs_x++;
            }
         } while (true);

         if (np == 27)
         {
            ChatView::handleMenu(sock, -1);
         }

         else {
            int NoP;
            string roomP;
            NoP = stoi(input);
            roomP = std::to_string(foundPrivateRoom[NoP].getId());
            if(NoP < 0 || NoP > countP) {
               //cout << "\033[0;31mYour option is not valid. Please select again\033[0m" << endl;
               wattron(joinroomWin, COLOR_PAIR(1));
               mvwprintw(joinroomWin, 13, 1, "Your option is not valid. Please select again ");
               wattroff(joinroomWin, COLOR_PAIR(1));
               wrefresh(joinroomWin);
               sleep(2);
               move(13, 1);
               clrtoeol();
               refresh();
               ChatView::ChooseRoom(sock , count);
            }
            else{
               ChatService::joinPrivateRoom(sock, roomP, joinedRoom);
               ChatView::ChattingView(sock, joinedRoom);
            }
         }
         // ChatService::touchUpdate(false);
         // checkUpdate.join();
         ChattingView(sock, joinedRoom);
      }

   else{
      ChatView::ChooseRoom(sock , count);
   }
}


void ChatView::ChattingView(int sock, ChatClient room) {
   //UI Code Chat
   currentView = Chatting;
   clear();
   refresh();
   endwin();
   // string icon = "✔";
   initscr();
   noecho();
   int height, width, start_X, start_Y;
   height = stdscr->_maxy;
   width = stdscr->_maxx;
   start_X = start_Y = 1;
   WINDOW * chattingMenu = newwin(height - 3, width - 1, start_Y, start_X);
   // mvwprintw(chattingMenu, 10, 10, icon.c_str());
   keypad(chattingMenu, TRUE);
   currentWin = chattingMenu;
   box(chattingMenu, 0, 0);
   refresh();
   ChatRoomConsole(room, chattingMenu);
   ChatService::GetListChat(sock, room);
   ChatService::clearStack();
   int count;
   ChatView::displayChat(room, chattingMenu, count);
   wrefresh(chattingMenu);
   ChatService::MessageSend(sock, chattingMenu, count);
   ChatView::ChatConsole(joinedRoom, sock);
}

//test

void ChatView::UserUI(int sock, UserClient user,WINDOW *interactUserMenuWin) {

   clear();
   refresh();
   endwin();
   currentView = OrtherUserProfile;
   bool exit = false;
   int exitValue;
   system("clear");
   string input;
   // Start regigter window design
   initscr();
   start_color();
   init_pair(1, COLOR_YELLOW, COLOR_BLACK);
   init_pair(2, COLOR_RED, COLOR_BLACK);
   init_pair(3, COLOR_GREEN, COLOR_BLACK);
   init_pair(4, COLOR_CYAN, COLOR_BLACK);
   init_pair(5, COLOR_MAGENTA, COLOR_BLACK);
   init_pair(6, COLOR_WHITE, COLOR_BLACK);
   noecho();
   if(has_colors() == FALSE)
   {
      clear();
      endwin();
      printf("Your terminal does not support color\n");
      //exit(1);
   }
   echo();
   curs_set(1);
   WINDOW *OrtherUserProfileWin = newwin(20, 61, 0, 0);
   wattron(OrtherUserProfileWin, COLOR_PAIR(3));
   box(OrtherUserProfileWin, 0, 0);
   wattroff(OrtherUserProfileWin, COLOR_PAIR(3));
   int count = 1;
   refresh();
   int curs_x, curs_y;
   curs_x = 33;
   curs_y = 12;
   wmove(OrtherUserProfileWin, curs_x, curs_y);

   // change int to char *
   int n;
   char c = char(n);
   char *ch = &c;
   wattron(OrtherUserProfileWin, COLOR_PAIR(4));
   mvwprintw(OrtherUserProfileWin, 1, 11, "--------User Profile Options--------");
   wattroff(OrtherUserProfileWin, COLOR_PAIR(3));
   mvwprintw(OrtherUserProfileWin, 3, 1, "1. User Information");
   mvwprintw(OrtherUserProfileWin, 4, 1, "2. Create Chat");
   mvwprintw(OrtherUserProfileWin, 5, 1, "3. Exit");
   wattron(OrtherUserProfileWin, COLOR_PAIR(4));
   mvwprintw(OrtherUserProfileWin, 7, 1, "Select your option:");
   wattroff(OrtherUserProfileWin, COLOR_PAIR(4));
   wrefresh(OrtherUserProfileWin);
   bool F;
   do
   {
      // Get the key
      n = wgetch(OrtherUserProfileWin);
      // if get Esc(27), it will turn back to login menu
      if(n == 27) // back to login menu
      {
         clear();
         endwin();
         ChatView::handleMenu(sock, -1);
      } else if (n == 8 || n == 127) { // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
         if (input.length() != 0) // check if input length == 0 then no need to pop from string input
         {
            input.pop_back();
            wprintw(OrtherUserProfileWin, " \b");
            wrefresh(OrtherUserProfileWin);
            //curs_y--;
         }
         else if(input.length() == 0){
            wattron(OrtherUserProfileWin, COLOR_PAIR(4));
            mvwprintw(OrtherUserProfileWin, 7, 1, "Select your option:");
            wattroff(OrtherUserProfileWin, COLOR_PAIR(4));
            wrefresh(OrtherUserProfileWin);
         }
      } else if (n == 10 && input.length() == 0 ) { // User name must be at least 3 characters
         move(7, 1);
         clrtoeol();
         wattron(OrtherUserProfileWin, COLOR_PAIR(4));
         mvwprintw(OrtherUserProfileWin, 7, 1, "Select your option:");
         wattroff(OrtherUserProfileWin, COLOR_PAIR(4));
         wrefresh(OrtherUserProfileWin);
         continue;
      } else if(n == 10 && input.length() != 0){
         break;
      } else  { // User name just contains A-Z, a-z, 0-9, '_', can't start with a number, and must >= 3 characters
         c = char(n);
         //printw("X: %d, Y: %d", curs_x, curs_y);
         //refresh();
         mvwprintw(OrtherUserProfileWin, 34, 12,"%s", ch);// print the current key on screen and push it to input string
         wrefresh(OrtherUserProfileWin);
         input.push_back(c);
         //curs_x++;
      }
   } while (true);

   if (n == 27)
   {
      ChatView::handleMenu(sock, -1);
   }

   else {
      string select = input;
      //int n;
      //n = stoi(select);
         if(select == "1")  {
            int sig;
            char sig1 = char(sig);
            ChatService::GetUserProfile(sock, user,OrtherUserProfileWin);
            while(true){
               wattron(OrtherUserProfileWin, COLOR_PAIR(4));
               mvwprintw(OrtherUserProfileWin, 18, 1, "Press Enter to continue");
               wattroff(OrtherUserProfileWin, COLOR_PAIR(4));
               sig = wgetch(OrtherUserProfileWin);
               if(sig == 10){
                  ChatView::UserUI(sock,user,interactUserMenuWin);
                  break;
               }
            }
         }
         if(select == "2"){
            ChatService::joinPrivateRoom(sock, std::to_string(user.getId()), joinedRoom);
            ChatView::ChattingView(sock, joinedRoom);
         }
         if(select == "3"){
            ChatView::handleMenu(sock, -1);
          }
      }

   if (n == 27)
   {
      ChatView::handleMenu(sock, -1);
   }
}

ChatView::View ChatView::GetCurrentView() {
   return currentView;
}

void ChatView::ChangeBarText(string text) {
   noecho();
   start_color();
   init_pair(1, COLOR_BLACK, COLOR_YELLOW);
   statusBar.setText(" " + text + " ", COLOR_PAIR(1));
   statusBar.draw();
   refresh();
   wrefresh(statusBar.getParent());
}

void ChatView::ResetBar(int length) {
   string resetStr = "";
   for(int i=0; i < length; i ++) {
      resetStr += " ";
   }
   start_color();
   init_pair(1, COLOR_WHITE, COLOR_BLACK);
   statusBar.setText(" " + resetStr + " ", COLOR_PAIR(1));
   statusBar.draw();
   refresh();
   wrefresh(statusBar.getParent());
}

void ChatView::ReloadView(View view, int sock) {
   switch (view) {
      case 0:
         endwin();
         ChatView::handleLogin(sock, -1);
         break;
      case 1:
         endwin();
         ChatView::handleMenu(sock, -1);
         break;
      case 2:
         ChatView::handleMenu(sock, 110);
         break;
      case 3:
         ChatView::handleMenu(sock, 106);
         break;
      case 4:
         ChatView::handleMenu(sock, 102);
         break;
      case 5:
         ChatView::handleMenu(sock, 117);
         break;
      case 6:
         //ChatView::ChattingView(sock, joinedRoom);
         break;
      case 7:
         //ChatView::ChatConsole(joinedRoom, sock);
         break;
      case 8:
         // ChatView::UserUI(sock, user)
         break;
      case 9:
         endwin();
         ChatView::handleLogin(sock, 114);
         break;
      case 10:
         endwin();
         ChatView::handleLogin(sock, 108);
         break;
      default:
         break;
   }
}

UserClient ChatView::getUser() {
   return currentUser;
}

ChatClient ChatView::getJoinedRoom() {
   return joinedRoom;
}

WINDOW * ChatView::getCurrentWin() {
   return currentWin;
}

int ChatView::getRowRoom() {
   return rowRoom;
}

void ChatView::setRowRoom(int pos) {
   rowRoom = pos;
}

int ChatView::getColRoom() {
   return colRoom;
}

void ChatView::setColRoom(int pos) {
   colRoom = pos;
}

int ChatView::getNumRoom() {
   return numRoom;
}

void ChatView::setNumRoom(int pos) {
   numRoom = pos;
}