create table bank_account(
                             id bigint auto_increment primary key,
                             balance double
);

create table categories(
                           id bigint auto_increment primary key,
                           name varchar(255) not null,
                           type varchar(50) not null
);

create table transaction(
                            id bigint auto_increment primary key,
                            title varchar(255) not null,
                            amount double not null,
                            date date not null,
                            created_at timestamp not null,
                            updated_at timestamp
);

create table users(
                      id bigint auto_increment primary key,
                      username varchar(50) not null unique,
                      email varchar(255) not null unique,
                      password varchar(255) not null,
                      first_name varchar(50),
                      last_name varchar(50),
                      is_active boolean,
                      created_at timestamp,
                      updated_at timestamp
);
