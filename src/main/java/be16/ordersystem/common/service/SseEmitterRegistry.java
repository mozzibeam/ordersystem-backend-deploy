package be16.ordersystem.common.service;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SseEmitterRegistry {
    // SseEmitter는 연결된 사용자 정보(ip, mac 등)를 의미
    // ConcurrentHashMap는 Thread-Safe 한 map(동시성 이슈 발생 X)
    private Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();


    public void addSseEmitter(String email, SseEmitter sseEmitter) {
        emitterMap.put(email, sseEmitter);
    }

    public void removeEmitter(String email){
        emitterMap.remove(email);
    }

    public SseEmitter getEmitterMap(String email){
        return this.emitterMap.get(email);
    }
}
