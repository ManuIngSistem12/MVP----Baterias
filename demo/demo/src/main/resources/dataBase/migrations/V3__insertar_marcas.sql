insert into marca (nombre)
values ('MAC'),
       ('WILLARD'),
       ('BOSCH'),
       ('VARTA'),
       ('TUDOR')
on conflict (nombre) do nothing;
