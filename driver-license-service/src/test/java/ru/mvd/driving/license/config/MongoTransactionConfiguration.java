package ru.mvd.driving.license.config;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@TestConfiguration(proxyBeanMethods = false)
public class MongoTransactionConfiguration {
    @Autowired
    private MongoTransactionManager mongoTransactionManager;

    @Bean
    public TransactionTemplate transactionTemplate() {
        return new TransactionTemplate(mongoTransactionManager);
    }
}
