package hello.blog.websocket;

//import hello.blog.redis.RedisMessagePublisher;
//import hello.blog.redis.RedisMessageSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
/**
 * 중요한 일들은 다 handleTextMessage() 얘가 담당할 것
 * handleTextMessage() 메서드에서 주요 작업을 하면 된다.
 * TextWebSocketHandler.class 를 상속받아서 채팅 구현
 * !!! 현재 인증된 사용자의 이름이 표시되도록 구현할 것
 */
@Component // 추가
@RequiredArgsConstructor // 추가
public class MyWebSocketHandler extends TextWebSocketHandler {

    private final RedisMessagePublisher redisMessagePublisher; // 추가

//    //웹소켓으로 접속한 세션들을 관리하기 위한 저장소를 생성한다.
//    // 1. 채팅 구현을 위해 웹 소켓으로 접속한 세션들을 관리하기 위한 저장소를 생성한다.
//    private static final Set<WebSocketSession> sessions = Collections.synchronizedSet(new HashSet<>());

    // 커넥션이 성공되면 그 이후에 할 일 -- 양방향 채팅에서는 필수
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("세션 연결 성공 :: " + session.getId());
        RedisMessageSubscriber.addSession(session); // 추가
    }

    // 핵심 메서드 -- 양방향 채팅에서는 필수
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //Echo 서버는 메시지보낸 세션에만 다시 메시지를 보내주면 끝!!
        //그렇다면 chat 서버는???
        // a. 단방향 --> Echo Server는 메시지보낸 세션에만 다시 메시지를 보내주면 끝이었다. --> 이때 단뱡항들은 각각 세션에 저장되어 있다.
        String payload = message.getPayload(); //실제 메시지 (헤더나 메타정보나 이런것들을 제외한..)
        System.out.println("수신된 메시지 :: " + payload);

        // a. 단방향 채팅 만들 때.. ECHO
//        // session.sendMessage(new TextMessage("Echo : " + payload)); // -> 이 코드가 아래 4번에서 실행

        //현재 인증된 사용자 정보 가져옴.
        // c. 현재 인증된 사용자 정보 가져옴
        // CustomHandshakeInterceptor.class 에 지정한 "SPRING_SECURITY_CONTEXT" 과 동일하게 저장할 것
        SecurityContext securityContext = (SecurityContext) session.getAttributes().get("SPRING_SECURITY_CONTEXT");
        String username = "Unknown user";

        if(securityContext != null && securityContext.getAuthentication() != null &&
                securityContext.getAuthentication().getPrincipal() instanceof UserDetails){
            UserDetails userDetails = (UserDetails) securityContext.getAuthentication().getPrincipal();
            username = userDetails.getUsername();
        }

        // b. 양방향 채팅
//        // 4. 채팅은 전체에게 보내줘야 하니깐
//        synchronized (sessions){
//        for (WebSocketSession webSocketSession : sessions){
//            if(webSocketSession.isOpen()){
//                webSocketSession.sendMessage(new TextMessage( username + ": "+payload));
//            }
//        }

        // 기존에서 레디스를 사용하니깐
        // 레디스를 이용하도록 바꾸기
        redisMessagePublisher.publish(username + " : "+payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        RedisMessageSubscriber.removeSession(session); // 추가
        System.out.println("세션 연결 종료 :: "+ session.getId());
    }
}