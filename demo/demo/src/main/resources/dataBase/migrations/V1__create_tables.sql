create table rol (
    id   serial      primary key,
    name varchar(50) not null unique
);

create table brand (
    id   serial       primary key,
    name varchar(100) not null unique
);

create table users (
    id        serial       primary key,
    identity  varchar(50)  not null unique,
    name      varchar(50)  not null,
    last_name varchar(50)  not null,
    number    varchar(50)  not null,
    email     varchar(100) not null unique,
    rol_id    int          not null references rol (id),
    password  varchar(300) not null,
    active    boolean      not null default true
);

create table battery (
    id              serial         primary key,
    code            varchar(50)    not null unique,
    model           varchar(100)   not null,
    brand_id        int            not null references brand (id),
    price           numeric(12, 2) not null check (price >= 0),
    warranty_months int            not null check (warranty_months >= 0),
    stock           int            not null default 0 check (stock >= 0),
    active          boolean        not null default true
);

create table sale (
    id                serial         primary key,
    user_id           int            not null references users (id),
    customer_identity varchar(50)    not null,
    customer_name     varchar(100)   not null,
    customer_number   varchar(50)    not null,
    sale_date         timestamp      not null default now(),
    total             numeric(12, 2) not null check (total >= 0)
);

create table sale_detail (
    id                serial         primary key,
    sale_id           int            not null references sale (id),
    battery_id        int            not null references battery (id),
    quantity          int            not null check (quantity > 0),
    unit_price        numeric(12, 2) not null check (unit_price >= 0),
    warranty_end_date date           not null
);

create index idx_users_rol_id on users (rol_id);
create index idx_battery_brand_id on battery (brand_id);
create index idx_sale_user_id on sale (user_id);
create index idx_sale_detail_sale_id on sale_detail (sale_id);
create index idx_sale_detail_battery_id on sale_detail (battery_id);
