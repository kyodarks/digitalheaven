CREATE TABLE users (
    userid INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    displayname VARCHAR(100),
    password VARCHAR(255) NOT NULL,
    usertype ENUM('customer', 'admin', 'supplier') NOT NULL DEFAULT 'customer',
    profilepic VARCHAR(255) DEFAULT 'storage/pfps/default/defaultpfp.png',
    authURI VARCHAR(255),
    email VARCHAR(100) UNIQUE,
    verified BOOLEAN DEFAULT FALSE,
    created DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE product(
    productid INT AUTO_INCREMENT PRIMARY KEY,
    supplierid INT,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL DEFAULT 0,
    type ENUM('Giftcard', 'Skin', 'Product', 'Licence', 'Game_Key', 'Other') NOT NULL DEFAULT 'Other',
    FOREIGN KEY (supplierid) REFERENCES supplier(id)
);

CREATE TABLE producticon(
    id INT AUTO_INCREMENT PRIMARY KEY,
    productid INT,
    url VARCHAR(255),
    icon_index INT,
    resolution VARCHAR(255)    

    FOREIGN KEY (productid) REFERENCES product(productid)
);

CREATE TABLE sell (
    sellid INT AUTO_INCREMENT PRIMARY KEY,
    userid INT,
    product INT,
    amount INT,
    date DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (product) REFERENCES product(productid),
    FOREIGN KEY (userid) REFERENCES users(userid)
);

CREATE TABLE favorite (
    userid INT,
    productid INT,
    PRIMARY KEY (userid, productid),
    FOREIGN KEY (userid) REFERENCES users(userid),
    FOREIGN KEY (productid) REFERENCES product(productid)
);

CREATE TABLE productvisit(
    userid INT,
    productid INT,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,	
    PRIMARY KEY (userid, productid),
    FOREIGN KEY (userid) REFERENCES user(userid),
    FOREIGN KEY (productid) REFERENCES product(productid)
);