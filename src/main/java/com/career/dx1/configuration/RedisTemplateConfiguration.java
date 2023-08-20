package com.career.dx1.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration.LettuceClientConfigurationBuilder;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@Profile("!default")
public class RedisTemplateConfiguration {
    @Bean
    @Primary
    @ConfigurationProperties("spring.redis")
    RedisProperties redisProperties(
            // azure keyvault
            @Value("${spring-redis-password}") String password) {
        RedisProperties redisProperties = new RedisProperties();
        redisProperties.setPassword(password);
        return redisProperties;
    }

    @Bean
    RedisConnectionFactory redisConnectionFactory(RedisProperties redisProperties) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisProperties.getHost());
        config.setPort(redisProperties.getPort());
        config.setDatabase(0);
        config.setPassword(RedisPassword.of(redisProperties.getPassword()));

        LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder()
            .commandTimeout(redisProperties.getTimeout());
        if (redisProperties.isSsl()) {
            builder.useSsl();
        }

        return new LettuceConnectionFactory(config, builder.build());
    }

    @Bean
    RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

}
