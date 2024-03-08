# full-text-search

[数据集来源](https://github.com/chinese-poetry/chinese-poetry)

## 前期处理

* 数据导入 MySQL
* 数据导入 elasticsearch

## es 安装 ik 分词器

1. [分词器下载](https://github.com/infinilabs/analysis-ik)
2. 复制到 /usr/share/elasticsearch/plugins 目录下，重启 es
3. 查看 es 插件列表 bin/elasticsearch-plugin list