package hello.blog.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

/**
 * !!! 현재 인증된 사용자의 이름이 표시되도록 구현할 것
 */
//@Component
//@Slf4j
//public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
//                                   Map<String, Object> attributes) throws Exception {
//        // 인증정보로부터 username을 얻어온다.
//        SecurityContext context = SecurityContextHolder.getContext();
//        log.info("이름 얻어왔나");
//        if (context.getAuthentication() != null) {
//            attributes.put("SPRING_SECURITY_CONTEXT", context); // context() 자체를 "SPRING_SECURITY_CONTEXT" 이 이름으로 지정
//        }
//
//        return super.beforeHandshake(request, response, wsHandler, attributes);
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
//        super.afterHandshake(request, response, wsHandler, ex);
//    }
//}

//@Component
//public class CustomHandshakeInterceptor extends HttpSessionHandshakeInterceptor {
//    @Override
//    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
//                                   Map<String, Object> attributes) throws Exception {
//        // 인증정보로부터 username을 얻어온다.
//        SecurityContext context = SecurityContextHolder.getContext();
//        if(context.getAuthentication() != null){
//            attributes.put("SPRING_SECURITY_CONTEXT",context); // context() 자체를 "SPRING_SECURITY_CONTEXT" 이 이름으로 지정
//        }
//
//        return super.beforeHandshake(request, response, wsHandler, attributes);
//    }
//
//    @Override
//    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
//        super.afterHandshake(request, response, wsHandler, ex);
//    }
//}