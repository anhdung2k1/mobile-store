#!/bin/bash
g++ -o client main.cpp controller/ChatController.cpp services/ChatService.cpp view/ChatView.cpp model/MessageClient.cpp model/UserClient.cpp model/ChatClient.cpp view/StatusBar.cpp -pthread -lncurses
./client
