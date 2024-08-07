package hello.blog.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

//@Configuration
//@EnableRedisRepositories // Spring Data Redis 리포지토리 활성화
//public class RedisConfig {
//
//    // RedisConnectionFactory - Redis 와의 연결을 생성하는 데 사용
//    @Bean // Db 에 접속하기 위한 code
//    public RedisConnectionFactory redisConnectionFactory() {
//        RedisStandaloneConfiguration redisStandaloneConfiguration =
//                new RedisStandaloneConfiguration("localhost", 6379);
//
//        redisStandaloneConfiguration.setPassword("1234");
//
//        // LettuceConnectionFactory 를 통해 Redis 서버와의 연결이 설정
//        return new LettuceConnectionFactory(redisStandaloneConfiguration);
//    }
//
//    // RedisTemplate --> Redis 서버와 상호작용
//    @Bean // 레디스가 정보를 저장할 때 탬플릿을 통해 저장을 함..
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//
//        // 우리가 쓰고자하는 설정들..
//        // Redis 에 저장하거나 Redis 에서 읽어올 때 데이터를 직렬화 설정
//        // StringRedisSerializer -> 문자열 데이터를 직렬화
//        // GenericJackson2JsonRedisSerializer -> JSON 형식으로 데이터를 직렬화
//        redisTemplate.setKeySerializer(new StringRedisSerializer());
//        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
//        // redisTemplate.setHashValueSerializer(new StringRedisSerializer());
//        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
//        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
//        return redisTemplate;
//    }
//}