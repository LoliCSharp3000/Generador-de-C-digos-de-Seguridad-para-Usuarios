drop database if exists generador_usuarios;
create database generador_usuarios
    character set utf8mb4
    collate utf8mb4_general_ci;

use generador_usuarios;

drop table if exists usuarios;

create table usuarios (
    codigo_seguridad varchar(20) primary key,
    nombre_usuario varchar(100) not null,
    tipo enum('NORMAL','PREMIUM','ADMIN') not null,
    estado enum('ACTIVO','INACTIVO','BLOQUEADO') not null,
    fecha_creacion date not null,
    ultima_actividad date not null
);

create index idx_tipo_estado on usuarios(tipo, estado);
create index idx_estado_usuario on usuarios(estado);