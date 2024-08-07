package hello.blog.config;

//import hello.blog.websocket.CustomHandshakeInterceptor;
//import hello.blog.websocket.MyWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 양방향 통신 -- 채팅
 * !!! 현재 인증된 사용자의 이름이 표시되도록 구현할 것
 * WebSocketConfigurer 상속
 */
//@Configuration
//@EnableWebSocket
//@RequiredArgsConstructor
//public class WebSocketConfig implements WebSocketConfigurer {
//
//    //security와 함께쓰기위해서 추가
//    private final CustomHandshakeInterceptor customHandshakeInterceptor;
//    private final MyWebSocketHandler myWebSocketHandler;
//
//    @Override
//    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
//        registry.addHandler(myWebSocketHandler, "/ws")
//                .setAllowedOrigins("*")
//                // security와 함께쓰기위해서 추가
//                .addInterceptors(customHandshakeInterceptor);
//    }
//}
