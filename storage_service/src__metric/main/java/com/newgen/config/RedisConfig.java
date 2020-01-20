package com.newgen.config;

import java.util.Deque;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.support.collections.DefaultRedisList;


@Configuration
public class RedisConfig  {
	/*@Value("${redis.host}")
	private  String redisHost;
	
	@Value("${redis.port}")
    private  int redisPort;

    @Bean
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        factory.setUsePool(true);
        return factory;
    }
    */
    @Bean
    public Deque<String> queue(StringRedisTemplate redisTemplate) { 
        return new DefaultRedisList<>(
                   redisTemplate.boundListOps("STORAGE_QUEUE")); 
    }
}
