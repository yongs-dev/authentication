package com.mark.authentication.config.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    private final String HOST;
    private final int PORT;
    private final String PASSWORD;

    public RedisConfig(
            @Value("${spring.data.redis.host}") final String HOST,
            @Value("${spring.data.redis.port}") final int PORT,
            @Value("${spring.data.redis.password}") final String PASSWORD
    ) {
        this.HOST = HOST;
        this.PORT = PORT;
        this.PASSWORD = PASSWORD;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        final RedisStandaloneConfiguration redisConfiguration = new RedisStandaloneConfiguration();
        redisConfiguration.setHostName(HOST);
        redisConfiguration.setPort(PORT);
        redisConfiguration.setPassword(PASSWORD);
        return new LettuceConnectionFactory(redisConfiguration);
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate() {
        final RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }
}
