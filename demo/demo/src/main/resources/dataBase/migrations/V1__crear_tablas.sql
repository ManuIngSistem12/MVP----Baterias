create table rol (
    id     serial      primary key,
    nombre varchar(50) not null unique
);

create table marca (
    id     serial       primary key,
    nombre varchar(100) not null unique
);

create table usuario (
    id             serial       primary key,
    identificacion varchar(50)  not null unique,
    nombre         varchar(50)  not null,
    apellido       varchar(50)  not null,
    telefono       varchar(50)  not null,
    correo         varchar(100) not null unique,
    rol_id         int          not null references rol (id),
    contrasena     varchar(300) not null,
    activo         boolean      not null default true
);

create table bateria (
    id             serial         primary key,
    codigo         varchar(50)    not null unique,
    modelo         varchar(100)   not null,
    marca_id       int            not null references marca (id),
    precio         numeric(12, 2) not null check (precio >= 0),
    meses_garantia int            not null check (meses_garantia >= 0),
    existencias    int            not null default 0 check (existencias >= 0),
    activo         boolean        not null default true
);

create table venta (
    id                     serial         primary key,
    usuario_id             int            not null references usuario (id),
    identificacion_cliente varchar(50)    not null,
    nombre_cliente         varchar(100)   not null,
    telefono_cliente       varchar(50)    not null,
    fecha_venta            timestamp      not null default now(),
    total                  numeric(12, 2) not null check (total >= 0)
);

create table detalle_venta (
    id                 serial         primary key,
    venta_id           int            not null references venta (id),
    bateria_id         int            not null references bateria (id),
    cantidad           int            not null check (cantidad > 0),
    precio_unitario    numeric(12, 2) not null check (precio_unitario >= 0),
    fecha_fin_garantia date           not null
);

create index idx_usuario_rol_id on usuario (rol_id);
create index idx_bateria_marca_id on bateria (marca_id);
create index idx_venta_usuario_id on venta (usuario_id);
create index idx_detalle_venta_venta_id on detalle_venta (venta_id);
create index idx_detalle_venta_bateria_id on detalle_venta (bateria_id);
