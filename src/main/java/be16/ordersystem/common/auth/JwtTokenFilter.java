package be16.ordersystem.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtTokenFilter extends GenericFilterBean {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jwt.secretKeyAt}")
    private String secretKeyAt;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        String bearerToken = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (bearerToken == null) {
            chain.doFilter(request, response);
            return;
        }

        try {
            String token = bearerToken.substring(7);
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(this.secretKeyAt)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            List<GrantedAuthority> authorityList = new ArrayList<>();
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + claims.get("roleCode")));
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(claims.getSubject(), "", authorityList);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            authentication.setDetails(claims);
        } catch (Exception e) {
            e.printStackTrace();
            SecurityContextHolder.clearContext();   // 오류 발생 시 인증 정보 초기화
        }
        chain.doFilter(request, response);
    }
}
