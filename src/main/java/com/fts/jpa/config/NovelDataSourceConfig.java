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
        entityManagerFactoryRef = "entityManagerFactoryNovel",
        basePackages = {"com.fts.jpa.repository.novel"},
        transactionManagerRef = "transactionManagerNovel"
)
public class NovelDataSourceConfig {

    @Autowired
    @Qualifier("novelDataSource")
    private DataSource novelDataSource;

    @Resource
    private HibernateProperties hibernateProperties;

    @Resource
    private JpaProperties jpaProperties;

    @Bean
    @Qualifier("entityManagerNovel")
    public EntityManager entityManagerNovel(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryNovel(builder).getObject().createEntityManager();
    }

    @Bean
    @Qualifier("entityManagerFactoryNovel")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryNovel(EntityManagerFactoryBuilder builder) {
        return builder.dataSource(novelDataSource)
                .properties(getHibernateProperties())
                .packages("com.fts.jpa.entity.novel")
                .persistenceUnit("novelPersistenceUnit")
                .build();
    }

    @Bean
    @Qualifier("transactionManagerNovel")
    public PlatformTransactionManager transactionManagerNovel(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryNovel(builder).getObject());
    }

    private Map<String, Object> getHibernateProperties() {
        return hibernateProperties.determineHibernateProperties(jpaProperties.getProperties(), new HibernateSettings());
    }

}
