insert into rol (nombre)
values ('ADMINISTRADORA')
on conflict (nombre) do nothing;
