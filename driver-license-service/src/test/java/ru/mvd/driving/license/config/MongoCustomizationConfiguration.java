package ru.mvd.driving.license.config;

import com.mongodb.BasicDBList;
import com.mongodb.ServerAddress;
import com.mongodb.client.ClientSession;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.connection.ServerDescription;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.awaitility.Awaitility;
import org.bson.Document;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.time.Duration;
import java.util.List;

@TestConfiguration(proxyBeanMethods = false)
public class MongoCustomizationConfiguration {
    private static final String REPLICA_SET_NAME = "rs1";

    @Bean
    public IMongodConfig embeddedMongoConfiguration(EmbeddedMongoProperties embeddedProperties) throws IOException {
        IMongoCmdOptions cmdOptions = new MongoCmdOptionsBuilder().useNoJournal(false).build();
        return new MongodConfigBuilder().version(Version.Main.PRODUCTION)
                .replication(new Storage(null, REPLICA_SET_NAME, 0)).cmdOptions(cmdOptions)
                .stopTimeoutInMillis(60000).build();
    }

    @Bean
    MongoInitializer mongoInitializer(MongoClient client, MongoTemplate template) {
        return new MongoInitializer(client, template);
    }

    static class MongoInitializer implements InitializingBean {
        private final MongoClient client;
        private final MongoTemplate template;

        MongoInitializer(MongoClient client, MongoTemplate template) {
            this.client = client;
            this.template = template;
        }

        @Override
        public void afterPropertiesSet() throws Exception {
            List<ServerDescription> servers = this.client.getClusterDescription().getServerDescriptions();
            ServerAddress address = servers.get(0).getAddress();
            BasicDBList members = new BasicDBList();
            members.add(new Document("_id", 0).append("host", address.getHost() + ":" + address.getPort()));
            Document config = new Document("_id", REPLICA_SET_NAME);
            config.put("members", members);
            MongoDatabase admin = this.client.getDatabase("admin");
            admin.runCommand(new Document("replSetInitiate", config));
            Awaitility.await().atMost(Duration.ofMinutes(1)).until(() -> {
                try (ClientSession session = this.client.startSession()) {
                    return true;
                } catch (Exception ex) {
                    return false;
                }
            });
            this.template.createCollection("driving-licenses");
            this.template.createCollection("stored-events");
        }

    }
}
