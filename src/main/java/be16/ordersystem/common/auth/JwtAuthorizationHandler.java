package be16.ordersystem.common.auth;

import be16.ordersystem.common.dto.CommonErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtAuthorizationHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error(accessDeniedException.getMessage());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);    // 403
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        CommonErrorDto dto = new CommonErrorDto(HttpStatus.FORBIDDEN.value(), "권한이 없습니다.");
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(dto);

        PrintWriter printWriter = response.getWriter();
        printWriter.write(body);
        printWriter.flush();
    }
}
