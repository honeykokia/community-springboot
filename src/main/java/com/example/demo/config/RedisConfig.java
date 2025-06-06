package com.example.demo.config;

import java.time.Duration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
@EnableCaching
public class RedisConfig {
    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory factory) {

        RedisSerializer<Object> serializer = new GenericJackson2JsonRedisSerializer();
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
            .entryTtl(Duration.ofMinutes(10)) // 設定 TTL 10 分鐘
            .disableCachingNullValues() // 不快取 null 值，避免浪費空間
            .serializeValuesWith(
            RedisSerializationContext.SerializationPair.fromSerializer(serializer)); // 不使用jdk Serializable改用json存入redis 

        return RedisCacheManager.builder(factory)
            .cacheDefaults(config)
            .build();
    }

    // 可選：客製化 key 格式用
    @Bean
    public SimpleKeyGenerator keyGenerator() {
        return new SimpleKeyGenerator();
    }
}
