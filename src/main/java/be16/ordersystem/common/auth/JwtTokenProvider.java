package be16.ordersystem.common.auth;

import be16.ordersystem.member.domain.Member;
import be16.ordersystem.member.repository.MemberRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    private final MemberRepository memberRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Value("${jwt.expirationAt}")
    private int expirationAt;
    @Value("${jwt.secretKeyAt}")
    private String secretKeyAt;
    @Value("${jwt.expirationRt}")
    private int expirationRt;
    @Value("${jwt.secretKeyRt}")
    private String secretKeyRt;

    private Key secret_at_key;
    private Key secret_rt_key;

    // Qualifier은 기본적으로 메서드를 통한 주입 가능, 따라서 생성자 주입 방식을 활용해야 @Qualifier 사용 가능
    public JwtTokenProvider(MemberRepository memberRepository, @Qualifier("rtInventory") RedisTemplate<String, String> redisTemplate) {
        this.memberRepository = memberRepository;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init() {
        // JwtTokenFilter와 동일한 방식으로 키 생성
        secret_at_key = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKeyAt), SignatureAlgorithm.HS512.getJcaName());
        secret_rt_key = new SecretKeySpec(java.util.Base64.getDecoder().decode(secretKeyRt), SignatureAlgorithm.HS512.getJcaName());
    }

    public String createAtToken(Member member) {
        String email = member.getEmail();
        String roleCode = member.getRole().toString();

        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roleCode", roleCode);
        Date now = new Date();
        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationAt * 60 * 1000L))
                .signWith(this.secret_at_key, SignatureAlgorithm.HS512)
                .compact();
        return accessToken;
    }

    public String createRtToken(Member member) {
        // 유효기간이 긴 rt 토큰 생성
        String email = member.getEmail();
        String roleCode = member.getRole().toString();
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("roleCode", roleCode);
        Date now = new Date();
        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationRt * 60 * 1000L))
                .signWith(this.secret_rt_key, SignatureAlgorithm.HS512)
                .compact();

        // rt토큰을 radis에 저장: key-value 형식으로 set
        redisTemplate.opsForValue().set(member.getEmail(), refreshToken);
        return refreshToken;
    }

    public Member validateRt(String refreshToken){
        // rt 그 자체를 검증
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secret_rt_key)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

        String email = claims.getSubject();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        // redis의 값과 일치하는지 검증
        String redisRt = redisTemplate.opsForValue().get(member.getEmail());
        if (!redisRt.equals(refreshToken)){
            throw new IllegalArgumentException("잘못된 토큰입니다.");
        }
        return member;
    }
}
