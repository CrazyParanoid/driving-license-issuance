package ru.mvd.driving.license;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.mvd.driving.license.infrastructure.events.integration.OutputChannelBindings;

@EnableScheduling
@SpringBootApplication
@EnableMongoAuditing
@EnableDiscoveryClient
@EnableBinding(OutputChannelBindings.class)
@EnableTransactionManagement
public class Application {

    @Configuration
    public static class SchedulingConfiguration implements SchedulingConfigurer {

        @Value("${scheduler.pool}")
        private int poolSize;
        @Override
        public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
            ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
            taskScheduler.setPoolSize(100);
            taskScheduler.initialize();
            scheduledTaskRegistrar.setTaskScheduler(taskScheduler);
        }
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory dbFactory) {
        return new MongoTransactionManager(dbFactory);
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
