#!/bin/bash

die () {
    echo "ERROR: $1" >&2
    exit 1
}

install() {
    # Install dependencies
    sudo apt-get update && \
    sudo apt install build-essential -y && \
    sudo apt-get install g++ -y && \
    sudo apt-get install nlohmann-json3-dev -y && \
    sudo apt-get install libncurses5-dev libncursesw5-dev -y \
    || die "Failed to install dependencies"
}

compile() {
    # Ensure server_address and server_port are provided
    test -n "$__server_address" || die "Module server_address required"
    test -n "$__server_port" || die "Module server_port required"
    
    # Set environment variables
    export SERVER_ADDRESS=$__server_address
    export SERVER_PORT=$__server_port

    # Compile the C++ program
    g++ -o client main.cpp controller/ClientController.cpp \
           services/ClientService.cpp \
           view/ClientView.cpp \
           model/Customer.cpp \
           model/Mobile.cpp \
           model/Payment.cpp \
           model/Permission.cpp \
           model/Role.cpp \
           model/Transaction.cpp \
           model/UserClient.cpp \
           view/StatusBar.cpp \
           -pthread \
           -lncurses \
    || die "Compilation failed"

    # Run the client
    ./client
}

#Get the command
cmd=$1
shift
grep -q "^$cmd()" $0 || die "Invalid command [$cmd]"

while echo "$1" | grep -q '^--'; do
    if echo $1 | grep -q =; then
        o=$(echo "$1" | cut -d= -f1 | sed -e 's,-,_,g')
        v=$(echo "$1" | cut -d= -f2-)
        eval "$o=\"$v\""
    else
        o=$(echo "$1" | sed -e 's,-,_,g')
		eval "$o=yes"
    fi
    shift
done
unset o
long_opts=`set | grep '^__' | cut -d= -f1`

#Execute command
trap "die Interrupted" INT TERM
$cmd "$@"
status=$?
exit $status
