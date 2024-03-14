create database
    if not exists poetry
    default character set utf8mb4 collate utf8mb4_unicode_ci;

use poetry;

drop table if exists `tangshi`; -- 全唐诗
create table if not exists `tangshi` (
    id varchar(30) not null primary key,
    author varchar(100) not null,
    paragraphs text not null,
    note text null, -- 注释
    title text not null
) default character set utf8mb4 collate utf8mb4_unicode_ci;

create fulltext index idx_title_paragraphs
    on tangshi(title,paragraphs) with parser ngram;

drop table if exists `songshi`; -- 全宋诗
create table if not exists `songshi` (
    id varchar(30) not null primary key,
    author varchar(100) not null,
    paragraphs text not null,
    note text null, -- 注释
    title text not null
) default character set utf8mb4 collate utf8mb4_unicode_ci;

create fulltext index idx_title_paragraphs
    on songshi(title,paragraphs) with parser ngram;

drop table if exists `songci`;
create table if not exists `songci` (
    id varchar(30) not null primary key,
    author varchar(30) not null,
    paragraphs text not null,
    rhythmic varchar(50) -- 词/曲牌名
) default character set utf8mb4 collate utf8mb4_unicode_ci;

-- drop index idx_rhythmic_paragraphs on songci;
create fulltext index idx_rhythmic_paragraphs
    on songci(rhythmic,paragraphs) with parser ngram;

drop table if exists `yuanqu`;
create table if not exists `yuanqu` (
    id varchar(30) not null primary key,
    author varchar(30) not null,
    paragraphs text not null,
    title varchar(100) -- 词/曲牌名
) default character set utf8mb4 collate utf8mb4_unicode_ci;

create fulltext index idx_paragraphs
    on yuanqu(paragraphs) with parser ngram;

drop table if exists `author`;
create table if not exists `author` (
    id varchar(30) not null primary key,
    name varchar(100) not null,
    description text,
    dynasty varchar(20) not null
) default character set utf8mb4 collate utf8mb4_unicode_ci;

-- drop index idx_author_desc on author;
create fulltext index idx_author_desc
    on author(description) with parser ngram;

-- 查看分词结果
-- 同时执行下面两条语句
SET GLOBAL innodb_ft_aux_table="poetry/songci";

SELECT * FROM information_schema.innodb_ft_index_cache
ORDER BY doc_id , position;

/* TEST CASE
select * from songci where paragraphs like '%满江红%' or songci.rhythmic like '%满江红%';
explain select * from songci where paragraphs like '%满江红%' or songci.rhythmic like '%满江红%';

SELECT * FROM songci WHERE MATCH(rhythmic, paragraphs) AGAINST('满江红' IN NATURAL LANGUAGE MODE);
explain SELECT * FROM songci WHERE MATCH(rhythmic, paragraphs) AGAINST('满江红' IN NATURAL LANGUAGE MODE);
explain analyze SELECT * FROM songci WHERE MATCH(rhythmic, paragraphs) AGAINST('满江红' IN NATURAL LANGUAGE MODE);
*/


