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

create index on users (id);



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
    search_vector    tsvector,
    -- TODO: delivery max time
    created_at       timestamp                      default current_timestamp,
    updated_at       timestamp                      default current_timestamp,
    constraint fk_owner_id foreign key (owner_id) references users (id) on delete cascade,
    constraint fk_main_branch foreign key (main_branch) references pharmacies (id) on delete cascade
);

create index pharmacies_idx on pharmacies (id);

create table pharmacy_shifts
(
    pharmacy_id int not null,
    shift_id    int not null,
    constraint fk_pharmacy_id foreign key (pharmacy_id) references pharmacies (id) on delete cascade,
    constraint fk_shift_id foreign key (shift_id) references shifts (id) on delete cascade,
    primary key (pharmacy_id, shift_id)
);

-- POSTGRESQL Full-Text Search -- PHARMACIES

create index pharmacy_search_idx on pharmacies using gin (search_vector);

create function pharmacy_search_vector_update() returns trigger as
$$
begin
    new.search_vector :=
            setweight(to_tsvector('english', coalesce(new.name, '')), 'A') ||
            setweight(to_tsvector('english', coalesce(new.address, '')), 'D');
    return new;
end;
$$ language plpgsql;


create trigger pharmacy_search_vector_update
    before insert or update
    on pharmacies
    for each row
execute function pharmacy_search_vector_update();

-- FUZZY SEARCH
CREATE EXTENSION pg_trgm;
create index pharmacy_name_trigram_index on pharmacies using gin (name gin_trgm_ops);
create index pharmacy_address_trigram_index on pharmacies using gin (address gin_trgm_ops);
-- FUZZY SEARCH

-- POSTGRESQL Full-Text Search -- PHARMACIES

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
    shift_id   int         not null,
    updated_at TIMESTAMP            DEFAULT current_timestamp,
    CONSTRAINT fk_user_id FOREIGN KEY (id) REFERENCES users (id) ON DELETE CASCADE,
    constraint fk_pharmacy_id foreign key (pharmacy) references pharmacies (id) on delete cascade
);

create index employee_idx on employees (id, pharmacy);

create table shifts
(
    id         serial primary key,
    name       text,
    start_time time not null,
    end_time   time not null
);

create index shift_idx on shifts (id);

alter table employees
    add constraint fk_shift_id foreign key (shift_id) references shifts (id) on delete set null;

create table employee_role
(
    emp_id     int  not null,
    role       text not null,
    granted_at timestamp default current_timestamp,
    constraint fk_emp_id foreign key (emp_id) references employees (id),
    primary key (emp_id, role)
);

create table refresh_token
(
    id          serial primary key,
    token       text not null,
    username    text,
    expiry_date timestamp default current_timestamp,
    used        bool      default false
);

create index refresh_token_idx on refresh_token (token);

create table drugs
(
    id            serial primary key,
    name          varchar(250) not null,
    form          text,
    units         smallint     not null default 0,
    full_price    real         not null,
    search_vector tsvector,
    created_by    int,
    created_at    timestamp             default CURRENT_TIMESTAMP,
    updated_at    timestamp             default CURRENT_TIMESTAMP,
    constraint fk_created_by foreign key (created_by) references users (id) on delete cascade
);

create index drug_idx on drugs (id, name, form);

-- POSTGRESQL Full-Text Search -- DRUGS

create index drug_search_idx on drugs using gin (search_vector);

create function drug_search_vector_update() returns trigger as
$$
begin
    new.search_vector :=
            setweight(to_tsvector('english', coalesce(new.name, '')), 'A') ||
            setweight(to_tsvector('english', coalesce(new.form, '')), 'B');
    return new;
end;
$$ language plpgsql;


create trigger drug_search_vector_update
    before insert or update
    on drugs
    for each row
execute function drug_search_vector_update();

-- FUZZY SEARCH
create index drug_name_trigram_index on drugs using gin (name gin_trgm_ops);
create index drug_form_trigram_index on drugs using gin (form gin_trgm_ops);
-- FUZZY SEARCH

-- POSTGRESQL Full-Text Search -- DRUGS


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
    id          serial primary key,
    drug_id     int  not null,
    pharmacy_id int  not null,
    added_by    int  not null,
    stock       int  not null default 0,
    price       real not null default 0,
    expiry_date date not null default current_date + 100,
    created_at  timestamp     default current_timestamp,
    updated_at  timestamp     default current_timestamp,

    constraint fk_drug_id foreign key (drug_id) references drugs (id) on delete cascade,
    constraint fk_pharmacy_id foreign key (pharmacy_id) references pharmacies (id) on delete cascade,
    constraint fk_added_by foreign key (added_by) references users (id) on delete no action
);

create index pharmacy_drug_idx on pharmacy_drug (id, drug_id, pharmacy_id, added_by, expiry_date);

create table order_stats
(
    drug_id     int not null,
    pharmacy_id int not null,
    required    int default 0,
    n_orders    int default 0,
    constraint fk_drug_id foreign key (drug_id) references drugs (id) on delete cascade,
    constraint fk_phar_id foreign key (pharmacy_id) references pharmacies (id) on delete cascade,
    primary key (drug_id, pharmacy_id)
);


create index order_stats_idx on order_stats (drug_id, pharmacy_id);


create table drug_order
(
    id          serial primary key,
    pharmacy_id int not null,
    ordered_by  int not null,
    status      text      default false,
    ordered_at  timestamp default current_timestamp,
    received_at timestamp default current_timestamp,
    constraint fk_phar_id foreign key (pharmacy_id) references pharmacies (id) on delete cascade,
    constraint fk_user_id foreign key (ordered_by) references users (id) on delete set null
);

alter table drug_order
    add column name text;

create index drug_order_idx on drug_order (id, pharmacy_id, ordered_by, status);

create table order_item
(
    order_id int not null,
    drug_id  int not null,
    required int not null default 0,
    constraint fk_order_id foreign key (order_id) references drug_order (id) on delete cascade,
    constraint fk_drug_id foreign key (drug_id) references drugs (id) on delete cascade,
    primary key (order_id, drug_id)
);

create index order_item_idx on order_item (order_id, drug_id, required);

create table receipt
(
    id         serial primary key,
    cashier    int not null,
    status     text,
    created_at timestamp default current_timestamp,
    updated_at timestamp default current_timestamp,

    constraint fk_cashier foreign key (cashier) references users (id) on delete set null
);

alter table receipt
    add column pharmacy_id int;
alter table receipt
    add constraint fk_pharmacy_id foreign key (pharmacy_id) references pharmacies (id);

create table operation
(
    id           serial primary key,
    cashier      int not null,
    type         text,
    completed_at timestamp default current_timestamp,
    receipt_id   int,
    order_id     int,
    constraint fk_cashier foreign key (cashier) references users (id) on delete set null,
    constraint fk_receipt_id foreign key (receipt_id) references receipt (id) on delete set null,
    constraint fk_order_id foreign key (order_id) references drug_order (id) on delete set null
);



create index operation_cashier_idx on operation (id, cashier, type, receipt_id, order_id);

create index receipt_idx on receipt (id, cashier, status);


drop table sales_receipt;

create table sales_receipt
(
    receipt_id       int      not null,
    pharmacy_drug_id int      not null,
    quantity         int      not null default 1,
    amount_due       real     not null default 0.0,
    discount         real,
    units            smallint not null default 0,
    pack             smallint not null default 0,
    constraint fk_receipt_id foreign key (receipt_id) references receipt (id) on delete cascade,
    constraint fk_pd_id foreign key (pharmacy_drug_id) references pharmacy_drug (id) on delete cascade,
    primary key (receipt_id, pharmacy_drug_id)
);

alter table sales_receipt
    add column status text;

create index sales_receipt_id on sales_receipt (receipt_id, pharmacy_drug_id);

create table sales_operation
(
    id          serial primary key,
    drug_id     int  not null,
    pharmacy_id int  not null,
    receipt_id  int,
    order_id    int,
    type        text not null,
    constraint fk_drug_id foreign key (drug_id) references drugs (id) on delete cascade,
    constraint fk_pharmacy_id foreign key (pharmacy_id) references pharmacies (id) on delete cascade,
    constraint fk_receipt_item_id foreign key (receipt_id) references receipt (id) on delete cascade,
    constraint fk_order_id foreign key (order_id) references drug_order (id) on delete cascade
);

alter table sales_operation
    add column cashier_id int;
alter table sales_operation
    add constraint fk_cashier_id foreign key (cashier_id) references users (id) on delete cascade;

alter table sales_operation add column created_at timestamp;
alter table sales_operation add column updated_at timestamp;

alter table sales_operation
    add column status text;

alter table sales_operation
    add column amount int;


create index sales_operation_idx on sales_operation (id, drug_id, pharmacy_id, receipt_id, order_id, type, status);

-- TODO: add separate revenue, profit

-- TODO: add transactions history

create table images
(
    id         serial primary key,
    url        text not null,
    drug_id    int  not null,
    created_by int not null,
    foreign key (drug_id) references drugs (id) on delete cascade,
    foreign key (created_by) references users (id) on delete cascade
);

alter table images add column created_at timestamp;
alter table images add column updated_at timestamp;