package com.car.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.car.demo.dao.mapper")
public class DemoApplication {
    //@Bean
    //public RedisTemplate<String, Object> redisTemplate(LettuceConnectionFactory connectionFactory) {
        // 配置redisTemplate
       // RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
      //  redisTemplate.setConnectionFactory(connectionFactory);
      //  redisTemplate.setKeySerializer(new StringRedisSerializer());//key序列化
      //  redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());//value序列化
      //  redisTemplate.afterPropertiesSet();
      //  return redisTemplate;
  //  }

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

}
