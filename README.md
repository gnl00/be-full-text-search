# full-text-search

[诗词数据集来源](https://github.com/chinese-poetry/chinese-poetry)

[小说数据集来源](https://github.com/luoxuhai/chinese-novel)


## 前期处理

* 数据导入 MySQL
* 数据导入 elasticsearch

## es 安装 ik 分词器

1. [分词器下载](https://github.com/infinilabs/analysis-ik)
2. 复制到 /usr/share/elasticsearch/plugins 目录下，重启 es
3. 查看 es 插件列表 bin/elasticsearch-plugin list


## JPA

**多数据源配置**

有几个可能会出现问题的地方

1. `HibernateProperties that could not be found`，需要加上 `@Primary`
```java
@Primary // fix 'org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties' that could not be found.
@Bean
@Qualifier("poetryDataSource")
@ConfigurationProperties(prefix = "spring.datasource.poetry")
public DataSource poetryDataSource() {
    return DataSourceBuilder.create().build();
}
```

2. Multi entityManagerFactoryXXX，加上 `@Primary`

```java
@Primary
@Bean
@Qualifier("entityManagerFactoryPoetry")
public LocalContainerEntityManagerFactoryBean entityManagerFactoryPoetry(EntityManagerFactoryBuilder builder) {
    return builder.dataSource(poetryDataSource)
            .properties(getHibernateProperties())
            .packages("com.fts.jpa.entity.poetry")
            .persistenceUnit("poetryPersistenceUnit")
            .build();
}
```

3. jdbc-url require...
```yml
# 未使用多数据源写法
datasource:
  username: root
  password: root
  driver-class-name: com.mysql.cj.jdbc.Driver
  url: jdbc:mysql://localhost:3306/db

---

# 使用多数据源写法
datasource:
    primary:
      username: root
      password: root
      driver-class-name: com.mysql.cj.jdbc.Driver
      jdbc-url: jdbc:mysql://localhost:3306/db_primary # 其实就是将参数名 url 修改成 jdbc-url
```

## MySQL 全文检索

### 使用

00、可以先从 `ft_db.sql` 开始对全文索引有一个初步的了解

0、收集数据

novel 目录结构如下

```shell
novel
├── 其他
├──── 小说1
├──── 小说2
├──── 小说3
├──── ...
├── 世态人情
├── 传奇小说
├── 历史演义
├── 英雄传奇
├── 谴责公案
└── 鬼怪神魔
```

1、执行 `novel.sql` 创建表

2、执行 `com.fts.FTSNovelTest.insert_novel` 导入小说数据

3、测试

```sql
-- 使用 like
-- 耗时 12s
select * from novel_chapter chapter where chapter.content like '%神魔%';

-- 使用全文索引
-- 2s+
explain SELECT * FROM novel_chapter WHERE MATCH(content) AGAINST('神魔' IN NATURAL LANGUAGE MODE);
-- 第二次就走缓存，当前数据集 300ms 内出结果
```

提升还是挺明显的，快了将近 6 倍

### 缺点

1. ngram_token_size 分词限制，搜索词最小只能是 token size 
2. ngram 分词解析耗费时间比较长，并且分词模型占用较大空间
3. 分词数据稀疏
4. ...

## ES

### 索引设计

```json
PUT /novel?pretty
{
	"settings": {
	  "number_of_shards": 5,
	  "number_of_replicas": 1,
	  "codec": "best_compression",
	  "max_result_window": "100000000",
	  "refresh_interval":"30s"
	},
	"mappings": {
		"properties": {
		  "novel_id": {
			"type": "keyword"
		  },
		  "novel_name": {
			"type": "text",
            "analyzer": "ik_smart"
		  },
          "author_id": {
			"type": "keyword"
		  },
		  "author_name": {
			"type": "keyword"
		  },
          "author_intro": {
			"type": "text",
            "analyzer": "ik_smart"
		  },
		  "author_dynasty": {
			"type": "keyword"
		  },
		  "novel_intro": {
			"type": "text",
            "analyzer": "ik_smart"
		  },
		  "catalog": {
			"type": "text",
            "analyzer": "ik_smart"
		  },
		  "category": {
			"type": "text",
            "analyzer": "ik_smart"
		  },
          "words": {
			"type": "integer"
		  },
          "chapter_id": {
			"type": "keyword"
		  },
          "chapter_no": {
			"type": "integer"
		  },
		  "content": {
			"analyzer": "ik_smart",
			"term_vector": "with_positions_offsets",
			"type": "text"
		  }
		}
	}
}
```