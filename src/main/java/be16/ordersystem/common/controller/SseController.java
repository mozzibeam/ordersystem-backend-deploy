package be16.ordersystem.common.controller;

import be16.ordersystem.common.service.SseEmitterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sse")
public class SseController {
    private final SseEmitterRegistry sseEmitterRegistry;

    @GetMapping("/connect")
    public SseEmitter subscribt() {
        SseEmitter sseEmitter = new SseEmitter(14400*1000L*60);   // 10일 정도 emitter 유효기간 성정
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        sseEmitterRegistry.addSseEmitter(email, sseEmitter);
        try {
            sseEmitter.send(SseEmitter.event().name("connect").data("연결완료"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return sseEmitter;
    }

    @GetMapping("/disconnect")
    public void unSubscribe(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        sseEmitterRegistry.removeEmitter(email);
    }
}
