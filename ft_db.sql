drop database if exists ft_db;
create database ft_db default character set utf8mb4;

drop table if exists ft_tb;
create table ft_db(
    title varchar(30) not null,
    content text
) engine innodb;