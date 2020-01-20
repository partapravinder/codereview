package com.newgen.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;

import com.mongodb.MongoClient;
//import com.mongodb.MongoClientURI;
//import com.newgen.converter.ContentReaderConverter;
//import com.newgen.converter.ContentWriterConverter;
//import com.newgen.converter.FolderReaderConverter;
//import com.newgen.converter.FolderWriterConverter;
import com.mongodb.MongoClientURI;

@Configuration
public class MongoConfig extends AbstractMongoConfiguration{

	//private final List<Converter<?, ?>> converters = new ArrayList<>();

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
    
   /* @SuppressWarnings("deprecation")
	@Override
    public CustomConversions customConversions() {
        converters.add(new FolderWriterConverter());
        converters.add(new FolderReaderConverter());
        converters.add(new ContentWriterConverter());
        converters.add(new ContentReaderConverter());
        return new CustomConversions(converters);
    }*/

//    @Override
//    public MongoCustomConversions customConversions() {
//    	converters.add(new FolderWriterConverter());
//        converters.add(new FolderReaderConverter());
//        converters.add(new ContentWriterConverter());
//        converters.add(new ContentReaderConverter());
//        return new MongoCustomConversions(converters);
//    }
    
	@Override
	public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(mongoClientUri));
	}

}
