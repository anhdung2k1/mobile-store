// Copyright [2024] <Anh Dung>
#include "ClientView.h"
#include "../services/ClientService.h"

using std::cin;
using std::cout;
using std::endl;
using std::string;
using std::thread;
using std::vector;

#define ctrl(x) (x & 0x1F)
thread readThread;
ClientView::View currentView;
UserClient currentUser;
StatusBar statusBar;
string addr = ClientService::GetAddress();
const char *ADDRESS = addr.c_str();
const int PORT = ClientService::GetPort();
bool isAdmin = false;
WINDOW *currentWin;


// The function to read the key by value
template<typename KeyType, typename ValueType>
KeyType getKeyByValue(const std::map<KeyType, ValueType>& map, const ValueType& value) {
    for (const auto& pair : map) {
        if (pair.second == value) {
            return pair.first; // Return the key
        }
    }
    throw out_of_range("Value not found in map");
}

void ClientView::UI()
{
   char buffer[buffer_size];
   int sock = ClientService::handleConnect(ADDRESS, PORT);
   readThread = thread(ClientService::HandleReceive, sock, buffer);
   ClientService::RequestSend("ACTIVATE_THREAD", sock);
   while (true)
   {
      ClientView::handleLogin(sock, -1);
   }
}

void ClientView::LoginMenu()
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
   mvwprintw(loginMenu, 2, (getmaxx(loginMenu) - 24) / 2, ">>>>====Mobile Shop====<<<<");
   mvwprintw(loginMenu, 6, 3, "Register");
   mvwprintw(loginMenu, 8, 3, "Login");
   mvwprintw(loginMenu, 6, (getmaxx(loginMenu) - 12), "(Press R)");
   mvwprintw(loginMenu, 8, (getmaxx(loginMenu) - 12), "(Press L)");
   wattroff(loginMenu, COLOR_PAIR(4));
   wattron(loginMenu, COLOR_PAIR(2));
   mvwprintw(loginMenu, 10, 3, "Exit");
   mvwprintw(loginMenu, 10, (getmaxx(loginMenu) - 14), "(Press Esc)");
   wattroff(loginMenu, COLOR_PAIR(2));
   wrefresh(loginMenu);
}

void ClientView::handleLogin(int sock, int selection)
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
      mvwprintw(registerWin, getbegy(registerWin) + 2, (getmaxx(registerWin) - 16) / 2, "*** REGISTER ***");
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
      mvwprintw(registerWin, getbegy(registerWin) + 3, (getmaxx(registerWin) - 19) / 2, "(Press Esc to back)");
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
            curs_set(1);
            n = wgetch(nameWin);
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
            ClientService::RequestSend("REGISTER_USER|" + username, sock);
            isUserAvailable = ClientService::ConvertToBool(ClientService::GetValueFromServer(sock, "REGISTER_USER"));
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
               if (ClientService::HandlePassword(password) == FALSE)
               {
                  curs_set(0);
                  attron(COLOR_PAIR(1));
                  mvprintw(getbegy(registerWin) + 20, 5, "Password must atleast 8 characters and include alphabet, ");
                  mvprintw(getbegy(registerWin) + 21, 5, "special char !\"#$%%'()*+,-./:;<=>?@[\\]^_`{|}~ and number,");
                  mvprintw(getbegy(registerWin) + 22, 5, "ex: 4vQj*SFp. Press ENTER to continue.");
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
                  ClientService::RequestSend("REGISTER_PASSWORD|" + password, sock);
                  isSuccess = ClientService::ConvertToBool(ClientService::GetValueFromServer(sock, "REGISTER_PASSWORD"));
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
                     isAdmin = ClientService::IsAdminAuthenticated(sock);
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
      height = getbegy(stdscr) + 12;
      width = getbegx(stdscr) + 61;
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
            ClientService::RequestSend("LOGIN_USER|" + username, sock);
            isUserAvailable = ClientService::ConvertToBool(ClientService::GetValueFromServer(sock, "LOGIN_USER"));
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
                  ClientService::RequestSend("LOGIN_PASSWORD|" + password, sock);
                  isSuccess = ClientService::ConvertToBool(ClientService::GetValueFromServer(sock, "LOGIN_PASSWORD"));
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
                     isAdmin = ClientService::IsAdminAuthenticated(sock);
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
      ClientService::exitAppChat(sock);
      readThread.join();
      cout << "Exit chat app!\n";
      break;
      exit(0);
   default: // If not right key then it will not do anything
      // cout<<"Invalid choice!\n";
      ClientView::handleLogin(sock, -1);
   }
}

void ClientView::Menu()
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
   height = 24;
   width = 70;
   start_X = start_Y = 0;
   WINDOW *userMenu = newwin(height, width, start_Y, start_X);
   currentWin = userMenu;
   wattron(userMenu, COLOR_PAIR(3));
   box(userMenu, 0, 0);
   mvwprintw(userMenu, 4, 2, "------------------------------------------------------------------");
   wattroff(userMenu, COLOR_PAIR(3));
   wattron(userMenu, COLOR_PAIR(4));
   mvwprintw(userMenu, 2, (getmaxx(userMenu) - 28) / 2, ">>>>> Mobile Store Management <<<<<");
   mvwprintw(userMenu, 6, 3, "Create Mobile Instances");
   mvwprintw(userMenu, 8, 3, "Manage Mobile Inventory");
   mvwprintw(userMenu, 10, 3, "Transaction History");
   mvwprintw(userMenu, 12, 3, "List of Customers");
   mvwprintw(userMenu, 14, 3, "Find user(s)");
   mvwprintw(userMenu, 16, 3, "Update profile");
   mvwprintw(userMenu, 18, 3, "Log out");
   mvwprintw(userMenu, 6, (getmaxx(userMenu) - 11), "(Press A)");
   mvwprintw(userMenu, 8, (getmaxx(userMenu) - 11), "(Press N)");
   mvwprintw(userMenu, 10, (getmaxx(userMenu) - 11), "(Press I)");
   mvwprintw(userMenu, 12, (getmaxx(userMenu) - 11), "(Press J)");
   mvwprintw(userMenu, 14, (getmaxx(userMenu) - 11), "(Press F)");
   mvwprintw(userMenu, 16, (getmaxx(userMenu) - 11), "(Press U)");
   mvwprintw(userMenu, 18, (getmaxx(userMenu) - 11), "(Press O)");
   wattroff(userMenu, COLOR_PAIR(4));
   wattron(userMenu, COLOR_PAIR(2));
   mvwprintw(userMenu, 20, 3, "Exit");
   mvwprintw(userMenu, 20, (getmaxx(userMenu) - 13), "(Press Esc)");
   wattroff(userMenu, COLOR_PAIR(2));
   wrefresh(userMenu);
}

void ClientView::UserConsole(int sock)
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
   ClientService::RequestSend("UPDATE_PROFILE|", sock);
   ClientService::GetCurrentProfile(sock, currentUser);
   refresh();
   wattron(updateWin, COLOR_PAIR(4));
   mvwprintw(updateWin, 2, (getmaxx(updateWin) - (22 + currentUser.getName().length())) / 2, "*** Current user: %s ***", currentUser.getName().c_str());
   mvwprintw(updateWin, 6, 3, "1. Address: ");
   mvwprintw(updateWin, 8, 3, "2. Gender: ");
   mvwprintw(updateWin, 10, 3, "3. Change password ");
   mvwprintw(updateWin, 6, (getmaxx(updateWin) - 6), "(A)");
   mvwprintw(updateWin, 8, (getmaxx(updateWin) - 6), "(G)");
   mvwprintw(updateWin, 10, (getmaxx(updateWin) - 6), "(P)");
   mvwprintw(updateWin, 14, (getmaxx(updateWin) - 40) / 2, "Select an option that you want to change");
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
               return ClientView::UserConsole(sock);
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
               ClientService::RequestSend("UPDATE_ADDRESS|" + input, sock);
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
               return ClientView::UserConsole(sock);
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
               return ClientView::UserConsole(sock);
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
               ClientService::RequestSend("UPDATE_GENDER|" + input, sock);
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
               return ClientView::UserConsole(sock);
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
               return ClientView::UserConsole(sock);
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
               ClientService::RequestSend("CHANGE_PASSWORD|" + input, sock);
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
               return ClientView::UserConsole(sock);
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

void ClientView::MobileConsole(int sock, Mobile& mobile, bool isUpdate)
{
   clear();
   endwin();
   system("clear");
   currentView = MobileDetails;
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
   WINDOW *updateWin = newwin(35, 100, 0, 0);
   wattron(updateWin, COLOR_PAIR(3));
   box(updateWin, 0, 0);
   mvwprintw(updateWin, 4, 2, "--------------------------------------------------------");
   mvwprintw(updateWin, 20, 2, "--------------------------------------------------------");
   wattroff(updateWin, COLOR_PAIR(3));
   refresh();
   wattron(updateWin, COLOR_PAIR(4));
   mvwprintw(updateWin, 2, (getmaxx(updateWin) - (22 + mobile.getMobileName().length())) / 2, "*** Current Mobile: %s ***", mobile.getMobileName().c_str());
   mvwprintw(updateWin, 6, 3, "1. Mobile Name: ");
   mvwprintw(updateWin, 8, 3, "2. Mobile Type: ");
   mvwprintw(updateWin, 10, 3, "3. Mobile Model: ");
   mvwprintw(updateWin, 12, 3, "4. Mobile Quantity: ");
   mvwprintw(updateWin, 14, 3, "5. Mobile Price: ");
   mvwprintw(updateWin, 16, 3, "6. Mobile Description: ");
   mvwprintw(updateWin, 6, (getmaxx(updateWin) - 6), "(A)");
   mvwprintw(updateWin, 8, (getmaxx(updateWin) - 6), "(G)");
   mvwprintw(updateWin, 10, (getmaxx(updateWin) - 6), "(P)");
   mvwprintw(updateWin, 12, (getmaxx(updateWin) - 6), "(O)");
   mvwprintw(updateWin, 14, (getmaxx(updateWin) - 6), "(R)");
   mvwprintw(updateWin, 16, (getmaxx(updateWin) - 6), "(D)");
   mvwprintw(updateWin, 22, (getmaxx(updateWin) - 40) / 2, "Select an option that you want to fill");
   wattroff(updateWin, COLOR_PAIR(4));
   wattron(updateWin, COLOR_PAIR(6));
   mvwprintw(updateWin, 6, 27, "%s", mobile.getMobileName().c_str());
   mvwprintw(updateWin, 8, 27, "%s", mobile.getMobileType().c_str());
   mvwprintw(updateWin, 10, 27, "%s", mobile.getMobileModel().c_str());
   if (mobile.getMobileQuantity() != 0) {
      mvwprintw(updateWin, 12, 27, "%s", to_string(mobile.getMobileQuantity()).c_str());
   } else {
      mvwprintw(updateWin, 12, 27, "%s", "0");
   }
   mvwprintw(updateWin, 14, 27, "%s", to_string(mobile.getMobilePrice()).c_str());
   mvwprintw(updateWin, 16, 27, "%s", mobile.getMobileDescription().c_str());
   wattroff(updateWin, COLOR_PAIR(6));
   wrefresh(updateWin);
   int curs_x, curs_y;
   wmove(updateWin, 63, 13);
   int n;
   char c = char(n);
   char *ch = &c;
   do
   {
      input = "";
      n = wgetch(updateWin);
      switch (n)
      {
      case 27: //ESC
      {
         return ClientView::handleMenu(sock, -1);
      }
      case 97: // A
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Mobile Name: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::MobileConsole(sock, mobile, isUpdate);
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
               mobile.setMobileName(input);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::MobileConsole(sock, mobile, isUpdate);
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
         break;
      }
      case 103: // G
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Mobile Type: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::MobileConsole(sock, mobile, isUpdate);
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
               mobile.setMobileType(input);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::MobileConsole(sock, mobile, isUpdate);
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
         break;
      }
      case 112: // P
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Mobile Model: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::MobileConsole(sock, mobile, isUpdate);
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
               mobile.setMobileModel(input);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::MobileConsole(sock, mobile, isUpdate);
            }
            else
            {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x + 1, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(char(n));
               curs_x++;
            }
         } while (true);
         break;
      }
      case 111: // O
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Mobile Quantity: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::MobileConsole(sock, mobile, isUpdate);
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
               mobile.setMobileQuantity(stoi(input));
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::MobileConsole(sock, mobile, isUpdate);
            }
            else
            {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x + 1, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(char(n));
               curs_x++;
            }
         } while (true);
         break;
      }
      case 114: // R
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Mobile Price: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::MobileConsole(sock, mobile, isUpdate);
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
               mobile.setMobilePrice(stod(input));
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::MobileConsole(sock, mobile, isUpdate);
            }
            else
            {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x + 1, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(char(n));
               curs_x++;
            }
         } while (true);
         break;
      }
      case 100: // D
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Mobile Description: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::MobileConsole(sock, mobile, isUpdate);
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
               mobile.setMobileDescription(input);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::MobileConsole(sock, mobile, isUpdate);
            }
            else
            {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x + 1, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(char(n));
               curs_x++;
            }
         } while (true);
         break;
      }
      default:
         do {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::handleMenu(sock, -1);
            }
            else if (n == 10) {
               if(isUpdate) {
                  ClientService::UpdateMobileDevice(sock, mobile);
               } else {
                  ClientService::CreateMobileDevice(sock, mobile);
               }
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 26, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               while (getch() != 10) {}
               refresh();
               printf("\n");
               return ClientView::MobileConsole(sock, mobile, isUpdate);
            } 
         } while (true);
         break;
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

void ClientView::CustomerConsole(int sock, Customer& customer, bool isUpdate) {
   clear();
   endwin();
   system("clear");
   currentView = MobileDetails;
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
   WINDOW *updateWin = newwin(35, 100, 0, 0);
   wattron(updateWin, COLOR_PAIR(3));
   box(updateWin, 0, 0);
   mvwprintw(updateWin, 4, 2, "--------------------------------------------------------");
   mvwprintw(updateWin, 20, 2, "--------------------------------------------------------");
   wattroff(updateWin, COLOR_PAIR(3));
   refresh();
   wattron(updateWin, COLOR_PAIR(4));
   mvwprintw(updateWin, 2, (getmaxx(updateWin) - (22 + customer.getCustomerName().length())) / 2, "*** Current Customer: %s ***", customer.getCustomerName().c_str());
   mvwprintw(updateWin, 6, 3, "1. Customer Name: ");
   mvwprintw(updateWin, 8, 3, "2. Customer Address: ");
   mvwprintw(updateWin, 10, 3, "3. Customer Gender: ");
   mvwprintw(updateWin, 12, 3, "4. Customer Birthday: ");
   mvwprintw(updateWin, 14, 3, "5. Customer Email: ");
   mvwprintw(updateWin, 6, (getmaxx(updateWin) - 6), "(A)");
   mvwprintw(updateWin, 8, (getmaxx(updateWin) - 6), "(G)");
   mvwprintw(updateWin, 10, (getmaxx(updateWin) - 6), "(P)");
   mvwprintw(updateWin, 12, (getmaxx(updateWin) - 6), "(O)");
   mvwprintw(updateWin, 14, (getmaxx(updateWin) - 6), "(R)");
   mvwprintw(updateWin, 22, (getmaxx(updateWin) - 40) / 2, "Select an option that you want to fill");
   wattroff(updateWin, COLOR_PAIR(4));
   wattron(updateWin, COLOR_PAIR(6));
   mvwprintw(updateWin, 6, 27, "%s", customer.getCustomerName().c_str());
   mvwprintw(updateWin, 8, 27, "%s", customer.getCustomerAddress().c_str());
   mvwprintw(updateWin, 10, 27, "%s", customer.getCustomerGender().c_str());
   mvwprintw(updateWin, 12, 27, "%s", customer.getCustomerBirthday().c_str());
   mvwprintw(updateWin, 14, 27, "%s", customer.getCustomerEmail().c_str());
   wattroff(updateWin, COLOR_PAIR(6));
   wrefresh(updateWin);
   int curs_x, curs_y;
   wmove(updateWin, 63, 13);
   int n;
   char c = char(n);
   char *ch = &c;
   do
   {
      input = "";
      n = wgetch(updateWin);
      switch (n)
      {
      case 27: //ESC
      {
         return ClientView::handleMenu(sock, -1);
      }
      case 97: // A
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Customer Name: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::CustomerConsole(sock, customer, isUpdate);
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
               customer.setCustomerName(input);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::CustomerConsole(sock, customer, isUpdate);
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
         break;
      }
      case 103: // G
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Customer Address: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::CustomerConsole(sock, customer, isUpdate);
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
               customer.setCustomerAddress(input);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::CustomerConsole(sock, customer, isUpdate);
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
         break;
      }
      case 112: // P
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Customer Gender: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::CustomerConsole(sock, customer, isUpdate);
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
               customer.setCustomerGender(input);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::CustomerConsole(sock, customer, isUpdate);
            }
            else
            {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x + 1, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(char(n));
               curs_x++;
            }
         } while (true);
         break;
      }
      case 111: // O
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Customer Birthday (yyyy-mm-dd): ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::CustomerConsole(sock, customer, isUpdate);
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
            else if (n == 10 && input.length() == 10) // Must follow yyyy-mm-dd format
            {
               customer.setCustomerBirthday(input);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::CustomerConsole(sock, customer, isUpdate);
            }
            else
            {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x + 1, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(char(n));
               curs_x++;
            }
         } while (true);
         break;
      }
      case 114: // R
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Customer Email: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::CustomerConsole(sock, customer, isUpdate);
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
            else if (n == 10 && input.find("@") != std::string::npos)
            {
               customer.setCustomerEmail(input);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::CustomerConsole(sock, customer, isUpdate);
            }
            else
            {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x + 1, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(char(n));
               curs_x++;
            }
         } while (true);
         break;
      }
      default:
         do {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::handleMenu(sock, -1);
            }
            else if (n == 10) {
               if(isUpdate) {
                  ClientService::UpdateCustomer(sock, customer);
               } else {
                  ClientService::CreateCustomer(sock, customer);
               }
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 26, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               while (getch() != 10) {}
               refresh();
               printf("\n");
               return ClientView::CustomerConsole(sock, customer, isUpdate);
            } 
         } while (true);
         break;
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

void ClientView::TransactionConsole(int sock, Transaction& transaction, bool isUpdate, bool isCustomerUI, int customerId) {
   clear();
   endwin();
   system("clear");
   currentView = TransactionDetails;
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
   WINDOW *updateWin = newwin(35, 100, 0, 0);
   wattron(updateWin, COLOR_PAIR(3));
   box(updateWin, 0, 0);
   mvwprintw(updateWin, 4, 2, "--------------------------------------------------------");
   mvwprintw(updateWin, 20, 2, "--------------------------------------------------------");
   wattroff(updateWin, COLOR_PAIR(3));
   refresh();
   wattron(updateWin, COLOR_PAIR(4));
   mvwprintw(updateWin, 2, (getmaxx(updateWin) - (22 + transaction.getTransactionName().length())) / 2, "*** Current Transaction: %s ***", transaction.getTransactionName().c_str());
   mvwprintw(updateWin, 6, 3, "1. Transaction Name: ");
   mvwprintw(updateWin, 8, 3, "2. Transaction Type: ");
   mvwprintw(updateWin, 10, 3, "3. Payment Method: ");
   mvwprintw(updateWin, 6, (getmaxx(updateWin) - 6), "(A)");
   mvwprintw(updateWin, 8, (getmaxx(updateWin) - 6), "(G)");
   mvwprintw(updateWin, 10, (getmaxx(updateWin) - 6), "(P)");
   mvwprintw(updateWin, 16, (getmaxx(updateWin) - 40) / 2, "Select an option that you want to fill");
   wattroff(updateWin, COLOR_PAIR(4));
   wattron(updateWin, COLOR_PAIR(6));
   mvwprintw(updateWin, 6, 27, "%s", transaction.getTransactionName().c_str());
   mvwprintw(updateWin, 8, 27, "%s", transaction.getTransactionType().c_str());
   mvwprintw(updateWin, 10, 27, "%s", transaction.getPaymentMethod().c_str());
   wattroff(updateWin, COLOR_PAIR(6));
   wrefresh(updateWin);
   int curs_x, curs_y;
   wmove(updateWin, 63, 13);
   int n;
   char c = char(n);
   char *ch = &c;
   do
   {
      input = "";
      n = wgetch(updateWin);
      switch (n)
      {
      case 27: //ESC
      {
         return ClientView::handleMenu(sock, -1);
      }
      case 97: // A
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Transaction Name: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::TransactionConsole(sock, transaction, isUpdate, isCustomerUI, customerId);
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
               transaction.setTransactionName(input);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::TransactionConsole(sock, transaction, isUpdate, isCustomerUI, customerId);
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
         break;
      }
      case 103: // G
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Transaction Type: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::TransactionConsole(sock, transaction, isUpdate, isCustomerUI, customerId);
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
               transaction.setTransactionType(input);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::TransactionConsole(sock, transaction, isUpdate, isCustomerUI, customerId);
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
         break;
      }
      case 112: // P
      {
         curs_set(1);
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 24, 3, "New Payment Method: ");
         wattroff(updateWin, COLOR_PAIR(4));
         getyx(updateWin, curs_y, curs_x);
         wrefresh(updateWin);
         do
         {
            n = wgetch(updateWin);
            if (n == 27)
            {
               return ClientView::TransactionConsole(sock, transaction, isUpdate, isCustomerUI, customerId);
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
               transaction.setPaymentMethod(input);
               curs_set(0);
               wattron(updateWin, COLOR_PAIR(3));
               mvwprintw(updateWin, 26, 3, "Update complete.");
               wattron(updateWin, COLOR_PAIR(3));
               wattron(updateWin, COLOR_PAIR(1));
               mvwprintw(updateWin, 27, 3, "Press ENTER to refresh.");
               wattroff(updateWin, COLOR_PAIR(1));
               wrefresh(updateWin);
               refresh();
               printf("\n");
               return ClientView::TransactionConsole(sock, transaction, isUpdate, isCustomerUI, customerId);
            }
            else
            {
               c = char(n);
               mvwprintw(updateWin, curs_y, curs_x + 1, "%c", c); // print the current key on screen and push it to input string
               wrefresh(updateWin);
               input.push_back(char(n));
               curs_x++;
            }
         } while (true);
         break;
      }
      default:
      do {
         n = wgetch(updateWin);
         if (n == 27)
         {
            return ClientView::handleMenu(sock, -1);
         }
         else if (n == 10) {
            if(isUpdate) {
               ClientService::UpdateTransaction(sock, transaction);
            } else {
               ClientService::CreateTransaction(sock, transaction, customerId);
            }
            curs_set(0);
            wattron(updateWin, COLOR_PAIR(3));
            wattron(updateWin, COLOR_PAIR(1));
            mvwprintw(updateWin, 26, 3, "Press ENTER to refresh.");
            wattroff(updateWin, COLOR_PAIR(1));
            wrefresh(updateWin);
            while (getch() != 10) {}
            refresh();
            printf("\n");
            return ClientView::TransactionConsole(sock, transaction, isUpdate, isCustomerUI, customerId);
         } 
      } while (true);
      break;
   }
          
      if (n == 27)
      {
         if (isCustomerUI) {
            return ClientView::CustomerUI(sock, customerId);
         } else {
            return handleMenu(sock, -1);
         }
      }

      if (n == 10)
      {
         break;
      }
   } while (true);
}

int ClientView::selectMenu()
{
   int n;
   Menu();
   // cout << "\n\033[1;32mSelect your choice: \033[0m";
   // cin >> n;
   n = getch();
   noecho();
   printw("%d", n);
   // ClientService::HandleInput();
   // cin.ignore();
   if (n > 0 || n < 99)
   {
      return n;
   }
   else
      return selectMenu();
}

void ClientView::interactUserMenu(int count, map<int, UserClient> foundUser, int sock)
{
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
      { // If the key press is backspace, erase current char
         // ClientView::ChangeBarText(std::to_string(input.length()));
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
      ClientView::handleMenu(sock, -1);
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
         ClientView::interactUserMenu(count, foundUser, sock);
      }
      else
      {
         UserUI(sock, foundUser[choice], interactUserMenuWin);
      }
   }
}

void ClientView::findMobileMenu(int sock, map<int,int>& idMobileInventory) {
   string input;
   refresh();
   endwin();
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
   WINDOW *interactMobileMenuWin = newwin(5, 61, 46, 0);
   currentWin = interactMobileMenuWin;
   wattron(currentWin, COLOR_PAIR(3));
   box(currentWin, 0, 0);
   wattroff(currentWin, COLOR_PAIR(3));
   refresh();
   wattron(currentWin, COLOR_PAIR(4));
   mvwprintw(currentWin, 1, 1, "Select mobile option interact: ");
   wattroff(currentWin, COLOR_PAIR(4));
   wrefresh(currentWin);
   int curs_x, curs_y;
   wmove(currentWin, 1, 34);
   // change int to char *
   int n;
   char c = char(n);
   char *ch = &c;
   input = "";
   do
   {
      // Get the key
      n = wgetch(currentWin);
      if (n == 27) // back to login menu
      {
         clear();
         endwin();
         break;
      }
      else if (n == 8 || n == 127)
      { 
         if (input.length() != 0) // check if input length == 0 then no need to pop from string input
         {
            input.pop_back();
            wprintw(currentWin, " \b");
            wrefresh(currentWin);
            curs_x--;
         }
         else if (input.length() == 0)
         {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 1, 1, "Select mobile option interact: ");
            wattroff(currentWin, COLOR_PAIR(4));
            wrefresh(currentWin);
         }
      }
      else if (n == 10 && input.length() == 0)
      {
         move(1, 1);
         clrtoeol();
         wattron(currentWin, COLOR_PAIR(4));
         mvwprintw(currentWin, 1, 1, "Select mobile option interact: ");
         wattroff(currentWin, COLOR_PAIR(4));
         wrefresh(currentWin);
         continue;
      }
      else if (n == 10 && input.length() != 0)
      {
         break;
      }
      else
      {
         c = char(n);
         mvwprintw(currentWin, curs_y, curs_x, "%c", c);
         wrefresh(currentWin);
         input.push_back(c);
         curs_x++;
      }
   } while (true);

   if (n == 27)
   {
      ClientView::handleMenu(sock, -1);
   }
   else
   {
      int choice = stoi(input);
      ClientView::MobileUI(sock, getKeyByValue(idMobileInventory, choice));
   }
}

void ClientView::findCustomerMenu(int sock, map<int,int>& idCustomerInventory) {
   string input;
   refresh();
   endwin();
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
   WINDOW *interactCustomerMenuWin = newwin(5, 61, 46, 0);
   currentWin = interactCustomerMenuWin;
   wattron(currentWin, COLOR_PAIR(3));
   box(currentWin, 0, 0);
   wattroff(currentWin, COLOR_PAIR(3));
   refresh();
   wattron(currentWin, COLOR_PAIR(4));
   mvwprintw(currentWin, 1, 1, "Select customer option interact: ");
   wattroff(currentWin, COLOR_PAIR(4));
   wrefresh(currentWin);
   int curs_x, curs_y;
   wmove(currentWin, 1, 34);
   // change int to char *
   int n;
   char c = char(n);
   char *ch = &c;
   input = "";
   do
   {
      // Get the key
      n = wgetch(currentWin);
      if (n == 27) // back to login menu
      {
         clear();
         endwin();
         break;
      }
      else if (n == 8 || n == 127)
      { 
         if (input.length() != 0) // check if input length == 0 then no need to pop from string input
         {
            input.pop_back();
            wprintw(currentWin, " \b");
            wrefresh(currentWin);
            curs_x--;
         }
         else if (input.length() == 0)
         {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 1, 1, "Select customer option interact: ");
            wattroff(currentWin, COLOR_PAIR(4));
            wrefresh(currentWin);
         }
      }
      else if (n == 10 && input.length() == 0)
      {
         move(1, 1);
         clrtoeol();
         wattron(currentWin, COLOR_PAIR(4));
         mvwprintw(currentWin, 1, 1, "Select customer option interact: ");
         wattroff(currentWin, COLOR_PAIR(4));
         wrefresh(currentWin);
         continue;
      }
      else if (n == 10 && input.length() != 0)
      {
         break;
      }
      else
      {
         c = char(n);
         mvwprintw(currentWin, curs_y, curs_x, "%c", c);
         wrefresh(currentWin);
         input.push_back(c);
         curs_x++;
      }
   } while (true);

   if (n == 27)
   {
      ClientView::handleMenu(sock, -1);
   }
   else
   {
      int choice = stoi(input);
      ClientView::CustomerUI(sock, getKeyByValue(idCustomerInventory, choice));
   }
}

void ClientView::findTransactionMenu(int sock, map<int, int>& idTransactionInventory) {
   string input;
   refresh();
   endwin();
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
   WINDOW *interactTransactionMenuWin = newwin(5, 61, 46, 0);
   currentWin = interactTransactionMenuWin;
   wattron(currentWin, COLOR_PAIR(3));
   box(currentWin, 0, 0);
   wattroff(currentWin, COLOR_PAIR(3));
   refresh();
   wattron(currentWin, COLOR_PAIR(4));
   mvwprintw(currentWin, 1, 1, "Select transaction option interact: ");
   wattroff(currentWin, COLOR_PAIR(4));
   wrefresh(currentWin);
   int curs_x, curs_y;
   wmove(currentWin, 1, 40);
   // change int to char *
   int n;
   char c = char(n);
   char *ch = &c;
   input = "";
   do
   {
      // Get the key
      n = wgetch(currentWin);
      if (n == 27) // back to login menu
      {
         clear();
         endwin();
         break;
      }
      else if (n == 8 || n == 127)
      { 
         if (input.length() != 0) // check if input length == 0 then no need to pop from string input
         {
            input.pop_back();
            wprintw(currentWin, " \b");
            wrefresh(currentWin);
            curs_x--;
         }
         else if (input.length() == 0)
         {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 1, 1, "Select transaction option interact: ");
            wattroff(currentWin, COLOR_PAIR(4));
            wrefresh(currentWin);
         }
      }
      else if (n == 10 && input.length() == 0)
      {
         move(1, 1);
         clrtoeol();
         wattron(currentWin, COLOR_PAIR(4));
         mvwprintw(currentWin, 1, 1, "Select transaction option interact: ");
         wattroff(currentWin, COLOR_PAIR(4));
         wrefresh(currentWin);
         continue;
      }
      else if (n == 10 && input.length() != 0)
      {
         break;
      }
      else
      {
         c = char(n);
         mvwprintw(currentWin, curs_y, curs_x, "%c", c);
         wrefresh(currentWin);
         input.push_back(c);
         curs_x++;
      }
   } while (true);

   if (n == 27)
   {
      ClientView::handleMenu(sock, -1);
   }
   else
   {
      int choice = stoi(input);
      ClientView::TransactionUI(sock, getKeyByValue(idTransactionInventory, choice));
   }
}

void ClientView::handleMenu(int sock, int selection)
{
   bool isSuccess = false;
   vector<Mobile>mobileList;
   vector<Customer>customerList;
   vector<Transaction>transactionList;
   Mobile mobile;
   Customer customer;
   do
   {
      if (selection < 0)
      {
         selection = selectMenu();
      }
      switch (selection)
      {
      case 97: //A
      {
         clear();
         endwin();
         currentView = MobileInventory;
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
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 2, (getmaxx(updateWin) - 40), "*** Mobile Inventory ***");
         mvwprintw(updateWin, 6, 3, "1. Create New Mobile Devices: ");
         mvwprintw(updateWin, 8, 3, "2. Create New Customer: ");
         mvwprintw(updateWin, 6, (getmaxx(updateWin) - 6), "(A)");
         mvwprintw(updateWin, 8, (getmaxx(updateWin) - 6), "(G)");
         mvwprintw(updateWin, 14, (getmaxx(updateWin) - 40) / 2, "Select an option that you want to add");
         wattroff(updateWin, COLOR_PAIR(4));
         wattron(updateWin, COLOR_PAIR(6));
         int curs_x, curs_y;
         wmove(updateWin, 43, 13);
         int n;
         char c = char(n);
         do {
            n = wgetch(updateWin);

            if (n == 27) {
               return ClientView::handleMenu(sock, -1);
            }

            if(n == 97) // A
            {
               refresh();
               endwin();
               system("clear");
               // Create new mobile Inventory
               ClientView::MobileConsole(sock, mobile, false);
            }
            else if (n == 103) { // G
               refresh();
               endwin();
               system("clear");
               // Create new Customer
               ClientView::CustomerConsole(sock, customer, false);
            }
         } while(true);
         break;
      }
      case 110: //N
      {
         clear();
         endwin();
         currentView = MobileInventory;
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
         wattron(updateWin, COLOR_PAIR(4));
         mvwprintw(updateWin, 2, (getmaxx(updateWin) - 40), "*** Mobile Inventory ***");
         mvwprintw(updateWin, 6, 3, "1. Find Mobile Devices by Mobile Name: ");
         mvwprintw(updateWin, 8, 3, "2. Find Mobile Devices by Mobile Category: ");
         mvwprintw(updateWin, 6, (getmaxx(updateWin) - 6), "(A)");
         mvwprintw(updateWin, 8, (getmaxx(updateWin) - 6), "(G)");
         mvwprintw(updateWin, 14, (getmaxx(updateWin) - 40) / 2, "Select an option that you want to find");
         wattroff(updateWin, COLOR_PAIR(4));
         wattron(updateWin, COLOR_PAIR(6));
         int curs_x, curs_y;
         wmove(updateWin, 43, 13);
         int n;
         char c = char(n);
         do {
            n = wgetch(updateWin);
            switch (n)
            {
            case 97: // A
            {
               clear();
               refresh();
               endwin();
               currentView = MobileInventory;
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
               WINDOW *mobileInventoryWin = newwin(5, 61, 0, 0);
               currentWin = mobileInventoryWin;
               wattron(currentWin, COLOR_PAIR(3));
               box(currentWin, 0, 0);
               wattroff(currentWin, COLOR_PAIR(3));
               refresh();
               wattron(currentWin, COLOR_PAIR(4));
               mvwprintw(currentWin, 2, 1, "Enter Mobile Name: ");
               wattroff(currentWin, COLOR_PAIR(4));
               wrefresh(currentWin);
               int curs_x, curs_y;
               getyx(currentWin, curs_y, curs_x);
               wmove(currentWin, curs_y, curs_x);
               // change int to char *
               int n;
               char c = char(n);
               input = "";
               do
               {
                  // Get the key
                  n = wgetch(currentWin);
                  if(n == 27)
                  {
                     return ClientView::handleMenu(sock, -1);
                  } else if (n == 8 || n == 127) {
                     if (input.length() != 0){
                        input.pop_back();
                        wprintw(currentWin, "\b \b");
                        wrefresh(currentWin);
                        curs_x--;
                     }
                  } else if (n == 10 && input.length() >= 0 ) {
                     break;
                  }
                  else if (n == 10) {
                     attroff(COLOR_PAIR(1));
                     refresh();
                     sleep(2);
                     move(16, 0);
                     clrtoeol();
                     refresh();
                     wmove(currentWin, curs_y, curs_x);
                     wrefresh(currentWin);
                     ClientView::handleMenu(sock, 110);
                  }
                  else  {
                     c = char(n);
                     mvwprintw(currentWin, curs_y, curs_x, "%c",c);
                     wrefresh(currentWin);
                     input.push_back(c);
                     curs_x++;
                  }
                  getyx(currentWin, curs_y, curs_x);
               } while (true);
               
               if (n == 27)
               {
                  break;
               }
               else{
                  map<int,int> idInventory = ClientService::FindInventoryName(sock, mobileList, input, "FIND_INVENTORY_NAME");
                  if (idInventory.size() == 0) {
                     break;
                  }
                  int spacePos = 0;
                  for (auto mb : mobileList) {
                     mvprintw(6+spacePos, 0, "%s", ClientService::processString(to_string(idInventory[mb.getMobileId()])).c_str());
                     mvprintw(6+spacePos, 5, "%s", ClientService::processString(mb.getMobileName()).c_str());
                     mvprintw(6+spacePos, 40, "%s", ClientService::processString(mb.getMobileType()).c_str());
                     mvprintw(6+spacePos, 60, "%s", ClientService::processString(mb.getMobileModel()).c_str());
                     mvprintw(6+spacePos, 80, "%s", ClientService::processString(to_string(mb.getMobileQuantity())).c_str());
                     mvprintw(6+spacePos, 100, "%s", ClientService::processString(to_string(mb.getMobilePrice())).c_str());
                     mvprintw(6+spacePos, 120, "%s", ClientService::processString(mb.getMobileDescription()).c_str());
                     spacePos += 1;
                  }

                  mvprintw((getmaxx(currentWin) - 20), 0, "Press any key to continue!");
                  int back = getch();
                  if (back == 27)
                  {
                     return ClientView::handleMenu(sock, -1);
                  }
                  
                  // Search information details for mobile ID
                  findMobileMenu(sock, idInventory);
               }
               break;
            }
            case 103: // G
            {
               clear();
               refresh();
               endwin();
               currentView = MobileInventory;
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
               WINDOW *mobileInventoryWin = newwin(5, 61, 0, 0);
               currentWin = mobileInventoryWin;
               wattron(currentWin, COLOR_PAIR(3));
               box(currentWin, 0, 0);
               wattroff(currentWin, COLOR_PAIR(3));
               refresh();
               wattron(currentWin, COLOR_PAIR(4));
               mvwprintw(currentWin, 2, 1, "Enter Mobile Type: ");
               wattroff(currentWin, COLOR_PAIR(4));
               wrefresh(currentWin);
               int curs_x, curs_y;
               getyx(currentWin, curs_y, curs_x);
               wmove(currentWin, curs_y, curs_x);
               // change int to char *
               int n;
               char c = char(n);
               input = "";
               do
               {
                  // Get the key
                  n = wgetch(currentWin);
                  if(n == 27)
                  {
                     return ClientView::handleMenu(sock, -1);
                  } else if (n == 8 || n == 127) {
                     if (input.length() != 0){
                        input.pop_back();
                        wprintw(currentWin, "\b \b");
                        wrefresh(currentWin);
                        curs_x--;
                     }
                  } else if (n == 10 && input.length() >= 0 ) {
                     break;
                  }
                  else if (n == 10) {
                     attroff(COLOR_PAIR(1));
                     refresh();
                     sleep(2);
                     move(16, 0);
                     clrtoeol();
                     refresh();
                     wmove(currentWin, curs_y, curs_x);
                     wrefresh(currentWin);
                     ClientView::handleMenu(sock, 110);
                  }
                  else  {
                     c = char(n);
                     mvwprintw(currentWin, curs_y, curs_x, "%c",c);
                     wrefresh(currentWin);
                     input.push_back(c);
                     curs_x++;
                  }
                  getyx(currentWin, curs_y, curs_x);
               } while (true);
               
               if (n == 27)
               {
                  break;
               }
               else{
                  map<int,int> idInventory = ClientService::FindInventoryName(sock, mobileList, input, "FIND_INVENTORY_TYPE");
                  if (idInventory.size() == 0) {
                     break;
                  }
                  int spacePos = 0;
                  for (auto mb : mobileList) {
                     mvprintw(6+spacePos, 0, "%s", ClientService::processString(to_string(idInventory[mb.getMobileId()])).c_str());
                     mvprintw(6+spacePos, 5, "%s", ClientService::processString(mb.getMobileName()).c_str());
                     mvprintw(6+spacePos, 40, "%s", ClientService::processString(mb.getMobileType()).c_str());
                     mvprintw(6+spacePos, 60, "%s", ClientService::processString(mb.getMobileModel()).c_str());
                     mvprintw(6+spacePos, 80, "%s", ClientService::processString(to_string(mb.getMobileQuantity())).c_str());
                     mvprintw(6+spacePos, 100, "%s", ClientService::processString(to_string(mb.getMobilePrice())).c_str());
                     mvprintw(6+spacePos, 120, "%s", ClientService::processString(mb.getMobileDescription()).c_str());
                     spacePos += 1;
                  }

                  mvprintw((getmaxx(currentWin) - 20), 0, "Press any key to continue!");
                  int back = getch();
                  if (back == 27)
                  {
                     return ClientView::handleMenu(sock, -1);
                  }

                  // Search information details for mobile ID
                  findMobileMenu(sock, idInventory);
               }
               break;
            }
         }
         // Press ESC to return the previous page
         if (n == 27) {
            clear();
            endwin();
            break;
         }

         } while(true);
         break;
      }
      case 105: //I: Transaction History
      {
         clear();
         refresh();
         endwin();
         currentView = Transactions;
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
         if (has_colors() == FALSE)
         {
            clear();
            endwin();
            printf("Your terminal does not support color\n");
            exit(1);
         }
         //echo();
         WINDOW *transactionWin = newwin(5, 61, 0, 0);
         currentWin = transactionWin;
         wattron(currentWin, COLOR_PAIR(3));
         box(currentWin, 0, 0);
         wattroff(currentWin, COLOR_PAIR(3));
         refresh();
         wattron(currentWin, COLOR_PAIR(4));
         mvwprintw(currentWin, 2, 1, "Enter Transaction Name: ");
         wattroff(currentWin, COLOR_PAIR(4));
         wrefresh(currentWin);
         int curs_x, curs_y;
         getyx(currentWin, curs_y, curs_x);
         wmove(currentWin, curs_y, curs_x);
         // change int to char *
         int n;
         char c = char(n);
         input = "";
         do
         {
            // Get the key
            n = wgetch(currentWin);
            if(n == 27)
            {
               clear();
               endwin();
               break;
            } else if (n == 8 || n == 127) {
               if (input.length() != 0){
                  input.pop_back();
                  wprintw(currentWin, "\b \b");
                  wrefresh(currentWin);
                  curs_x--;
               }
            } else if (n == 10 && input.length() >= 0 ) {
               break;
            }
            else if (n == 10) {
               attroff(COLOR_PAIR(1));
               refresh();
               sleep(2);
               move(16, 0);
               clrtoeol();
               refresh();
               wmove(currentWin, curs_y, curs_x);
               wrefresh(currentWin);
               ClientView::handleMenu(sock, 110);
            }
            else  {
               c = char(n);
               mvwprintw(currentWin, curs_y, curs_x, "%c",c);
               wrefresh(currentWin);
               input.push_back(c);
               curs_x++;
            }
            getyx(currentWin, curs_y, curs_x);
         } while (true);
         
         if (n == 27) {
            break;
         }
         else {
            map<int,int> idTransactionMapping = ClientService::FindTransactionHistory(sock, transactionList, input);
            if (idTransactionMapping.size() == 0) {
               break;
            }
            int spacePos = 0;
            for(auto tr : transactionList) {
               mvprintw(6+spacePos, 0, "%s", ClientService::processString(to_string(idTransactionMapping[tr.getTransactionId()])).c_str());
               mvprintw(6+spacePos, 5, "%s", ClientService::processString(tr.getTransactionName()).c_str());
               mvprintw(6+spacePos, 40, "%s", ClientService::processString(tr.getTransactionType()).c_str());
               mvprintw(6+spacePos, 80, "%s", ClientService::processString(tr.getPaymentMethod()).c_str());
               spacePos += 1;
            }
            
            mvprintw((getmaxx(currentWin) - 20), 0, "Press any key to continue!");
            int back = getch();
            if (back == 27) {
               clear();
               endwin();
               break;
            }

            // Search information details for customer ID
            findTransactionMenu(sock, idTransactionMapping);
         }
         break;
      }
      case 106: //J
      {
         clear();
         refresh();
         endwin();
         currentView = CustomerView;
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
         WINDOW *customerWin = newwin(5, 61, 0, 0);
         currentWin = customerWin;
         wattron(currentWin, COLOR_PAIR(3));
         box(currentWin, 0, 0);
         wattroff(currentWin, COLOR_PAIR(3));
         refresh();
         wattron(currentWin, COLOR_PAIR(4));
         mvwprintw(currentWin, 2, 1, "Enter Customer Name: ");
         wattroff(currentWin, COLOR_PAIR(4));
         wrefresh(currentWin);
         int curs_x, curs_y;
         getyx(currentWin, curs_y, curs_x);
         wmove(currentWin, curs_y, curs_x);
         // change int to char *
         int n;
         char c = char(n);
         input = "";
         do {
            n = wgetch(currentWin);
            if (n == 27) {
               clear();
               endwin();
               break;
            } 
            else if (n == 8 || n == 127) {
               if (input.length()!= 0) {
                  input.pop_back();
                  wprintw(currentWin, "\b \b");
                  wrefresh(currentWin);
                  curs_x--;
               }
            } 
            else if (n == 10 && input.length() >= 0) {
               break;
            }
            else if (n == 10) {
               attroff(COLOR_PAIR(1));
               refresh();
               sleep(2);
               move(16, 0);
               clrtoeol();
               refresh();
               wmove(currentWin, curs_y, curs_x);
               wrefresh(currentWin);
               ClientView::handleMenu(sock, 110);
            } else {
               c = char(n);
               mvwprintw(currentWin, curs_y, curs_x, "%c", c);
               wrefresh(currentWin);
               input.push_back(c);
               curs_x++;
            }
            getyx(currentWin, curs_y, curs_x);
         } while(true);
         if (n == 27) {
            break;
         }
         else {
            map<int, int> idCustomerMapping = ClientService::FindCustomerName(sock, customerList, input);
            if (idCustomerMapping.size() == 0) {               
               break;
            }
            int spacePos = 0;
            for (auto cus : customerList) {
               mvprintw(6+spacePos, 0, "%s", ClientService::processString(to_string(idCustomerMapping[cus.getCustomerId()])).c_str());
               mvprintw(6+spacePos, 5, "%s", ClientService::processString(cus.getCustomerName()).c_str());
               mvprintw(6+spacePos, 30, "%s", ClientService::processString(cus.getCustomerGender()).c_str());
               mvprintw(6+spacePos, 60, "%s", ClientService::processString(cus.getCustomerAddress()).c_str());
               mvprintw(6+spacePos, 100, "%s", ClientService::processString(cus.getCustomerBirthday()).c_str());
               mvprintw(6+spacePos, 120, "%s", ClientService::processString(cus.getCustomerEmail()).c_str());
               spacePos++;
            }

            mvprintw((getmaxx(currentWin) - 20), 0, "Press any key to continue!");
            int back = getch();
            if (back == 27) {
               clear();
               endwin();
               break;
            }
            
            // Search information details for customer ID
            findCustomerMenu(sock, idCustomerMapping);
         }
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
            ClientService::RequestSend("FIND_USER|" + searchValue, sock);
            int count = 0;
            // mvwprintw(finduserWin, 2, 2, "countname");
            map<int, UserClient> foundUser = ClientService::GetFoundUser(sock, currentUser, count, finduserWin);
            ClientView::interactUserMenu(count, foundUser, sock);
         }
         break;
      }
      case 117: // U
         refresh();
         endwin();
         std::system("clear");
         ClientService::RequestSend("UPDATE_PROFILE|", sock);
         ClientService::GetCurrentProfile(sock, currentUser);
         ClientView::UserConsole(sock);
         break;
      case 111: // O
         refresh();
         clear();
         endwin();
         std::system("clear");
         ClientService::RequestSend("LOGOUT_USER|", sock);
         currentUser.setId(-1);
         currentUser.setPassword("");
         currentUser.setName("");
         handleLogin(sock, -1);
         break;
      case 27: // ESC
         refresh();
         endwin();
         ClientService::exitAppChat(sock);
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

void ClientView::UserUI(int sock, UserClient user, WINDOW *interactUserMenuWin)
{

   clear();
   refresh();
   endwin();
   currentView = OrtherUserProfile;
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
   }
   echo();
   curs_set(1);
   WINDOW *OtherUserProfileWin = newwin(20, 61, 0, 0);
   currentWin = OtherUserProfileWin;
   wattron(currentWin, COLOR_PAIR(3));
   box(currentWin, 0, 0);
   wattroff(currentWin, COLOR_PAIR(3));
   int count = 1;
   refresh();
   int curs_x, curs_y;
   curs_x = 33;
   curs_y = 12;
   wmove(currentWin, curs_x, curs_y);

   // change int to char *
   int n;
   char c = char(n);
   char *ch = &c;
   wattron(currentWin, COLOR_PAIR(4));
   mvwprintw(currentWin, 1, 11, "--------User Profile Options--------");
   wattroff(currentWin, COLOR_PAIR(3));
   mvwprintw(currentWin, 2, 1, "1. User Information");
   if (isAdmin) {
      mvwprintw(currentWin, 3, 1, "2. Delete Account");
      mvwprintw(currentWin, 4, 1, "3. Exit");
   }
   else {
      mvwprintw(currentWin, 3, 1, "2. Exit");
   }
   wattron(currentWin, COLOR_PAIR(4));
   mvwprintw(currentWin, 7, 1, "Select your option:");
   wattroff(currentWin, COLOR_PAIR(4));
   wrefresh(currentWin);
   do
   {
      // Get the key
      n = wgetch(currentWin);
      // if get Esc(27), it will turn back to login menu
      if (n == 27) // back to login menu
      {
         clear();
         endwin();
         ClientView::handleMenu(sock, -1);
      }
      else if (n == 8 || n == 127)
      {                           // If the key press is backspace, erase current char, ex: hoangmy -> hoangm
         if (input.length() != 0) // check if input length == 0 then no need to pop from string input
         {
            input.pop_back();
            wprintw(currentWin, " \b");
            wrefresh(currentWin);
            // curs_y--;
         }
         else if (input.length() == 0)
         {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 7, 1, "Select your option:");
            wattroff(currentWin, COLOR_PAIR(4));
            wrefresh(currentWin);
         }
      }
      else if (n == 10 && input.length() == 0)
      { // User name must be at least 3 characters
         move(7, 1);
         clrtoeol();
         wattron(currentWin, COLOR_PAIR(4));
         mvwprintw(currentWin, 7, 1, "Select your option:");
         wattroff(currentWin, COLOR_PAIR(4));
         wrefresh(currentWin);
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
         mvwprintw(currentWin, 34, 12, "%s", ch); // print the current key on screen and push it to input string
         wrefresh(currentWin);
         input.push_back(c);
         // curs_x++;
      }
   } while (true);

   if (n == 27)
   {
      ClientView::handleMenu(sock, -1);
   }

   else
   {
      string select = input;
      int sig;
      // int n;
      // n = stoi(select);
      if (select == "1")
      {
         ClientService::GetUserProfile(sock, user, currentWin);
         while (true)
         {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 18, 1, "Press Enter to continue");
            wattroff(currentWin, COLOR_PAIR(4));
            sig = wgetch(currentWin);
            if (sig == 10)
            {
               ClientView::UserUI(sock, user, interactUserMenuWin);
               break;
            }
         }
      }
      if (isAdmin) {
         if (select == "2") {
            // Delete account
            bool isDeleted = ClientService::DeleteAccountUser(sock, user.getId());
            if (isDeleted) {
               while (true)
               {
                  wattron(currentWin, COLOR_PAIR(4));
                  mvwprintw(currentWin, 18, 1, "Successfully deleted user account");
                  wattroff(currentWin, COLOR_PAIR(4));
                  sig = wgetch(currentWin);
                  if (sig == 10)
                  {
                     ClientView::UserUI(sock, user, interactUserMenuWin);
                     break;
                  }
               }
            }
            else {
               while (true)
               {
                  wattron(currentWin, COLOR_PAIR(4));
                  mvwprintw(currentWin, 18, 1, "Failed to delete user account");
                  wattroff(currentWin, COLOR_PAIR(4));
                  sig = wgetch(currentWin);
                  if (sig == 10)
                  {
                     ClientView::UserUI(sock, user, interactUserMenuWin);
                     break;
                  }
               }
            }
         }
         else if (select == "3") {
            ClientView::handleMenu(sock, -1);
         }
      } else {
         if (select == "2")
         {
            ClientView::handleMenu(sock, -1);
         }
      }
   }
}

void ClientView::MobileUI(int sock, int mobileId) {
   clear();
   refresh();
   endwin();
   currentView = MobileDetails;

   system("clear");
   string input;
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
   }
   echo();
   curs_set(1);
   WINDOW *mobileSearchWin = newwin(20, 61, 0, 0);
   currentWin = mobileSearchWin;
   wattron(currentWin, COLOR_PAIR(3));
   box(currentWin, 0, 0);
   wattroff(currentWin, COLOR_PAIR(3));
   int count = 1;
   refresh();
   int curs_x, curs_y;
   curs_x = 34;
   curs_y = (getmaxx(currentWin) - 30);
   wmove(currentWin, curs_x, curs_y);

   int n;
   char c = char(n);
   char *ch = &c;
   wattroff(currentWin, COLOR_PAIR(3));
   mvwprintw(currentWin, 1, 4, "------------Mobile Device Information------------");
   wattroff(currentWin, COLOR_PAIR(3));
   mvwprintw(currentWin, 3, 1, "1. Mobile Information");
   mvwprintw(currentWin, 4, 1, "2. Update Mobile Information");
   mvwprintw(currentWin, 5, 1, "3. Delete Mobile Device");
   mvwprintw(currentWin, 6, 1, "4. Exit");
   wattroff(currentWin, COLOR_PAIR(4));
   mvwprintw(currentWin, 7, 1, "Select your option: ");
   wattroff(currentWin, COLOR_PAIR(4));
   wrefresh(currentWin);
   
   do {
      // Get the key
      n = wgetch(currentWin);
      if (n == 27) {
         clear();
         endwin();
         ClientView::handleMenu(sock, -1);
      } 
      else if (n == 8 || n == 127) {
         // If the key press is backspace, erase current char
         if (input.length() != 0) {
            input.pop_back();
            wprintw(currentWin, " \b");
            wrefresh(currentWin);
         } else if (input.length() == 0) {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 7, 1, "Select your option: ");
            wattroff(currentWin, COLOR_PAIR(4));
            wrefresh(currentWin);
         }
      } else if (n == 10 && input.length() == 0) {
         move(7, 1);
         clrtoeol();
         wattron(currentWin, COLOR_PAIR(4));
         mvwprintw(currentWin, 7, 1, "Select your option: ");
         wattroff(currentWin, COLOR_PAIR(4));
         wrefresh(currentWin);
         continue;
      } else if (n == 10 && input.length() != 0) {
         break;
      } else {
         c = char(n);
         mvwprintw(currentWin, curs_y, curs_x, "%s", ch);
         wrefresh(currentWin);
         input.push_back(c);
      }
   } while (true);
   
   if (n == 27) {
      ClientView::handleMenu(sock, -1);
   }
   else {
      string select = input;
      int sig;

      if (select == "1") {
         Mobile mb = ClientService::GetMobileInformation(sock, mobileId);
         mvwprintw(currentWin, 9, 1, "%s: %s", "Mobile ID", to_string(mb.getMobileId()).c_str());
         mvwprintw(currentWin, 10, 1, "%s: %s", "Mobile Name", mb.getMobileName().c_str());
         mvwprintw(currentWin, 11, 1, "%s: %s", "Mobile Type", mb.getMobileType().c_str());
         mvwprintw(currentWin, 12, 1, "%s: %s", "Mobile Model", mb.getMobileModel().c_str());
         mvwprintw(currentWin, 13, 1, "%s: %s", "Mobile Quantity", to_string(mb.getMobileQuantity()).c_str());
         mvwprintw(currentWin, 14, 1, "%s: %s", "Mobile Price", to_string(mb.getMobilePrice()).c_str());
         mvwprintw(currentWin, 15, 1, "%s: %s", "Mobile Description", mb.getMobileDescription().c_str());
         while (true) {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 18, 1, "Press Enter to continue");
            wattroff(currentWin, COLOR_PAIR(4));
            sig = wgetch(currentWin);
            if (sig == 10) {
               ClientView::MobileUI(sock, mobileId);
               break;
            }
         }
      }
      else if (select == "2") {
         Mobile mobile = ClientService::GetMobileInformation(sock, mobileId);
         ClientView::MobileConsole(sock, mobile, true);
      }
      else if (select == "3") {
         bool isDeleted = ClientService::DeleteMobileDevice(sock, mobileId);
         while (true) {
            wattron(currentWin, COLOR_PAIR(3));
            isDeleted ? mvwprintw(currentWin, 14, 1, "%s", "Mobile has been deleted successfully!!")
                      : mvwprintw(currentWin, 14, 1, "%s", "Failed to delete mobile device !!");;
            wattroff(currentWin, COLOR_PAIR(3));
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 18, 1, "Press Enter to continue");
            wattroff(currentWin, COLOR_PAIR(4));
            sig = wgetch(currentWin);
            if (sig == 10) {
               ClientView::handleMenu(sock, -1);
               break;
            }
         }
      }
      else if (select == "4") {
         ClientView::handleMenu(sock, -1);
      }
   }
}

void ClientView::CustomerUI(int sock, int customerId) {
   Transaction transaction;
   clear();
   refresh();
   endwin();
   currentView = CustomerDetails;

   system("clear");
   string input;
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
   }
   echo();
   curs_set(1);
   WINDOW *customerSearchWin = newwin(20, 70, 0, 0);
   currentWin = customerSearchWin;
   wattron(currentWin, COLOR_PAIR(3));
   box(currentWin, 0, 0);
   wattroff(currentWin, COLOR_PAIR(3));
   int count = 1;
   refresh();
   int curs_x, curs_y;
   curs_x = 34;
   curs_y = (getmaxx(currentWin) - 30);
   wmove(currentWin, curs_x, curs_y);

   int n;
   char c = char(n);
   char *ch = &c;
   wattroff(currentWin, COLOR_PAIR(4));
   mvwprintw(currentWin, 1, 4, "------------Customer Information------------");
   wattroff(currentWin, COLOR_PAIR(3));
   mvwprintw(currentWin, 3, 1, "1. Customer Information");
   mvwprintw(currentWin, 4, 1, "2. Update Customer Information");
   mvwprintw(currentWin, 5, 1, "3. Delete Customer");
   mvwprintw(currentWin, 6, 1, "4. Create New Transaction");
   mvwprintw(currentWin, 7, 1, "5. Show Customer Transaction");
   mvwprintw(currentWin, 8, 1, "6. Exit");
   refresh();
   wattroff(currentWin, COLOR_PAIR(4));
   mvwprintw(currentWin, 9, 1, "Select your option: ");
   wattroff(currentWin, COLOR_PAIR(4));
   wrefresh(currentWin);
   
   do {
      // Get the key
      n = wgetch(currentWin);
      if (n == 27) {
         clear();
         endwin();
         ClientView::handleMenu(sock, -1);
      } 
      else if (n == 8 || n == 127) {
         // If the key press is backspace, erase current char
         if (input.length() != 0) {
            input.pop_back();
            wprintw(currentWin, " \b");
            wrefresh(currentWin);
         } else if (input.length() == 0) {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 9, 1, "Select your option: ");
            wattroff(currentWin, COLOR_PAIR(4));
            wrefresh(currentWin);
         }
      } else if (n == 10 && input.length() == 0) {
         move(7, 1);
         clrtoeol();
         wattron(currentWin, COLOR_PAIR(4));
         mvwprintw(currentWin, 9, 1, "Select your option: ");
         wattroff(currentWin, COLOR_PAIR(4));
         wrefresh(currentWin);
         continue;
      } else if (n == 10 && input.length() != 0) {
         break;
      } else {
         c = char(n);
         mvwprintw(currentWin, curs_y, curs_x, "%s", ch);
         wrefresh(currentWin);
         input.push_back(c);
      }
   } while (true);
   
   if (n == 27) {
      ClientView::handleMenu(sock, -1);
   }
   else {
      string select = input;
      int sig;

      if (select == "1") {
         Customer customer = ClientService::GetCustomerInformation(sock, customerId);
         mvwprintw(currentWin, 11, 1, "%s: %s", "Customer ID", to_string(customer.getCustomerId()).c_str());
         mvwprintw(currentWin, 12, 1, "%s: %s", "Customer Name", customer.getCustomerName().c_str());
         mvwprintw(currentWin, 13, 1, "%s: %s", "Customer Address", customer.getCustomerAddress().c_str());
         mvwprintw(currentWin, 14, 1, "%s: %s", "Customer Gender", customer.getCustomerGender().c_str());
         mvwprintw(currentWin, 15, 1, "%s: %s", "Customer Birthday", customer.getCustomerBirthday().c_str());
         mvwprintw(currentWin, 16, 1, "%s: %s", "Customer Email", customer.getCustomerEmail().c_str());
         
         while (true) {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 18, 1, "Press Enter to continue");
            wattroff(currentWin, COLOR_PAIR(4));
            sig = wgetch(currentWin);
            if (sig == 10) {
               ClientView::CustomerUI(sock, customerId);
               break;
            }
         }
      }
      else if (select == "2") {
         Customer customer = ClientService::GetCustomerInformation(sock, customerId);
         // Update Customer Information
         ClientView::CustomerConsole(sock, customer, true);
      }
      else if (select == "3") {
         bool isDeleted = ClientService::DeleteCustomer(sock, customerId);
         while (true) {
            wattron(currentWin, COLOR_PAIR(3));
            isDeleted ? mvwprintw(currentWin, 14, 1, "%s", "Customer has been deleted successfully!!")
                      : mvwprintw(currentWin, 14, 1, "%s", "Failed to delete customer !!");;
            wattroff(currentWin, COLOR_PAIR(3));
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 18, 1, "Press Enter to continue");
            wattroff(currentWin, COLOR_PAIR(4));
            sig = wgetch(currentWin);
            if (sig == 10) {
               ClientView::handleMenu(sock, -1);
               break;
            }
         }
      }
      else if (select == "4") {
         refresh();
         endwin();
         system("clear");
         // Create new Transaction and in customer UI
         ClientView::TransactionConsole(sock, transaction, false, true, customerId);
      }
      else if (select == "5") {
         vector<Transaction> transactionList;
         map<int, int> idTransactionMapping = ClientService::GetTransactionHistoryWithCustomerId(sock, transactionList, customerId);
         if (idTransactionMapping.empty()) {
            mvwprintw(currentWin, 14, 1, "%s", "No transaction found!!");
         } else {
            WINDOW *transactionListWin = newwin(30, 120, 0, 0);
            currentWin = transactionListWin;
            // Show Customer Transaction
            mvwprintw(currentWin, 1, 5, "%s", "Transaction Name");
            mvwprintw(currentWin, 1, 60, "%s", "Transaction Type");
            mvwprintw(currentWin, 1, 80, "%s", "Payment Method");
            int spacePos = 0;
            for(auto transaction : transactionList) {
               mvwprintw(currentWin, 2+spacePos, 2, "%s", ClientService::processString(to_string(idTransactionMapping[transaction.getTransactionId()])).c_str());
               mvwprintw(currentWin, 2+spacePos, 5, "%s", ClientService::processString(transaction.getTransactionName()).c_str());
               mvwprintw(currentWin, 2+spacePos, 60, "%s", ClientService::processString(transaction.getTransactionType()).c_str());
               mvwprintw(currentWin, 2+spacePos, 80, "%s", ClientService::processString(transaction.getPaymentMethod()).c_str());
               spacePos += 1;
            }

            while (true) {
               wattron(currentWin, COLOR_PAIR(4));
               mvwprintw(currentWin, (getmaxx(currentWin) - 10), 1, "Press Enter to continue");
               wattroff(currentWin, COLOR_PAIR(4));
               sig = wgetch(currentWin);
               if (sig == 10) {
                  ClientView::CustomerUI(sock, customerId);
                  break;
               }
            }
         }
      }
      else if (select == "6") {
         ClientView::handleMenu(sock, -1);
      }
   }
}

void ClientView::TransactionUI(int sock, int transactionId) {
   clear();
   refresh();
   endwin();
   currentView = TransactionDetails;

   system("clear");
   string input;
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
   }
   echo();
   curs_set(1);
   WINDOW *transactionSearchWin = newwin(20, 61, 0, 0);
   currentWin = transactionSearchWin;
   wattron(currentWin, COLOR_PAIR(3));
   box(currentWin, 0, 0);
   wattroff(currentWin, COLOR_PAIR(3));
   int count = 1;
   refresh();
   int curs_x, curs_y;
   curs_x = 34;
   curs_y = (getmaxx(currentWin) - 30);
   wmove(currentWin, curs_x, curs_y);

   int n;
   char c = char(n);
   char *ch = &c;
   wattroff(currentWin, COLOR_PAIR(3));
   mvwprintw(currentWin, 1, 4, "------------Transaction Information------------");
   wattroff(currentWin, COLOR_PAIR(3));
   mvwprintw(currentWin, 3, 1, "1. Transaction Information");
   mvwprintw(currentWin, 4, 1, "2. Update Transaction Information");
   mvwprintw(currentWin, 5, 1, "3. Delete Transaction");
   mvwprintw(currentWin, 6, 1, "4. Exit");
   wattroff(currentWin, COLOR_PAIR(4));
   mvwprintw(currentWin, 7, 1, "Select your option: ");
   wattroff(currentWin, COLOR_PAIR(4));
   wrefresh(currentWin);
   
   do {
      // Get the key
      n = wgetch(currentWin);
      if (n == 27) {
         clear();
         endwin();
         ClientView::handleMenu(sock, -1);
      } 
      else if (n == 8 || n == 127) {
         // If the key press is backspace, erase current char
         if (input.length() != 0) {
            input.pop_back();
            wprintw(currentWin, " \b");
            wrefresh(currentWin);
         } else if (input.length() == 0) {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 7, 1, "Select your option: ");
            wattroff(currentWin, COLOR_PAIR(4));
            wrefresh(currentWin);
         }
      } else if (n == 10 && input.length() == 0) {
         move(7, 1);
         clrtoeol();
         wattron(currentWin, COLOR_PAIR(4));
         mvwprintw(currentWin, 7, 1, "Select your option: ");
         wattroff(currentWin, COLOR_PAIR(4));
         wrefresh(currentWin);
         continue;
      } else if (n == 10 && input.length() != 0) {
         break;
      } else {
         c = char(n);
         mvwprintw(currentWin, curs_y, curs_x, "%s", ch);
         wrefresh(currentWin);
         input.push_back(c);
      }
   } while (true);
   
   if (n == 27) {
      ClientView::handleMenu(sock, -1);
   }
   else {
      string select = input;
      int sig;

      if (select == "1") {
         Transaction transaction = ClientService::GetTransactionInformation(sock, transactionId);
         mvwprintw(currentWin, 9, 1, "%s: %s", "Transaction ID", to_string(transaction.getTransactionId()).c_str());
         mvwprintw(currentWin, 10, 1, "%s: %s", "Transaction Type", transaction.getTransactionName().c_str());
         mvwprintw(currentWin, 11, 1, "%s: %s", "Payment Method", transaction.getTransactionType().c_str());
         
         while (true) {
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 18, 1, "Press Enter to continue");
            wattroff(currentWin, COLOR_PAIR(4));
            sig = wgetch(currentWin);
            if (sig == 10) {
               ClientView::TransactionUI(sock, transactionId);
               break;
            }
         }
      }
      else if (select == "2") {
         Transaction transaction = ClientService::GetTransactionInformation(sock, transactionId);
         // Update Customer Information and didn't in Customer UI
         ClientView::TransactionConsole(sock, transaction, true, false, -1);
      }
      else if (select == "3") {
         bool isDeleted = ClientService::DeleteTransaction(sock, transactionId);
         while (true) {
            wattron(currentWin, COLOR_PAIR(3));
            isDeleted ? mvwprintw(currentWin, 14, 1, "%s", "Transaction has been deleted successfully!!")
                      : mvwprintw(currentWin, 14, 1, "%s", "Failed to delete transaction !!");;
            wattroff(currentWin, COLOR_PAIR(3));
            wattron(currentWin, COLOR_PAIR(4));
            mvwprintw(currentWin, 18, 1, "Press Enter to continue");
            wattroff(currentWin, COLOR_PAIR(4));
            sig = wgetch(currentWin);
            if (sig == 10) {
               ClientView::handleMenu(sock, -1);
               break;
            }
         }
      }
      else if (select == "4") {
         ClientView::handleMenu(sock, -1);
      }
   }
}

UserClient ClientView::getUser()
{
   return currentUser;
}

WINDOW *ClientView::getCurrentWin()
{
   return currentWin;
}