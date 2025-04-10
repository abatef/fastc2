create table users
(
    id           serial primary key,
    name         text               not null,
    username     varchar(20) unique not null,
    email        text unique        not null,
    password     text,
    role         text,
    phone        text unique,
    fb_user      bool      default false,
    fb_uid       text,
    managed_user bool      default false,
    created_at   timestamp default current_timestamp,
    updated_at   timestamp default current_timestamp
);

create table pharmacies
(
    id               serial primary key,
    name             text                  not null,
    owner_id         int                   not null,
    location         geometry(Point, 4326) not null,
    address          text                  not null,
    is_branch        bool                  not null default false,
    main_branch      int,
    expiry_threshold smallint              not null,
    -- TODO: delivery max time
    created_at       timestamp                      default current_timestamp,
    updated_at       timestamp                      default current_timestamp,
    constraint fk_owner_id foreign key (owner_id) references users (id) on delete cascade,
    constraint fk_main_branch foreign key (main_branch) references pharmacies (id) on delete cascade
);


-- TODO: add shifts table

CREATE TABLE employees
(
    id         INT PRIMARY KEY,
    age        SMALLINT,
    gender     VARCHAR(10) NOT NULL,
    salary     REAL        NOT NULL DEFAULT 0,
    pharmacy   INT         NOT NULL,
    created_at TIMESTAMP            DEFAULT current_timestamp,
    end_date   date,
    updated_at TIMESTAMP            DEFAULT current_timestamp,
    CONSTRAINT fk_user_id FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE
);


create table employee_role
(
    emp_id     int  not null,
    role       text not null,
    granted_at timestamp default current_timestamp,
    constraint fk_emp_id foreign key (emp_id) references employees (id),
    primary key (emp_id, role)
);

alter table employees
    add constraint fk_pharmacy_id foreign key (pharmacy) references pharmacies (id) on delete cascade;


create table refresh_token
(
    id          serial primary key,
    token       text not null,
    username    text,
    expiry_date timestamp default current_timestamp,
    used        bool      default false
);

create table drugs
(
    id         serial primary key,
    name       varchar(250) not null,
    form       text,
    units      smallint     not null default 0,
    full_price real         not null,
    created_by int,
    created_at timestamp             default CURRENT_TIMESTAMP,
    updated_at timestamp             default CURRENT_TIMESTAMP,
    constraint fk_created_by foreign key (created_by) references users (id) on delete cascade
);


ALTER TABLE drugs
    ADD CONSTRAINT drug_form_check
        CHECK (form IN (
                        'Granules', 'Lotion', 'Tablet', 'Nose Drops', 'Injection', 'Infusion',
                        'Film', 'Other', 'Spray', 'Syrup', 'Hair Treatment',
                        'Gargle', 'Oral Drop', 'Paint', 'Inhalations', 'Cream', 'Sachets',
                        'Powder', 'Eye Drops', 'Suppositories', 'Patch', 'Ear Drops',
                        'Solution', 'Effervescent', 'Gel', 'Lozenges', 'Capsule'
            ));

create table pharmacy_drug
(
    drug_id     int  not null,
    pharmacy_id int  not null,
    added_by    int  not null,
    stock       int  not null default 0,
    price       real not null default 0,
    -- TODO: add unit price
    -- TODO: add expiry date
    expiry_date date not null,
    created_at  timestamp     default current_timestamp,
    updated_at  timestamp     default current_timestamp,
    constraint fk_drug_id foreign key (drug_id) references drugs (id) on delete cascade,
    constraint fk_pharmacy_id foreign key (pharmacy_id) references pharmacies (id) on delete cascade,
    constraint fk_added_by foreign key (added_by) references users (id) on delete no action,
    primary key (drug_id, pharmacy_id, expiry_date)
);

alter table pharmacy_drug
    add column expiry_date date not null default current_date + 100;

create table receipt
(
    id               serial primary key,
    drug_id          int      not null,
    pharmacy_id      int      not null,
    quantity         int      not null default 1,
    amount_due       real     not null default 0.0,
    discount         real,
    cashier          int      not null,
    units            smallint not null default 0,
    packs            smallint not null default 0,
    drug_expiry_date date,
    -- TODO: add separate revenue, profit
    created_at       timestamp         default current_timestamp,
    updated_at       timestamp         default current_timestamp,
    constraint fk_pharmacy_drug foreign key (drug_id, pharmacy_id, drug_expiry_date)
        references pharmacy_drug (drug_id, pharmacy_id, expiry_date) on delete set null,
    constraint fk_cashier foreign key (cashier) references users (id) on delete set null
);


-- TODO: add transactions history

create table image
(
    id         serial primary key,
    url        text not null,
    drug_id    int  not null,
    created_by varchar(20),
    foreign key (drug_id) references drugs (id) on delete cascade,
    foreign key (created_by) references users (username) on delete cascade
);