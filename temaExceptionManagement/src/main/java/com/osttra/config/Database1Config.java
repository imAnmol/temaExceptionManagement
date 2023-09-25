package com.osttra.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.client.MongoClients;

@Configuration
@EnableMongoRepositories(
	    basePackages = "com.osttra.repository.database1", // Specify the package for Database 1 repositories
	    mongoTemplateRef = "mongoTemplate1"
	    )

public class Database1Config {

	 @Primary
	 @Bean(name = "mongoTemplate1")
	    public MongoTemplate mongoTemplate1() {
	        //return new MongoTemplate(MongoClients.create("mongodb+srv://admin:admin@cluster0.zucrbvv.mongodb.net/mongo-exception-api"), "mongo-exception-api");
	        return new MongoTemplate(MongoClients.create("mongodb://localhost:27017/mongo-ecxeption-api"), "mongo-exception-api");
	    }	
}
