create table users
(
    id         serial primary key,
    name       text               not null,
    username   varchar(20) unique not null,
    email      text unique        not null,
    password   text,
    role       text,
    phone      text unique,
    fb_user    bool      default false,
    fb_uid     text,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp
);

create table employees
(
    id          serial primary key,
    user_id     int  not null,
    role        text not null,
    age         smallint,
    gender      varchar(10),
    salary      real not null default 0,
    pharmacy_id int  not null,
    created_at  timestamp     default current_timestamp,
    updated_at  timestamp     default current_timestamp,
    constraint fk_user_id foreign key (user_id) references users (id) on delete cascade,
    constraint fk_phar_id foreign key (pharmacy_id) references pharmacies (id) on delete cascade
);

create table pharmacies
(
    id          serial primary key,
    owner_id    int                   not null,
    location    geometry(Point, 4326) not null,
    address     text                  not null,
    is_branch   bool                  not null default false,
    main_branch int,
    created_at  timestamp                      default current_timestamp,
    updated_at  timestamp                      default current_timestamp,
    constraint fk_owner_id foreign key (owner_id) references users (id) on delete cascade,
    constraint fk_main_branch foreign key (main_branch) references pharmacies (id) on delete cascade
);


create table refresh_token
(
    id          serial primary key,
    token       text not null,
    username    text,
    expiry_date timestamp default current_timestamp,
    used        bool      default false
);