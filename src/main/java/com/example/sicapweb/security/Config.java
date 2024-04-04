package com.example.sicapweb.security;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Calendar;
import java.util.TimeZone;

@Component
public class Config {

    public static String json(Object object){
        return new Gson().toJson(object);
    }


    public static <T> T fromJson(String object, Class<T> tClass){
        return new Gson().fromJson(object, tClass);
    }


    public String ip = "172.30.0.149";
    private Jedis jedis = null;

//    @Bean
//    public Jedis getJedis() {
//        if(jedis == null)
//            jedis = new Jedis(ip, 6379, 2000, 2000);
//        return jedis;
//    }

    private JedisConnectionFactory jedisConnectionFactory;

//    @Bean
//    JedisConnectionFactory jedisConnectionFactory() {
//        JedisConnectionFactory jedisConFactory
//                = new JedisConnectionFactory();
//        jedisConFactory.setHostName(ip);
//        jedisConFactory.setPort(6379);
//        return jedisConFactory;
//    }
//
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate() {
//        RedisTemplate<String, Object> template = new RedisTemplate<>();
//        template.setConnectionFactory(this.jedisConnectionFactory);
//        return template;
//    }

    public void setJedis(Jedis jedis) {
        this.jedis = jedis;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public static void main(String[] args) {
//        com.example.sicapweb.security.User user = new com.example.sicapweb.security.User();
//        Config config = new Config();
//
//        Calendar systemDate = Calendar.getInstance();
//        Calendar saoPauloDate = Calendar.getInstance(TimeZone.getTimeZone("America/Sao_Paulo"));
//        Calendar brazilEastDate = Calendar.getInstance(TimeZone.getTimeZone("Brazil/East"));
//
//        System.out.println("Sem Timezone: " + Config.getFormatedDate(systemDate));
//        System.out.println("America/São_Paulo: " + Config.getFormatedDate(saoPauloDate));
//        System.out.println("Brazil/East: " + Config.getFormatedDate(brazilEastDate));
//
//        config.jedis = new Jedis(config.ip, 6379);
//        config.jedis.set(user.userName, json(user));
//        config.jedis.set(user.userName, json(user));
//        System.out.println(config.jedis.get(user.userName));
    }

    private static String getFormatedDate(Calendar date) {
        StringBuffer formattedDate = new StringBuffer();
        formattedDate.append(date.get(Calendar.DAY_OF_MONTH)).append("/");
        formattedDate.append(date.get(Calendar.MONTH) + 1).append("/");
        formattedDate.append(date.get(Calendar.YEAR)).append(" ");
        formattedDate.append(date.get(Calendar.HOUR_OF_DAY)).append(":");
        formattedDate.append(date.get(Calendar.MINUTE));
        return formattedDate.toString();
    }
}