package com.osttra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(
	    basePackages = "com.osttra.repository.database2", // Specify the package for Database 2 repositories
	    mongoTemplateRef = "mongoTemplate2"
	)
public class Database2Config {

	 @Bean(name = "mongoTemplate2")
	    public MongoTemplate mongoTemplate2() {
	        return new MongoTemplate(MongoClients.create("mongodb://localhost:27017/tema-database"), "tema-database");
	    }
}
