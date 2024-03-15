package com.fts.jpa.config;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryPoetry",
        basePackages = {"com.fts.jpa.dao.poetry"},
        transactionManagerRef = "transactionManagerPoetry"
)
public class PoetryDataSourceConfig {

    @Autowired
    @Qualifier("poetryDataSource")
    private DataSource poetryDataSource;

    @Resource
    private HibernateProperties hibernateProperties;

    @Resource
    private JpaProperties jpaProperties;

    @Bean
    @Qualifier("entityManagerPoetry")
    public EntityManager entityManagerPoetry(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryPoetry(builder).getObject().createEntityManager();
    }

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

    @Bean
    @Qualifier("transactionManagerPoetry")
    public PlatformTransactionManager transactionManagerPoetry(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryPoetry(builder).getObject());
    }

    private Map<String, Object> getHibernateProperties() {
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

}
