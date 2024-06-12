# Mobile Store

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=java&logoColor=white)
![Socket.IO](https://img.shields.io/badge/Socket.io-010101?&style=for-the-badge&logo=Socket.io&logoColor=white)
![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Apache Tomcat](https://img.shields.io/badge/apache%20tomcat-%23F8DC75.svg?style=for-the-badge&logo=apache-tomcat&logoColor=black)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-F2F4F9?style=for-the-badge&logo=spring-boot)
![C++](https://img.shields.io/badge/C%2B%2B-blue?style=for-the-badge&logo=c++&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-blue?style=for-the-badge&logo=MYSQL&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-green?style=for-the-badge&logo=spring-boot&logoColor=white)
![Kotlin](https://img.shields.io/badge/kotlin-%23ER8B41.svg?style=for-the-badge&logo=kotlin&logoColor=white)

Mobile Store repository works as TPC client-server connection running on Docker environment.

---
The repository using socket connection with Spring Boot web as main hosting APIs to ensure the security connection in each API request. The MYSQL database serves as a relational-database to perform query and storage datasets.

## Contents

- [Mobile Store](#mobile-store)
  - [Contents](#contents)
  - [Developer's Guide](#developers-guide)
    - [Getting Started](#getting-started)
      - [Development Environment](#development-environment)
      - [How to use](#how-to-use)

## Developer's Guide

### Getting Started

#### Development Environment

The recommend standard development environment is Ubuntu 18.04 LTS or later

#### How to use

1. Install docker: [docker installation](https://docs.docker.com/engine/install/ubuntu/)

    ```bash
    sudo apt-get -y update
    sudo apt-get -y upgrade
    sudo apt-get install apt-transport-https ca-certificates curl \
        gnupg-agent software-properties-common
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    sudo add-apt-repository \
        "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
        $(lsb_release -cs) \
        stable"
    sudo apt-get -y update
    sudo apt-get install docker-ce docker-ce-cli containerd.io
    sudo usermod -aG docker $USER
    ```

2. Install make
    ```bash
    $ sudo apt install make
    ```

3. Building steps are done via make, the builders:
    - If you don't want to clean the build artifacts, run the following command
    ```bash
    $ make build image push
    ```
    - If you wan't to clean the build and re-run all the building steps
    ```bash
    $ make clean init train build image push
    ```

4. For local development only, the docker used can be executed to run API server with docker
   ```bash
   $ make build-authentication
   ```
   This will automatically build image and running the docker container.

5. The listed API supports [API_ENDPOINTS](https://github.com/anhdung2k1/mobile-store/tree/main/authentication)
6. If you run the Back end API servers with docker, this automatically sync the data from [vas.sh](https://github.com/anhdung2k1/mobile-store/blob/main/vas.sh)
```
#Start docker mysql container
docker run -d --name $mysql_con \
    -e MYSQL_ROOT_PASSWORD=root \
    -e MYSQL_DATABASE=${COMMON_DB} \
    -e MYSQL_USER=${COMMON_DB} \
    -e MYSQL_PASSWORD=${COMMON_DB} \
    -v ${VAS_GIT}/sql:/docker-entrypoint-initdb.d \
    -p 3306:3306 \
    mysql:latest \
|| die "[ERROR]: Failed to run mysql docker"
```
