package be16.ordersystem.common.service;

import be16.ordersystem.common.dto.SseMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Component
public class SseAlarmService implements MessageListener {
    private final SseEmitterRegistry sseEmitterRegistry;
    private final RedisTemplate<String, String> redisTemplate;

    public SseAlarmService(SseEmitterRegistry sseEmitterRegistry, @Qualifier("ssePubSub") RedisTemplate<String, String> redisTemplate) {
        this.sseEmitterRegistry = sseEmitterRegistry;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        // Message : 실질적인 메시지가 담겨있는 객체
        // pattern : 채널명
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            SseMessageDto dto = objectMapper.readValue(message.getBody(),SseMessageDto.class);
            System.out.println("dto : " + dto);
            System.out.println("channel : " + new String(pattern));
            //여러개의 채널을 구독하고 있을 경우, 채널명으로 분기처리

            SseEmitter sseEmitter = sseEmitterRegistry.getEmitterMap(dto.getReceiver());
            // emitter객체가 현재 서버에 있으면, 직접 알림 발송, 그렇지 않으면 redis에 publish
            if(sseEmitter!=null){
                try {
                    sseEmitter.send(SseEmitter.event().name("ordered").data(dto));
                    // 사용자가 로그아웃(새로고침) 후에 다시 화면에 들어왔을 때 알림메시지가 남아있으려면 DB에 추가적으로 저장 필요
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 특정 사용자에게 message 발송
    public void publishMessage(String receiver, String sender, Long orderingId/*알림메시지*/) {
        SseMessageDto dto = SseMessageDto.builder()
                .sender(sender)
                .receiver(receiver)
                .orderingId(orderingId)
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String data = null;
        try {
            data = objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // emmiter 객체를 통해 메시지 전송
        SseEmitter sseEmitter = sseEmitterRegistry.getEmitterMap(receiver);

        // emitter 객체가 현재 서버에 있으면 직접 알림 발송, 그렇지 않으면 redis에 publish
        if (sseEmitter != null) {
            try {
                sseEmitter.send(SseEmitter.event().name("ordered").data(data));
                // 사용자가 연결이 안되어있다가 연결이 되거나 새로고침 했을 때 알림메시지가 남아있으려면 rdb에 추가로 저장 필요
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            redisTemplate.convertAndSend("order-channel", data);
        }
    }
}
