create table USERS (
    user_id bigint not null auto_increment, 
    address varchar(255), 
    birth_day date, 
    create_at datetime(6), 
    gender varchar(255), 
    update_at datetime(6), 
    user_name varchar(255) not null, 
    PRIMARY KEY (user_id)
);

create table ACCOUNTS(
    acc_id bigint NOT NULL auto_increment,
    acc_role_id int(10) NOT NULL,
    user_name varchar(255) NOT NULL,
    hash_pass varchar(255) NOT NULL,
    phone_number int(255) NOT NULL,
    update_at datetime(6),
    user_id bigint,
    PRIMARY KEY (acc_id)
);

create table ROLES(
    role_id bigint NOT NULL auto_increment,
    role_name varchar(255) NOT NULL,
    role_description varchar(255) NOT NULL,
    PRIMARY KEY (role_id)
);

create table PERMISSION(
    per_id bigint NOT NULL auto_increment,
    per_role_id int(10) NOT NULL,
    per_module varchar(255) NOT NULL,
    per_name varchar(255) NOT NULL
    PRIMARY KEY (per_id)
);