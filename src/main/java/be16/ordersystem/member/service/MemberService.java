package be16.ordersystem.member.service;

import be16.ordersystem.common.auth.JwtTokenProvider;
import be16.ordersystem.member.domain.Member;
import be16.ordersystem.member.dto.LoginReqDto;
import be16.ordersystem.member.dto.MemberCreateDto;
import be16.ordersystem.member.dto.MemberResDto;
import be16.ordersystem.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Long memberJoin(MemberCreateDto memberCreateDto) {
        if (memberRepository.findByEmail(memberCreateDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }

        String encodePassword = passwordEncoder.encode(memberCreateDto.getPassword());
        Member member = memberRepository.save(memberCreateDto.toEntity(encodePassword));
        return member.getId();
    }

    public Member login(LoginReqDto loginReqDto) {
        Optional<Member> optionalMember = memberRepository.findByEmail(loginReqDto.getEmail());
        boolean check = true;

        if (!optionalMember.isPresent()) {
            check = false;
        } else if (!passwordEncoder.matches(loginReqDto.getPassword(), optionalMember.get().getPassword())) {
            check = false;
        }

        if (!check) {
            throw new IllegalArgumentException("email 또는 비밀번호가 일치하지 않습니다.");
        }

        return optionalMember.get();
    }

    @Transactional(readOnly = true)
    public List<MemberResDto> findAll() {
        return memberRepository.findAll().stream().map(a -> MemberResDto.fromEntity(a)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MemberResDto memberDetail(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        return MemberResDto.fromEntity(member);
    }

    @Transactional(readOnly = true)
    public MemberResDto myInfo() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return MemberResDto.fromEntity(memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다.")));
    }

    public void delete() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다.")).deleteMember("Y");
    }
}
