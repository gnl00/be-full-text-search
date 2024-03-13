drop database if exists ft_db;
create database ft_db default character set utf8mb4;

use ft_db;

drop table if exists ft_db;
create table ft_tb(
    id int primary key auto_increment not null,
    content text
);

create fulltext index idx_ft_content
    on `ft_tb`(content) with parser ngram;

INSERT INTO ft_tb(content)
VALUES('MySQL提供了具有许多好的功能的内置全文搜索'),
      ('学习MySQL快速，简单和有趣');

-- 查看分词结果
-- 同时执行下面两条语句
SET GLOBAL innodb_ft_aux_table="ft_db/ft_tb";

SELECT * FROM information_schema.innodb_ft_index_cache
ORDER BY doc_id , position;

-- 搜索测试
explain analyze SELECT
    id, content
FROM
    ft_tb
WHERE
    MATCH (content) AGAINST ('搜索' );

select * from ft_tb where content like '%搜索%'





