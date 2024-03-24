CREATE TABLE USERS(
    user_id BIGINT NOT NULL AUTO_INCREMENT, 
    user_address VARCHAR(255), 
    birth_day DATE, 
    gender VARCHAR(255), 
    user_name VARCHAR(255) NOT NULL, 
    create_at DATETIME(6), 
    update_at DATETIME(6), 
    PRIMARY KEY (user_id)
);

CREATE TABLE ROLES(
    role_id BIGINT NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(255) NOT NULL,
    role_description VARCHAR(255) NOT NULL,
    PRIMARY KEY (role_id)
);

create table ACCOUNTS(
    acc_id BIGINT NOT NULL AUTO_INCREMENT,
    acc_role_id BIGINT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    hash_pass VARCHAR(255) NOT NULL,
    update_at DATETIME(6),
    user_id BIGINT,
    PRIMARY KEY (acc_id),
    FOREIGN KEY (acc_role_id) REFERENCES ROLES(role_id),
    FOREIGN KEY (user_id) REFERENCES USERS(user_id)
);

CREATE TABLE PERMISSION(
    per_id BIGINT NOT NULL AUTO_INCREMENT,
    per_role_id BIGINT NOT NULL,
    per_module VARCHAR(255) NOT NULL,
    per_name VARCHAR(255) NOT NULL,
    PRIMARY KEY (per_id),
    FOREIGN KEY (per_role_id) REFERENCES ROLES(role_id)
);

CREATE TABLE CUSTOMERS(
    cus_id BIGINT NOT NULL AUTO_INCREMENT,
    cus_name VARCHAR(255),
    cus_mobile VARCHAR(255),
    cus_email VARCHAR(255),
    cus_address VARCHAR(255),
    cus_gender VARCHAR(255),
    cus_birth_day DATE,
    cus_create_at DATETIME(6),
    cus_update_at DATETIME(6),
    PRIMARY KEY (cus_id)
);

CREATE TABLE PAYMENT(
    pay_id BIGINT NOT NULL AUTO_INCREMENT,
    pay_cus_id BIGINT NOT NULL,
    pay_date DATE,
    pay_desc VARCHAR(255),
    PAY_AMT VARCHAR(255),
    PRIMARY KEY (pay_id),
    FOREIGN KEY (pay_cus_id) REFERENCES CUSTOMERS(cus_id)
);

CREATE TABLE MOBILE(
    mob_id BIGINT NOT NULL AUTO_INCREMENT,
    mob_name VARCHAR(255) NOT NULL,
    mob_model VARCHAR(255) NOT NULL,
    mob_type VARCHAR(255),
    mob_desc VARCHAR(255),
    PRIMARY KEY (mob_id)
);

CREATE TABLE MODELS(
    mdl_id BIGINT NOT NULL AUTO_INCREMENT,
    mdl_name VARCHAR(255),
    mdl_type VARCHAR(255),
    pay_id BIGINT NOT NULL,
    PRIMARY KEY (mdl_id),
    FOREIGN KEY (pay_id) REFERENCES PAYMENT(pay_id)
);

