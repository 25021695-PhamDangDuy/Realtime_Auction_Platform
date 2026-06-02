CREATE TABLE IF NOT EXISTS users(
    ID TEXT PRIMARY KEY,
    Username TEXT NOT NULL UNIQUE ,
    Password TEXT NOT NULL,
    role TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS wallets(
                                      ID TEXT PRIMARY KEY ,
                                      owner_ID TEXT NOT NULL UNIQUE ,
                                      Balance REAL DEFAULT 0.0 CHECK ( Balance >= 0.0 ),
                                      BalanceLocked REAL DEFAULT 0.0 CHECK ( BalanceLocked >= 0.0 ),
                                      FOREIGN KEY (owner_ID) REFERENCES users(ID)

);

CREATE TABLE IF NOT EXISTS items(
                                    ID TEXT PRIMARY KEY ,
                                    owner_ID TEXT NOT NULL UNIQUE ,
                                    Name TEXT NOT NULL ,
                                    Price REAL NOT NULL CHECK ( Price >= 0 ),
                                    Condition TEXT,
                                    Status TEXT NOT NULL ,

                                    FOREIGN KEY (owner_ID) REFERENCES users(ID)

);

CREATE TABLE IF NOT EXISTS bidTickets(
                                         ID TEXT PRIMARY KEY ,
                                         user_ID TEXT NOT NULL ,
                                         session_ID TEXT NOT NULL ,
                                         timestamp TEXT NOT NULL ,
                                         amount REAL NOT NULL CHECK ( amount >= 0 ),
                                         status TEXT NOT NULL ,
                                         FOREIGN KEY (user_ID) REFERENCES users(ID),
                                         FOREIGN KEY (session_ID) REFERENCES sessions(ID)
);

CREATE TABLE IF NOT EXISTS sessions(
                                       ID    TEXT PRIMARY KEY,
                                       item_ID    TEXT NOT NULL,
                                       seller_ID   TEXT NOT NULL,
    topBidTicketID TEXT,
                                       currentPrice REAL NOT NULL CHECK ( currentPrice >= 0 ),
                                       minIncrement REAL NOT NULL CHECK ( minIncrement >= 0 ),
                                       startTime TEXT NOT NULL ,
                                       endTime TEXT NOT NULL ,
                                       status TEXT NOT NULL ,
                                       FOREIGN KEY (seller_ID) REFERENCES users(ID),
                                       FOREIGN KEY (item_ID) REFERENCES  items(ID),
    FOREIGN KEY (topBidTicketID) REFERENCES bidTickets(ID)
);

CREATE TABLE IF NOT EXISTS observers_sessions(
                                                 user_ID TEXT NOT NULL ,
                                                 sessions_ID TEXT NOT NULL ,
                                                 PRIMARY KEY (user_ID,sessions_ID),
                                                 FOREIGN KEY (user_ID) REFERENCES users(ID),
                                                 FOREIGN KEY (sessions_ID) REFERENCES sessions(ID)
);

CREATE TABLE IF NOT EXISTS transactions(
                                           ID TEXT PRIMARY KEY ,
                                           sender_ID TEXT NOT NULL ,
                                           receiver_ID TEXT,
                                           session_ID TEXT,
                                           amount REAL NOT NULL CHECK ( amount >= 0 ),
                                           timestamp TEXT NOT NULL ,
                                           type TEXT NOT NULL ,
                                           status TEXT NOT NULL,
    FOREIGN KEY (sender_ID) REFERENCES users(ID),
    FOREIGN KEY (receiver_ID) REFERENCES users(ID),
    FOREIGN KEY (session_ID) REFERENCES sessions(ID)
);

CREATE TABLE IF NOT EXISTS art_item(
    ID TEXT PRIMARY KEY ,
    author TEXT,
    material TEXT,
    FOREIGN KEY (ID) REFERENCES items(ID)
);

CREATE TABLE IF NOT EXISTS electronic_item(
    ID TEXT PRIMARY KEY ,
    HSD INTEGER,
    FOREIGN KEY (ID) REFERENCES  items(ID)
);

