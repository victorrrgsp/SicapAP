package com.example.sicapweb.security;


import com.example.sicapweb.exception.LoginExpiradoException;
import com.example.sicapweb.security.RedisConfig;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Objects;

@Component
public class RedisConnect {

    @Autowired
    private RedisConfig redis;

    @Autowired
    private Gson gson;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void delete(String id) {
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());
        redisTemplate.delete(String.valueOf(id));
    }

    public User getUser(HttpServletRequest request) {
        try {
            return get(request.getHeader("user"), User.class);
        } catch (Exception e) {
            throw new LoginExpiradoException();
        }
    }


    public User getUser(String id) {
        return get(id, User.class);
    }

    public void expire(String id, Duration duration) {
        redisTemplate.expire(id, duration);
    }

    public void add(String id, Object value) {
        if(Objects.isNull(value)) return;
        String json = gson.toJson(value);
        redisTemplate.opsForValue().set(id,json);
    }

    public <T> T get(String id, Class<T> tClass) {
        Object objectRedis = redis.conn().get(id);

        if(Objects.isNull(objectRedis)) return null;

        String jsonObject = objectRedis.toString();

        T object = gson.fromJson(jsonObject, tClass);
        return object;
    }
}
