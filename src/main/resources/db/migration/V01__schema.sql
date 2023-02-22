CREATE TABLE isikato_employee(
                                 id NUMBER(19,0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                                 username VARCHAR (50) UNIQUE NOT NULL ,
                                 password VARCHAR(255) NOT NULL ,
                                 name VARCHAR(255),
                                 phone VARCHAR(50) UNIQUE,
                                 email VARCHAR(255) UNIQUE,
                                 deleted number(1,0) DEFAULT 0,
                                 enabled number(1,0) DEFAULT 1,
                                 creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 last_modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_login_audit(
                                 id NUMBER(19,0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                                 user_id number(19, 0) REFERENCES isikato_employee(id),
                                 login_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE isikato_token(
                              id NUMBER(19,0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                              token VARCHAR(50) UNIQUE NOT NULL ,
                              employee_id NUMBER(19,0) NOT NULL REFERENCES isikato_employee(id),
                              expiration TIMESTAMP NOT NULL ,
                              creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE isikato_permission(
                                   id NUMBER(19,0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                   permissions CLOB ,
                                   description VARCHAR(1000) ,
                                   deleted number(1,0) DEFAULT 0,
                                   creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                   last_modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE isikato_employee_permission(
                                            employee_id NUMBER(19,0) REFERENCES isikato_employee(id),
                                            permission_id NUMBER(19,0) REFERENCES isikato_permission(id)
);

CREATE TABLE isikato_file(
                             id NUMBER(19,0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                             mime VARCHAR (255) ,
                             extension VARCHAR(255) ,
                             name VARCHAR(255) ,
                             file_size NUMBER(19,0) ,
                             type INTEGER,
                             duration Double Precision,
                             cover_time INTEGER ,
                             deleted number(1,0) DEFAULT 0,
                             creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                             content_id INTEGER,
                             last_modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE isikato_employee ADD image_id NUMBER(19,0) REFERENCES isikato_file(id);

CREATE TABLE isikato_data(
                             id NUMBER(19,0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                             data BLOB,
                             type INTEGER,
                             file_id NUMBER(19,0) references isikato_file(id),
                             deleted number(1,0) DEFAULT 0
);

CREATE TABLE isikato_category(
                                 id NUMBER(19,0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 name VARCHAR (255) ,
                                 type VARCHAR(255) ,
                                 description VARCHAR(1024) ,
                                 image_id NUMBER(19,0) references isikato_file(id),
                                 deleted number(1,0) DEFAULT 0,
                                 creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                 last_modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE isikato_content(
                                id NUMBER(19,0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY ,
                                title VARCHAR (255) ,
                                description VARCHAR(1024) ,
                                page VARCHAR(255) ,
                                body CLOB,
                                extra1 VARCHAR(1024),
                                extra2 VARCHAR(1024),
                                extra3 VARCHAR(1024),
                                extra4 VARCHAR(1024),
                                extra5 VARCHAR(1024),
                                featured number(1,0) DEFAULT 0,
                                deleted number(1,0) DEFAULT 0,
                                writer_id  NUMBER(19,0) REFERENCES isikato_employee(id),
                                download_counter NUMBER(19,0) DEFAULT 0,
                                published number(1,0) DEFAULT 1,
                                creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                last_modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE isikato_content_visit(
                                      id NUMBER(19,0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                      timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      content_id NUMBER(19,0) references isikato_content(id),
                                      user_id NUMBER(19,0) references isikato_employee(id)
);

CREATE TABLE isikato_tag(
                            content_id NUMBER(19,0) REFERENCES isikato_content(id),
                            name VARCHAR(255)
);

CREATE TABLE isikato_content_category(
                                         content_id NUMBER(19,0) REFERENCES isikato_content(id),
                                         category_id NUMBER(19,0) REFERENCES isikato_category(id)
);

CREATE TABLE isikato_content_image(
                                      content_id NUMBER(19,0) REFERENCES isikato_content(id),
                                      image_id NUMBER(19,0) REFERENCES isikato_file(id)
);

CREATE TABLE isikato_banner(
                               id NUMBER(19,0) GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               title VARCHAR (255) ,
                               page VARCHAR(255) ,
                               image_id NUMBER(19,0) references isikato_file(id),
                               deleted number(1,0) DEFAULT 0,
                               creation_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               last_modified_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE isikato_content_image2(
                                       content_id NUMBER(19,0) REFERENCES isikato_content(id),
                                       image_id NUMBER(19,0) REFERENCES isikato_file(id)
);

