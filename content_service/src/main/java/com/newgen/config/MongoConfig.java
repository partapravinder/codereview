package com.newgen.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;
import org.springframework.stereotype.Component;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI; 

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
    
   /* @SuppressWarnings("deprecation")
	@Override
    public CustomConversions customConversions() {
        converters.add(new FolderWriterConverter());
        converters.add(new FolderReaderConverter());
        converters.add(new ContentWriterConverter());
        converters.add(new ContentReaderConverter());
        return new CustomConversions(converters);
    }*/

    @Override
   public MongoCustomConversions customConversions() {
//    	converters.add(new FolderWriterConverter());
//        converters.add(new FolderReaderConverter());
//        converters.add(new ContentWriterConverter());
//        converters.add(new ContentReaderConverter());
   // 	converters.add(new DoubleToFloatConverter());
   //     converters.add(new FloatToFloatConverter());
    	
        return new MongoCustomConversions(converters);
    }

    @Component
    public class DoubleToFloatConverter implements Converter<Double, Float> {
    	
    	@Override
    	public Float convert(Double source) {
    		return source.floatValue();
    	}
    }
    
    @Component
    public class FloatToFloatConverter implements Converter<Float, Float> {

        @Override
        public Float convert(Float source) {
            return source;
        }
    }
    
	@Override
	public MongoClient mongoClient() {
        return new MongoClient(new MongoClientURI(mongoClientUri));
	}

}
