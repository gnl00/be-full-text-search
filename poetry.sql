create database
    if not exists poetry
    default character set utf8mb4 collate utf8mb4_unicode_ci;

drop table `tangshi`; -- 全唐诗
create table if not exists `tangshi` (
    id varchar(30) not null primary key,
    author varchar(100) not null,
    paragraphs text not null,
    note text null, -- 注释
    title text not null
) default character set utf8mb4 collate utf8mb4_unicode_ci;

create fulltext index idx_title_paragraphs
    on tangshi(title,paragraphs);

drop table `songshi`; -- 全宋诗
create table if not exists `songshi` (
    id varchar(30) not null primary key,
    author varchar(100) not null,
    paragraphs text not null,
    note text null, -- 注释
    title text not null
) default character set utf8mb4 collate utf8mb4_unicode_ci;

create fulltext index idx_title_paragraphs
    on songshi(title,paragraphs);

drop table `songci`;
create table if not exists `songci` (
    id varchar(30) not null primary key,
    author varchar(30) not null,
    paragraphs text not null,
    rhythmic varchar(50) -- 词/曲牌名
) default character set utf8mb4 collate utf8mb4_unicode_ci;

drop index idx_title_paragraphs on songci;
create fulltext index idx_rhythmic_paragraphs
    on songci(rhythmic,paragraphs);

drop table if exists `yuanqu`;
create table if not exists `yuanqu` (
    id varchar(30) not null primary key,
    author varchar(30) not null,
    paragraphs text not null,
    title varchar(100) -- 词/曲牌名
) default character set utf8mb4 collate utf8mb4_unicode_ci;

drop table if exists `author`;
create table if not exists `author` (
    id varchar(30) not null primary key,
    name varchar(100) not null,
    description text,
    dynasty varchar(20) not null
) default character set utf8mb4 collate utf8mb4_unicode_ci;

