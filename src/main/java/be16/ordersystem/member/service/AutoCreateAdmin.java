package be16.ordersystem.member.service;

import be16.ordersystem.member.domain.Member;
import be16.ordersystem.member.domain.Role;
import be16.ordersystem.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutoCreateAdmin implements CommandLineRunner {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (memberRepository.findByEmail("admin@naver.com").isPresent()) {
            return;
        }
        Member admin = Member.builder()
                .email("admin@naver.com")
                .password(passwordEncoder.encode("12341234"))
                .name("admin")
                .role(Role.ADMIN)
                .build();
        memberRepository.save(admin);
    }
}
