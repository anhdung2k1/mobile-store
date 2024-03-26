CREATE TABLE users(
    user_id BIGINT NOT NULL AUTO_INCREMENT, 
    address VARCHAR(255), 
    birth_day DATE, 
    gender VARCHAR(255), 
    user_name VARCHAR(255) NOT NULL, 
    create_at DATETIME(6), 
    update_at DATETIME(6), 
    PRIMARY KEY (user_id)
);

CREATE TABLE roles(
    role_id BIGINT NOT NULL AUTO_INCREMENT,
    role_name VARCHAR(255) NOT NULL,
    role_description VARCHAR(255),
    create_at DATETIME(6), 
    update_at DATETIME(6), 
    PRIMARY KEY (role_id)
);

CREATE TABLE accounts(
    acc_id BIGINT NOT NULL AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    hash_pass VARCHAR(255) NOT NULL,
    create_at DATETIME(6),
    update_at DATETIME(6),
    user_id BIGINT,
    PRIMARY KEY (acc_id),
    FOREIGN KEY (role_id) REFERENCES ROLES(role_id),
    FOREIGN KEY (user_id) REFERENCES USERS(user_id)
);

CREATE TABLE permission(
    per_id BIGINT NOT NULL AUTO_INCREMENT,
    role_id BIGINT NOT NULL,
    per_module VARCHAR(255) NOT NULL,
    per_name VARCHAR(255) NOT NULL,
    create_at DATETIME(6), 
    update_at DATETIME(6), 
    PRIMARY KEY (per_id),
    FOREIGN KEY (role_id) REFERENCES ROLES(role_id)
);

CREATE TABLE mobile(
    mob_id BIGINT NOT NULL AUTO_INCREMENT,
    mob_name VARCHAR(255) NOT NULL,
    mob_model VARCHAR(255) NOT NULL,
    mob_type VARCHAR(255) NOT NULL,
    mob_desc VARCHAR(255),
    create_at DATETIME(6), 
    update_at DATETIME(6), 
    PRIMARY KEY (mob_id)
);

CREATE TABLE customers(
    cus_id BIGINT NOT NULL AUTO_INCREMENT,
    cus_name VARCHAR(255) NOT NULL,
    mob_id BIGINT,
    cus_email VARCHAR(255),
    cus_address VARCHAR(255),
    cus_gender VARCHAR(255),
    cus_birth_day DATE,
    create_at DATETIME(6),
    update_at DATETIME(6),
    PRIMARY KEY (cus_id),
    FOREIGN KEY (mob_id) REFERENCES MOBILE(mob_id)
);

CREATE TABLE payment(
    pay_id BIGINT NOT NULL AUTO_INCREMENT,
    mob_id BIGINT NOT NULL,
    pay_date DATE,
    pay_desc VARCHAR(255),
    pay_method VARCHAR(255) NOT NULL,
    create_at DATETIME(6), 
    update_at DATETIME(6), 
    PRIMARY KEY (pay_id),
    FOREIGN KEY (mob_id) REFERENCES MOBILE(mob_id)
);

CREATE TABLE customer_payment(
    pay_id BIGINT NOT NULL,
    cus_id BIGINT NOT NULL
);

CREATE TABLE transactions(
    trans_id BIGINT NOT NULL AUTO_INCREMENT,
    trans_name VARCHAR(255) NOT NULL,
    trans_type VARCHAR(255) NOT NULL,
    pay_id BIGINT NOT NULL,
    create_at DATETIME(6), 
    update_at DATETIME(6), 
    PRIMARY KEY (trans_id),
    FOREIGN KEY (pay_id) REFERENCES PAYMENT(pay_id)
);