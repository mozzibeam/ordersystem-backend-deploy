package be16.ordersystem.common.auth;

import be16.ordersystem.common.dto.CommonErrorDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtAuthenticationHandler implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        log.error(authException.getMessage());
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);    // 401
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        CommonErrorDto dto = new CommonErrorDto(HttpStatus.UNAUTHORIZED.value(), "유효하지 않은 token 입니다.");
        ObjectMapper objectMapper = new ObjectMapper();
        String body = objectMapper.writeValueAsString(dto);

        PrintWriter printWriter = response.getWriter();
        printWriter.write(body);
        printWriter.flush();
    }
}
