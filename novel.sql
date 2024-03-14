create database
    if not exists novel
    default character set utf8mb4 collate utf8mb4_unicode_ci;

use novel;

drop table if exists `author`;
create table if not exists `author` (
    id varchar(30) not null primary key,
    name text not null,
    intro text, -- 作者介绍
    dynasty varchar(30)
) default character set utf8mb4 collate utf8mb4_unicode_ci;

create fulltext index idx_intro
    on `author`(intro) with parser ngram;

drop table if exists `novel`;
create table if not exists `novel` (
    id varchar(30) not null primary key,
    name text not null,
    author varchar(100) not null,
    author_id varchar(30) not null,
    intro text, -- 书籍介绍
    catalog text not null,
    category varchar(30) not null,
    words int
) default character set utf8mb4 collate utf8mb4_unicode_ci;

create fulltext index idx_intro_catalog
    on `novel`(intro,catalog) with parser ngram;

drop table if exists `novel_chapter`;
create table if not exists `novel_chapter` (
    id varchar(30) not null primary key,
    novel_id text not null, -- novelId
    novel_name text not null, -- novelName
    chapter text not null,
    content mediumtext not null
) default character set utf8mb4 collate utf8mb4_unicode_ci;

-- drop index idx_chapter_content on novel_chapter;
create fulltext index idx_chapter_content
    on `novel_chapter`(chapter,content) with parser ngram;

-- drop index idx_content on novel_chapter;
create fulltext index idx_content
    on `novel_chapter`(content) with parser ngram;

