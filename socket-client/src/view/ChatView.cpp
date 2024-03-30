// Copyright [2024] <Anh Dung>
#include "ChatView.h"
#include "../services/ChatService.h"
#include <chrono>
#include <iomanip>
#include "locale.h"
#include "ncurses.h"

using std::cin;
using std::cout;
using std::endl;
using std::string;
using std::thread;
using std::vector;

#define ctrl(x) (x & 0x1F)
bool endInput;
thread readThread;
ChatView::View currentView;
UserClient currentUser;
StatusBar statusBar;
string addr = ChatService::GetAddress();
const char *ADDRESS = addr.c_str();
const int PORT = ChatService::GetPort();
WINDOW *currentWin;
bool atTop = false;
bool isEmptyChat = true;

void ChatView::UI()
{
   char buffer[1024];
   int sock = ChatService::handleConnect(ADDRESS, PORT);
   readThread = thread(ChatService::HandleReceive, sock, buffer);
   ChatService::RequestSend("ACTIVATE_THREAD", sock);
   while (true)
   {
      ChatView::handleLogin(sock, -1);
   }
}

void ChatView::LoginMenu()
{
   currentView = Login;
   initscr();
   keypad(stdscr, TRUE);
   cbreak();
   if (has_colors() == FALSE)
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

   WINDOW *loginMenu = newwin(height, width, start_y, start_x);
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

void sigHandlerView(int signal)
{
   endInput = true;
}

void ChatView::handleLogin(int sock, int selection)
{
   int n;
   int test;
   LoginMenu();
   n = (selection != -1 ? selection : getch());
   printw("%d", n);
   noecho();

   switch (n)
   {
   case 114: // 114 is R key
   {
      clear();
      endwin();
      currentView = LoginInput;
      std::system("clear");
      string username;
      string password;
      // sendrequest with partern for register
      bool isSuccess = false;
      bool isUserAvailable = true;
      // PDHM
      std::string input;
      // Start regigter window design
      initscr();
      keypad(stdscr, TRUE);
      cbreak();
      if (has_colors() == FALSE)
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
            if (n == 27) // back to login menu
            {
               curs_set(0);
               clear();
               endwin();
               handleLogin(sock, -1);
               break;
            }
            else if (n == 8 || n == 127)
            {                           // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
               if (input.length() != 0) // check if input length == 0 then no need to pop from string input
               {
                  input.pop_back();
                  wprintw(nameWin, "\b \b");
                  wrefresh(nameWin);
                  curs_x--;
               }
            }
            else if (n >= 97 && n <= 122 || n >= 65 && n <= 90 || n == 95 || n == 124 || n >= 48 && n <= 57 && input.length() != 0)
            { // User name just contains A-Z, a-z, 0-9, '_', can't start with a number, and must >= 3 characters
               c = char(n);
               mvwprintw(nameWin, curs_y, curs_x, "%c", c); // print the current key on screen and push it to input string
               wrefresh(nameWin);
               input.push_back(c);
               curs_x++;
            }
            else if (n == 10 && input.length() >= 3)
            { // User name must be at least 3 characters
               break;
            }
            else if (n >= 48 && n <= 57 && input.length() == 0)
            { // User name can't start with number
               getyx(nameWin, curs_y, curs_x);
               curs_set(0);
               attron(COLOR_PAIR(1));
               mvprintw(20, 2, "User name can't start with number! Press ENTER to continue.");
               attroff(COLOR_PAIR(1));
               refresh();
               while (getch() != 10)
               {
               }
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
            else if (n == 10 && input.length() < 3)
            { // User name must be at least 3 characters
               getyx(nameWin, curs_y, curs_x);
               curs_set(0);
               attron(COLOR_PAIR(1));
               mvprintw(20, 2, "User name must be at least 3 characters! Press ENTER to continue.");
               attroff(COLOR_PAIR(1));
               refresh();
               while (getch() != 10)
               {
               }
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
            if (!isUserAvailable)
            {
               curs_set(0);
               attron(COLOR_PAIR(1));
               mvprintw(20, 2, "Username is already available! Press ENTER to continue\n");
               attroff(COLOR_PAIR(1));
               refresh();
               while (getch() != 10)
               {
               }
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
         input = "";                     // empty th input
         c = '*';                        // hide password
         while (true)
         {
            // Handle input password
            do
            {
               curs_set(1);
               n = wgetch(passWin);
               // if press escape then return back to login menu
               if (n == 27)
               {
                  curs_set(0);
                  clear();
                  endwin();
                  handleLogin(sock, -1);
                  break;
               }
               else if (n == 8 || n == 127)
               { // backspace
                  if (input.length() != 0)
                  {
                     input.pop_back();
                     wprintw(passWin, "\b \b");
                     wrefresh(passWin);
                     curs_x--;
                  }
               }
               else if (n == 10 && input.length() >= 8)
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
                  while (getch() != 10)
                  {
                  }
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
               }
               else if (n == ctrl('a'))
               {
                  isAppear = TRUE;
                  wmove(passWin, curs_y, curs_x - input.length());
                  wclrtoeol(passWin);
                  box(passWin, 0, 0);
                  wrefresh(passWin);
                  mvwprintw(passWin, curs_y, curs_x - input.length(), "%s", input.c_str());
               }
               else if (n == ctrl('d'))
               {
                  isAppear = FALSE;
                  wmove(passWin, curs_y, curs_x - input.length());
                  wclrtoeol(passWin);
                  box(passWin, 0, 0);
                  wrefresh(passWin);
                  mvwprintw(passWin, curs_y, curs_x - input.length(), "%s", string(input.length(), '*').c_str());
               }
               else
               {
                  if (isAppear)
                  {
                     c = char(n);
                  }
                  else
                  {
                     c = '*';
                  }
                  mvwprintw(passWin, curs_y, curs_x, "%s", ch);
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
                  while (getch() != 10)
                  {
                  }
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
                  if (!isSuccess)
                  {
                     mvprintw(20, 2, "Register Failed!");
                     mvprintw(21, 2, "Press any key to continue!");
                     clear();
                     endwin();
                     handleLogin(sock, -1);
                     break;
                  }
                  else
                  {
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
      std::system("clear");
      string username;
      string password;
      // sendrequest with partern for register
      bool isSuccess = false;
      bool isUserAvailable = true;
      // pdhm
      initscr();
      keypad(stdscr, TRUE);
      cbreak();
      noecho();
      curs_set(1);
      if (has_colors() == FALSE)
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
            if (sig == 27) // back to login menu
            {
               clear();
               endwin();
               handleLogin(sock, -1);
               break;
            }
            else if (sig == 8 || sig == 127)
            {                           // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
               if (input.length() != 0) // check if input length == 0 then no need to pop from string input
               {
                  curs_set(1);
                  input.pop_back();
                  wprintw(nameWin, "\b \b");
                  wrefresh(nameWin);
                  curs_x--;
               }
            }
            else if (sig >= 97 && sig <= 122 || sig >= 65 && sig <= 90 || sig == 95 || sig == 124 || sig >= 48 && sig <= 57 && input.length() != 0)
            { // User name just contains A-Z, a-z, 0-9, '_', can't start with a number, and must >= 3 characters
               curs_set(1);
               c = char(sig);
               mvwprintw(nameWin, curs_y, curs_x, "%c", c); // print the current key on screen and push it to input string
               wrefresh(nameWin);
               input.push_back(c);
               curs_x++;
            }
            else if (sig == 10 && input.length() >= 3)
            { // User name must be at least 3 characters
               break;
            }
            else if (sig >= 48 && sig <= 57 && input.length() == 0)
            { // User name can't start with number
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
            else if (sig == 10 && input.length() < 3)
            { // User name must be at least 3 characters
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
            if (!isUserAvailable)
            {
               curs_set(0);
               attron(COLOR_PAIR(1));
               mvprintw(14, 3, "Username is unavailable! Press Enter to continue\n");
               attroff(COLOR_PAIR(1));
               refresh();
               if (getch() == 10)
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
            else
            {
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
         input = "";                     // empty th input
         c = '*';                        // hide password
         bool isAppear = false;
         while (true)
         {
            // Handle input password
            do
            {
               curs_set(1);
               sig = wgetch(passWin);
               if (sig == 27)
               {
                  curs_set(0);
                  clear();
                  endwin();
                  handleLogin(sock, -1);
                  break;
               }
               else if (sig == 8 || sig == 127)
               { // backspace
                  if (input.length() != 0)
                  {
                     input.pop_back();
                     wprintw(passWin, "\b \b");
                     wrefresh(passWin);
                     curs_x--;
                  }
               }
               else if (sig == 10)
               {
                  break;
               }
               else if (sig == ctrl('a'))
               {
                  isAppear = TRUE;
                  wmove(passWin, curs_y, curs_x - input.length());
                  wclrtoeol(passWin);
                  box(passWin, 0, 0);
                  wrefresh(passWin);
                  mvwprintw(passWin, curs_y, curs_x - input.length(), "%s", input.c_str());
               }
               else if (sig == ctrl('d'))
               {
                  isAppear = FALSE;
                  wmove(passWin, curs_y, curs_x - input.length());
                  wclrtoeol(passWin);
                  box(passWin, 0, 0);
                  wrefresh(passWin);
                  mvwprintw(passWin, curs_y, curs_x - input.length(), "%s", string(input.length(), '*').c_str());
               }
               else
               {
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
                  if (!isSuccess && countFailure < 3)
                  {
                     curs_set(0);
                     attron(COLOR_PAIR(1));
                     mvprintw(14, 3, "Wrong password. Please try again");
                     mvprintw(15, 3, "Press ENTER to continue.");
                     attroff(COLOR_PAIR(1));
                     refresh();
                     while (getch() != 10)
                     {
                     }
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
                  else if (isSuccess)
                  {
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
      ChatView::handleLogin(sock, -1);
   }
}

void ChatView::Menu()
{
   currentView = Main;
   clear();
   endwin();
   initscr();
   keypad(stdscr, TRUE);
   cbreak();
   if (has_colors() == FALSE)
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
   WINDOW *userMenu = newwin(height, width, start_Y, start_X);
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

void ChatView::UserConsole(int sock)
{
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
   if (has_colors() == FALSE)
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
   do
   {
      input = "";
      n = wgetch(updateWin);
      if (n == 97)
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 16, 3, "New Address: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ChatView::UserConsole(sock);
            }
            else if (n == 8 || n == 127)
            {
               if (input.length() != 0)
               {
                  input.pop_back();
                  wprintw(updateWin, "\b \b");
                  wrefresh(updateWin);
                  curs_x--;
               }
            }
            else if (n == 10)
            {
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
               while (getch() != 10)
               {
               }
               refresh();
               printf("\n");
               return ChatView::UserConsole(sock);
            }
            else
            {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(c);
               curs_x++;
            }
         } while (true);
      }
      if (n == 103)
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 16, 3, "New gender: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ChatView::UserConsole(sock);
            }
            else if (n == 8 || n == 127)
            {
               if (input.length() != 0)
               {
                  input.pop_back();
                  wprintw(updateWin, "\b \b");
                  wrefresh(updateWin);
                  curs_x--;
               }
            }
            else if (n == 10)
            {
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
               while (getch() != 10)
               {
               }
               refresh();
               printf("\n");
               return ChatView::UserConsole(sock);
            }
            else
            {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(c);
               curs_x++;
            }
         } while (true);
      }
      if (n == 112)
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 16, 3, "New password: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ChatView::UserConsole(sock);
            }
            else if (n == 8 || n == 127)
            {
               if (input.length() != 0)
               {
                  input.pop_back();
                  wprintw(updateWin, "\b \b");
                  wrefresh(updateWin);
                  curs_x--;
               }
            }
            else if (n == 10)
            {
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
               while (getch() != 10)
               {
               }
               refresh();
               printf("\n");
               return ChatView::UserConsole(sock);
            }
            else
            {
               c = '*';
               mvwprintw(updateWin, curs_y, curs_x + 1, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(char(n));
               curs_x++;
            }
         } while (true);
      }
      if (n == 27)
      {
         clear();
         endwin();
         break;
      }
      if (n == 10)
      {
         break;
      }
   } while (true);
}

int ChatView::selectMenu()
{
   int n;
   Menu();
   // cout << "\n\033[1;32mSelect your choice: \033[0m";
   // cin >> n;
   n = getch();
   noecho();
   printw("%d", n);
   // ChatService::HandleInput();
   // cin.ignore();
   if (n > 0 || n < 99)
   {
      return n;
   }
   else
      return selectMenu();
}

void ChatView::interactUserMenu(int count, map<int, UserClient> foundUser, int sock)
{
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
   if (has_colors() == FALSE)
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
      if (n == 27) // back to login menu
      {
         clear();
         endwin();
         break;
      }
      else if (n == 8 || n == 127)
      { // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
         // ChatView::ChangeBarText(std::to_string(input.length()));
         if (input.length() != 0) // check if input length == 0 then no need to pop from string input
         {
            input.pop_back();
            wprintw(interactUserMenuWin, " \b");
            wrefresh(interactUserMenuWin);
            curs_x--;
         }
         else if (input.length() == 0)
         {
            wattron(interactUserMenuWin, COLOR_PAIR(4));
            mvwprintw(interactUserMenuWin, 1, 1, "Select user you want to interact: ");
            wattroff(interactUserMenuWin, COLOR_PAIR(4));
            wrefresh(interactUserMenuWin);
         }
      }
      else if (n == 10 && input.length() == 0)
      {
         move(1, 1);
         clrtoeol();
         wattron(interactUserMenuWin, COLOR_PAIR(4));
         mvwprintw(interactUserMenuWin, 1, 1, "Select user you want to interact: ");
         wattroff(interactUserMenuWin, COLOR_PAIR(4));
         wrefresh(interactUserMenuWin);
         continue;
      }
      else if (n == 10 && input.length() != 0)
      {
         break;
      }
      else
      {
         c = char(n);
         mvwprintw(interactUserMenuWin, curs_y, curs_x, "%c", c);
         wrefresh(interactUserMenuWin);
         input.push_back(c);
         curs_x++;
      }
   } while (true);

   if (n == 27)
   {
      ChatView::handleMenu(sock, -1);
   }
   else
   {
      selectValue = input;
      int choice;
      choice = stoi(selectValue);
      if (choice == count)
      {
         handleMenu(sock, -1);
      }
      else if (choice <= 0 || choice > count)
      {
         // cout << "Invalid choice!\n";
         wattron(interactUserMenuWin, COLOR_PAIR(1));
         mvwprintw(interactUserMenuWin, 2, 2, "Invalid choice! ");
         wattroff(interactUserMenuWin, COLOR_PAIR(1));
         wrefresh(interactUserMenuWin);
         // sleep(1);
         ChatView::interactUserMenu(count, foundUser, sock);
      }
      else
      {
         UserUI(sock, foundUser[choice], interactUserMenuWin);
      }
   }
}

void ChatView::handleMenu(int sock, int selection)
{
   bool isSuccess = false;

   do
   {
      if (selection < 0)
      {
         selection = selectMenu();
      }
      switch (selection)
      {
      case 102: // J
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
         if (has_colors() == FALSE)
         {
            clear();
            endwin();
            printf("Your terminal does not support color\n");
            exit(1);
         }
         // echo();
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
            if (n == 27) // back to login menu
            {
               clear();
               endwin();
               break;
            }
            else if (n == 8 || n == 127)
            { // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
               if (input.length() != 0)
               {
                  input.pop_back();
                  wprintw(finduserWin, "\b \b");
                  wrefresh(finduserWin);
                  curs_x--;
               }
               else if (input.length() == 0)
               {
                  wattron(finduserWin, COLOR_PAIR(4));
                  mvwprintw(finduserWin, 1, 1, "Enter search value: ");
                  wattroff(finduserWin, COLOR_PAIR(4));
                  wrefresh(finduserWin);
               }
            }
            else if (n == 10 && input.length() != 0)
            {
               break;
            }
            else
            {
               c = char(n);
               mvwprintw(finduserWin, curs_y, curs_x, "%c", c); // print the current key on screen and push it to input string
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
         else
         {
            searchValue = input;
            ChatService::RequestSend("FIND_USER|" + searchValue, sock);
            int count = 0;
            // mvwprintw(finduserWin, 2, 2, "countname");
            map<int, UserClient> foundUser = ChatService::GetFoundUser(sock, currentUser, count, finduserWin);
            ChatView::interactUserMenu(count, foundUser, sock);
         }
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
   } while (!isSuccess);
}

void ChatView::UserUI(int sock, UserClient user, WINDOW *interactUserMenuWin)
{

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
   if (has_colors() == FALSE)
   {
      clear();
      endwin();
      printf("Your terminal does not support color\n");
      // exit(1);
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
      if (n == 27) // back to login menu
      {
         clear();
         endwin();
         ChatView::handleMenu(sock, -1);
      }
      else if (n == 8 || n == 127)
      {                           // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
         if (input.length() != 0) // check if input length == 0 then no need to pop from string input
         {
            input.pop_back();
            wprintw(OrtherUserProfileWin, " \b");
            wrefresh(OrtherUserProfileWin);
            // curs_y--;
         }
         else if (input.length() == 0)
         {
            wattron(OrtherUserProfileWin, COLOR_PAIR(4));
            mvwprintw(OrtherUserProfileWin, 7, 1, "Select your option:");
            wattroff(OrtherUserProfileWin, COLOR_PAIR(4));
            wrefresh(OrtherUserProfileWin);
         }
      }
      else if (n == 10 && input.length() == 0)
      { // User name must be at least 3 characters
         move(7, 1);
         clrtoeol();
         wattron(OrtherUserProfileWin, COLOR_PAIR(4));
         mvwprintw(OrtherUserProfileWin, 7, 1, "Select your option:");
         wattroff(OrtherUserProfileWin, COLOR_PAIR(4));
         wrefresh(OrtherUserProfileWin);
         continue;
      }
      else if (n == 10 && input.length() != 0)
      {
         break;
      }
      else
      { // User name just contains A-Z, a-z, 0-9, '_', can't start with a number, and must >= 3 characters
         c = char(n);
         // printw("X: %d, Y: %d", curs_x, curs_y);
         // refresh();
         mvwprintw(OrtherUserProfileWin, 34, 12, "%s", ch); // print the current key on screen and push it to input string
         wrefresh(OrtherUserProfileWin);
         input.push_back(c);
         // curs_x++;
      }
   } while (true);

   if (n == 27)
   {
      ChatView::handleMenu(sock, -1);
   }

   else
   {
      string select = input;
      // int n;
      // n = stoi(select);
      if (select == "1")
      {
         int sig;
         char sig1 = char(sig);
         ChatService::GetUserProfile(sock, user, OrtherUserProfileWin);
         while (true)
         {
            wattron(OrtherUserProfileWin, COLOR_PAIR(4));
            mvwprintw(OrtherUserProfileWin, 18, 1, "Press Enter to continue");
            wattroff(OrtherUserProfileWin, COLOR_PAIR(4));
            sig = wgetch(OrtherUserProfileWin);
            if (sig == 10)
            {
               ChatView::UserUI(sock, user, interactUserMenuWin);
               break;
            }
         }
      }
      if (select == "3")
      {
         ChatView::handleMenu(sock, -1);
      }
   }

   if (n == 27)
   {
      ChatView::handleMenu(sock, -1);
   }
}

ChatView::View ChatView::GetCurrentView()
{
   return currentView;
}

void ChatView::ReloadView(View view, int sock)
{
   switch (view)
   {
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
      endwin();
      ChatView::handleLogin(sock, 114);
      break;
   case 7:
      endwin();
      ChatView::handleLogin(sock, 108);
      break;
   default:
      break;
   }
}

UserClient ChatView::getUser()
{
   return currentUser;
}

WINDOW *ChatView::getCurrentWin()
{
   return currentWin;
}