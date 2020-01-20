package com.newgen.config;

import java.util.Deque;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisList;


@Configuration
public class RedisConfig  {
	
	//@Value("${redis.host}")
	//private String redisHost;

	//@Value("${redis.port}")
    //private int redisPortStr;
	
	//private int redisPort = Integer.parseInt(redisPortStr);

/*
    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        factory.setUsePool(true);
        return factory;
    }
   */ 

    /*@Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration(redisHost, 6379);
        //redisStandaloneConfiguration.setPassword(RedisPassword.of("yourRedisPasswordIfAny"));
        
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
    
    @Bean
    public RedisTemplate<String, AsyncFolderOperation> redisTemplate() {
        RedisTemplate<String, AsyncFolderOperation> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory());
        template.setValueSerializer(new GenericToStringSerializer<AsyncFolderOperation>(AsyncFolderOperation.class));
        return template;
    }
    */
    @Bean
    public Deque<String> queue(StringRedisTemplate redisTemplate) { 
        return new DefaultRedisList<>(
                   redisTemplate.boundListOps("COPY_FOLDER_QUEUE")); 
    }
    
}
