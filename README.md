# full-text-search

[诗词数据集来源](https://github.com/chinese-poetry/chinese-poetry)

[小说数据集来源](https://github.com/luoxuhai/chinese-novel)


## 前期处理

> 为了使对比结果更加公正，给 mysql 和 es 分配的硬件资源是一致的。

* 启动 MySQL，并执行 `novel.sql` 和 `poetry.sql`，数据导入
* 启动 elasticsearch，安装 ik，数据导入

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

V1 如下，如果使用一个 novel Index 来保存所有数据就会造成部分数据的冗余，并且有一些数据我们并不需要每次搜索都出现，
比如 author_name/author_intro/category 等

```json lines
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

所以考虑拆分成两个 Index

novel_detail

```json lines
PUT /novel_detail?pretty
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
      }
    }
  }
}
```

novel_chapter

```json lines
PUT /novel_chapter?pretty
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
          "catalog": {
            "type": "text",
            "analyzer": "ik_smart"
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

### 任务拆分插入

由于部分 novel_content 内容太多，插入 es 的时候可能会出现类似下面的异常

```json
{
  "error": {
    "root_cause": [
      {
        "type": "es_rejected_execution_exception",
        "reason": "rejected execution of coordinating operation [coordinating_and_primary_bytes=95808981, replica_bytes=0, all_bytes=95808981, coordinating_operation_bytes=14351681, max_coordinating_and_primary_bytes=107374182]"
      }
    ],
    "type": "es_rejected_execution_exception",
    "reason": "rejected execution of coordinating operation [coordinating_and_primary_bytes=95808981, replica_bytes=0, all_bytes=95808981, coordinating_operation_bytes=14351681, max_coordinating_and_primary_bytes=107374182]"
  },
  "status": 429
}
```

简单来说就是 http 请求体内容过大，超过了 es 设置的 max_coordinating_and_primary_bytes 限制。
这个 max_coordinating_and_primary_bytes 参数收到 es 内存的影响，一般来说只需要给 es 加内存即可解决。这里给出两种思路：

1、修改 `elasticsearch.yml`
```yaml
indexing_pressure.memory.limit: 15% # 默认是 heap 内存的 10%，并且 es 官方不建议修改
```

2、将大任务拆分成多个小任务再执行插入操作
3、还有一个**可能**是修改配置文件中的 `http.max_content_length: 100mb`，因为错误中出现的 107374182 大约是 100mb，可以考虑将它设置大一点
4、使用 ElasticsearchTemplate#batchIndex 或者使用 BulkRequest 发送批量请求（实际上 ElasticsearchTemplate#batchIndex 底层使用的就是 BulkRequest）
> 一堆命令提交到 ES，那么 ES 执行的顺序和我们提交的顺序是一致的吗？毕竟有些业务场景会对执行命令的顺序有要求。 答案是不一定。

在这里使用了第二种方法，具体实现在 `com.fts.FTSESTest.insert_novel` 方法中。

刚开始的时候只拆分成 7 个 worker 线程来处理，依旧出现 es_rejected_execution_exception 错误，持续加 7 -> 32 -> 48 解决。

这种解决办法也是比较暴力的。

## 结果

* MySQL 使用 like，第一次搜索大概 5s+，在有了缓存的情况下后面的几次查询就变成 200ms+
* MySQL 使用 full-text-index 的情况下，第一次搜索大概在 200ms+，后续 150ms 左右，ft-index 只有在数据量比较大的情况下对比 like 优势才比较明显。
* Elasticsearch 第一次搜索大概 150ms 左右，后续 10ms 左右。

```sql
-- 7s
select * from novel_chapter chapter where chapter.content like '%鬼怪%';

-- 857 ms
SELECT * FROM novel_chapter WHERE MATCH(content) AGAINST('鬼怪' IN NATURAL LANGUAGE MODE);
```

```json lines
// 86 ms
GET novel_chapter/_search
{
  "query": {
    "match": {
      "content": "鬼怪"
    }
  }
}
```

结果显而易见，毕竟：`You know, for search`

## 参考
* es bulk 操作 https://cloud.tencent.com/developer/article/1811300
* ElasticsearchTemplate#batchIndex https://www.cnblogs.com/lori/p/16408670.html
* 计算 Java 对象大小 https://segmentfault.com/a/1190000015009289

