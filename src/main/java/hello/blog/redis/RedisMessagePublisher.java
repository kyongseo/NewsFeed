package hello.blog.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

// Redis Pub/Sub 시스템에서 메시지를 발행(Publish)하는 역할
@Service
@RequiredArgsConstructor
public class RedisMessagePublisher {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ChannelTopic channelTopic; // 메시지를 발행할 Redis 채널

    // 주어진 메시지를 Redis 채널에 발행
    public void publish(String message) {
        System.out.println("Redis에 메시지 발행: " + message);
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}