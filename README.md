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

### 多数据源配置

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