package com.example.sicapweb.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@Configuration
public class RedisConfig {

//    @Value("${redisrc.cluster}")
//    private String redisHostName;
//
//    @Value("${redisrc.port}")
//    private Integer port;

    private RedisTemplate<String, String> template = null;

    private RedisConnectionFactory connectionFactory;

    @Autowired
    RedisConfig(RedisConnectionFactory connectionFactory){
        this.connectionFactory = connectionFactory;
    }
    public ValueOperations conn() {
        return redisTemplate(connectionFactory).opsForValue();
    }


    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        if(this.template == null) {
            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setConnectionFactory(connectionFactory);
            this.template = template;
        }
        return this.template;
    }


    @Bean
    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
