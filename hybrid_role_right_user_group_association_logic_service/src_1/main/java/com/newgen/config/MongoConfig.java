package com.newgen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration {

	@Value("${spring.data.mongodb.database}")
	private String database;

	@Value("${spring.data.mongodb.uri}")
	private String mongoClientUri;
	
    @Override
    protected String getDatabaseName() {
    	System.out.println("database:-->"+database);
        return database;
    }
    
	/*
	 * //@Override public MongoClient mongoClient() { // return new MongoClient(new
	 * MongoClientURI(mongoClientUri)); return MongoClients.create(mongoClientUri);
	 * }
	 */

	/*
	 * @Override public MongoClient reactiveMongoClient() { return
	 * MongoClients.create(mongoClientUri); }
	 */
	
    @Override
	public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(mongoClientUri));
	}
	
	

}
