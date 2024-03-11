package com.fts.jpa.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary // fix 'org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties' that could not be found.
    @Bean
    @Qualifier("poetryDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.poetry")
    public DataSource poetryDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    @Qualifier("novelDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.novel")
    public DataSource novelDataSource() {
        return DataSourceBuilder.create().build();
    }

}
