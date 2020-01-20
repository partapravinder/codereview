package com.newgen.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.newgen.converter.StorageCredentialReaderConverter;
import com.newgen.converter.StorageCredentialWriterConverter;
import com.newgen.converter.StorageLocationReaderConverter;
import com.newgen.converter.StorageLocationWriterConverter;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration{

	private final List<Converter<?, ?>> converters = new ArrayList<>();

	@Value("${spring.data.mongodb.database}")
	private String database;

	@Value("${spring.data.mongodb.uri}")
	private String mongoClientUri;
	
    @Override
    protected String getDatabaseName() {
        return database;
    }

   /* @Override
    public Mongo mongo() throws Exception {
    	
        return new MongoClient(new MongoClientURI(mongoClientUri));
    }*/
    
    @Override
    public MongoCustomConversions customConversions() {
        converters.add(new StorageCredentialWriterConverter());
        converters.add(new StorageCredentialReaderConverter());
        converters.add(new StorageLocationWriterConverter());
        converters.add(new StorageLocationReaderConverter());
        return new MongoCustomConversions(converters);
    }

	@Override
	public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(mongoClientUri));
	}

}
