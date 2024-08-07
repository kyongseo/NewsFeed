package hello.blog.config.redis;

//import hello.blog.redis.RedisMessageSubscriber;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;

//@Configuration
//public class RedisListenerConfig {
//    // 특정 Redis 채널을 나타내는 클래스
//    // "messageQueue" 라는 이름의 채널을 나타냄
//    @Bean
//    public ChannelTopic channelTopic(){
//        return new ChannelTopic("chatMessage");
//    }
//
//    // 실제 메시지를 처리하는 리스너 클래스와 이를 호출하는 어댑터 역할
//    // MessageListenerAdapter -> 실제 메시지를 처리할 리스너
//    @Bean
//    public MessageListenerAdapter listenerAdapter(RedisMessageSubscriber subscriber){
//        return new MessageListenerAdapter(subscriber);
//    }
//
//    // Redis 메시지 리스너 컨테이너 --> Redis 채널로부터 메시지를 수신하여 등록된 리스너에게 전달
//    // RedisConnectionFactory -> Redis 서버와의 연결을 설정
//    @Bean
//    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory connectionFactory,
//                                                                       MessageListenerAdapter listenerAdapter){
//        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
//        container.setConnectionFactory(connectionFactory);
//        container.addMessageListener(listenerAdapter, new ChannelTopic("chatMessage"));
//        return container;
//    }
//}