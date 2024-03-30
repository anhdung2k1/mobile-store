#!/bin/bash
option=$1
if [[ $option == "-a" ]]; then
    sudo apt-get update && \
    sudo apt install build-essential -y
    sudo apt-get install g++ -y
    sudo apt-get install nlohmann-json3-dev -y && \
    sudo apt-get install libncurses5-dev libncursesw5-dev -y
else
    g++ -o client main.cpp controller/ChatController.cpp \
           services/ChatService.cpp \
           view/ChatView.cpp \
           model/Customer.cpp \
           model/Mobile.cpp \
           model/Payment.cpp \
           model/Permission.cpp \
           model/Role.cpp \
           model/Transaction.cpp \
           model/UserClient.cpp \
           view/StatusBar.cpp \
           utils/Date.cpp \
           -pthread -lncurses
    ./client
fi
