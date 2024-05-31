# Documentation Authentication Java-Spring Boot API
---
<a href = "#Prerequisites">Prerequisites</a>
---
- Technical:
    - Docker MySQL database installed in Docker Container
    - Set up account for using database: 
    ```
    $ docker exec -it mysql_container mysql -u mobile -pmobile
    ```
    - Spring Boot
    - Spring Security
    - JWT
    - Java environment (Java ver 17)
- Build: Maven 3.9.1
- Hibernate Core ORM: Hibernate

<h2>HOW TO RUN</h2> 

1. Cat the host in mysql_container by
```
docker inspect -f '{{range.NetworkSettings.Networks}}{{.IPAddress}}{{end}}' mysql_container
```

It will show the host address IPv4 mysql is running --> In this point, we are going to use 172.21.0.3 IP address <br/>
1. Configure in docker-compose.yml file and ChatServerService.cpp file located in socket-server and change the IP communication to `172.21.0.3` <br/>
2. Checking the pwd directory path in each context in docker-csompose.yml file and make a change point to right directory <br/>
3. 
# How to make an API Requests from terminal (CURL Command)
<a>Prerequisites</a>
- Environment:
    - Linux
    - Curl
- Command going to use to make a test API
```
curl [options] [URL...]
```
- HTTP GET
    - The GET method requests a specific resource from the localhost:8080 server
    - GET is the default method when making HTTP requests with <strong>curl</strong>
```
$ curl http://localhost:8080/api/accounts -H "Authorization: Bearer <ACCESS_TOKEN>"
```
To filter result use query params:
```
$ curl http://localhost:8080/api/accounts?acc_id=1 -H "Authorization: Bearer <ACCESS_TOKEN>"
```

- HTTP POST
    - The POST method is used to create a resource on the server (localhost:8080). If the resource exists, it is overridden
    - The following command make <strong>POST request</strong> using the data specified with the ```-d ``` option:
```
$ curl -X POST http://localhost:8080/api/accounts/signup -H "Content-Type: application/json" -d '{"userName": "anhdung", "password": "anhdung"}'
```
- This command will return the response from the server that you made request. In this circumstances, the response we are going to get is a token <ACCESS_TOKEN>
- Using this token detached in Header for each request --> Authenticate each Filter request when making API call to inner Application
- HTTP PUT:
    - The PUT method is used to update or replace a resource on a server. It replaces all data of the specified resource with the request data.
```
$ curl -X PUT http://localhost:8080/api/users/1 -H "Content-Type: application/json" -d '{"address":"address1", "gender": "MALE"}' -H "Authorization: Bearer <ACCESS_TOKEN>"
```
- HTTP DELETE:
    - The DELETE method is used ti removes the specified resource from the server
```
$ curl -X DELETE http://localhost:80808/api/user/1
```

<a><strong>API Requests</strong></a>

| .No | Path | Methods | Description |
| --- | --- | --- | --- |
| **User** | | | |
| 1 | /api/users | GET | GET All Users |
| 2 | /api/users | POST | CREATE new User |
| 3 | /api/users/:id | GET | GET User by UserID |
| 4 | /api/users/search?userName= | GET | GET User by Name |
| 5 | /api/users/:id | PATCH | UPDATE User |
| 6 | /api/users/:id | DELETE | Delete User |
| 7 | /api/users/find?user_name= | GET | GET User ID With UserName |
| **Account** | | | |
| 1 | /api/accounts/signin | POST | LOGIN Account |
| 2 | /api/accounts/signup | POST | REGISTER Account |
| 3 | /api/accounts | GET | GET All Accounts |
| 4 | /api/accounts/:id | GET | Get Accounts by ID |
| 5 | /api/accounts/find?userName= | GET | GET Account by userName |
| 6 | /api/accounts/admin?userName= | GET | Check if Account is admin |
| 7 | /api/accounts/:id | PUT | UPDATE account password |
| 8 | /api/accounts/:userId | DELETE | DELETE Account |
| **Cart** | | | |
| 1 | /api/carts/user/:userId | GET | GET Current User Carts |
| **Customer** | | | |
| 1 | /api/customers | POST | Create new Customer |
| 2 | /api/customers/query?query= | GET | FIND Customer by Name |
| 3 | /api/customers/:id | GET | GET one customer by ID |
| 4 | /api/customers/:id | PATCH | UPDATE customer information |
| 5 | /api/customers/:id | DELETE | DELETE customer information |
| **Mobile** | | | |
| 1 | /api/mobiles | POST | Create new Mobile |
| 2 | /api/mobiles | GET | GET all Mobiles |
| 3 | /api/mobiles/products/query?query= | GET | GET all Mobiles by Name |
| 4 | /api/mobiles/types/query?query= | GET | GET all Mobiles by Categories |
| 5 | /api/mobiles/categories | GET | GET all Mobiles Categories |
| 6 | /api/mobiles/:id | GET | GET Mobile by Id |
| 7 | /api/mobiles/:id | PATCH | UPDATE Mobile by Id |
| 8 | /api/mobiles/:id | DELETE | DELETE Mobile by Id |
| **Payment** | | | |
| 1 | /api/payments | POST | Create new Payment Method |
| 2 | /api/payments/customers/:customerId | GET | GET All Payments by Customer ID |
| 3 | /api/payments/ | GET | GET All Payments |
| 4 | /api/payments/:id | GET | GET Payment By ID |
| 5 | /api/payments/:id | PATCH | UPDATE Payment By ID |
| 6 | /api/payments/:id | DELETE | DELETE Payment By ID |
| **Permission** | | | |
| 1 | /api/permissions | POST | Create new Permission |
| 2 | /api/permissions | GET | GET ALL Permissions |
| 3 | /api/permissions/query?query= | GET | GET Permission By Name |
| 4 | /api/permissions/:id | PATCH | UPDATE Permission |
| 5 | /api/permissions/:id | DELETE | DELETE Permission |
| **Rating** | | | |
| 1 | /api/ratings | POST | Create new Rating |
| 2 | /api/ratings/mobiles/:id | GET | GET Mobile Rating |
| 3 | /api/ratings/mobiles/:id | PATCH | UPDATE Mobile Rating |
| **Role** | | | |
| 1 | /api/roles | POST | Create new Role |
| 2 | /api/roles | GET | GET All Roles |
| 3 | /api/roles/query?query= | GET | GET Role By Name |
| 4 | /api/roles/:id | PATCH | UPDATE Role |
| 5 | /api/roles/:id | DELETE | DELETE Role |
| **Transaction** | | | |
| 1 | /api/transactions/:customerID | POST | Create new Transaction |
| 2 | /api/transactions/query?query= | GET | GET All Transactions By Name |
| 3 | /api/transactions/customer/:customerId | GET | GET All Transactions By Customer ID |
| 4 | /api/transactions/:id | PATCH | UPDATE Transaction |
| 5 | /api/transactions/:id | GET | GET Transaction |
| 6 | /api/transactions/:id | DELETE | DELETE Transaction |
