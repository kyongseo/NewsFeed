package hello.blog.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

// 메시지를 수신(Subscribe)하고 처리하는 역할을 하는 컴포넌트
// security.package 에서의 MyWebSocketHandler 역할
@Component
public class RedisMessageSubscriber implements MessageListener {

    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static void addSession(WebSocketSession session) {
        sessions.add(session);
    }

    public static void removeSession(WebSocketSession session) {
        sessions.remove(session);
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        String msg = (String) redisTemplate.getValueSerializer().deserialize(message.getBody());

        // WebSocketHandle() 에서 사용하던 부분
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                try {
                    session.sendMessage(new TextMessage(msg));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}