package be16.ordersystem.member.controller;

import be16.ordersystem.common.auth.JwtTokenProvider;
import be16.ordersystem.common.dto.CommonDto;
import be16.ordersystem.member.domain.Member;
import be16.ordersystem.member.dto.*;
import be16.ordersystem.member.service.MemberService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, JwtTokenProvider jwtTokenProvider) {
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/create")
    public ResponseEntity<?> memberJoin(@RequestBody @Valid MemberCreateDto memberCreateDto) {
        Long id = memberService.memberJoin(memberCreateDto);
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(id)
                        .statusCode(HttpStatus.CREATED.value())
                        .statusMessage("회원가입 완료")
                        .build()
                , HttpStatus.CREATED);
    }

    @PostMapping("/dologin")
    public ResponseEntity<?> login(@RequestBody LoginReqDto loginReqDto) {
        Member member = memberService.login(loginReqDto);
        // at 토큰 생성
        String accessToken = jwtTokenProvider.createAtToken(member);
        // rt 토큰 생성
        String refreshToken = jwtTokenProvider.createRtToken(member);

        LoginResDto loginResDto = LoginResDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(loginResDto)
                        .statusCode(HttpStatus.OK.value())
                        .statusMessage("로그인 완료")
                        .build()
                , HttpStatus.OK);
    }

    // rt를 통한 at 갱신 요청
    @PostMapping("/refresh-at")
    public ResponseEntity<?> generateNewAt(@RequestBody RefreshTokenDto refreshTokenDto){
        // rt 검증 로직
        Member member = jwtTokenProvider.validateRt(refreshTokenDto.getRefreshToken());

        // at 신규 생성
        String accessToken = jwtTokenProvider.createAtToken(member);
        LoginResDto loginResDto = LoginResDto.builder()
                .accessToken(accessToken)
                .build();

        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(loginResDto)
                        .statusCode(HttpStatus.OK.value())
                        .statusMessage("AT 재발급 완료")
                        .build()
                , HttpStatus.OK);
    }

    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> findAll() {
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(memberService.findAll())
                        .statusCode(HttpStatus.OK.value())
                        .statusMessage("회원 목록 조회")
                        .build()
                , HttpStatus.OK);
    }

    @GetMapping("/detail/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> detail(@PathVariable Long memberId) {
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(memberService.memberDetail(memberId))
                        .statusCode(HttpStatus.OK.value())
                        .statusMessage("회원 목록 조회")
                        .build()
                , HttpStatus.OK);
    }
    @GetMapping("/myinfo")
    public ResponseEntity<?> myInfo() {
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(memberService.myInfo())
                        .statusCode(HttpStatus.OK.value())
                        .statusMessage("마이페이지 조회")
                        .build()
                , HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete() {
        memberService.delete();
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result("ok")
                        .statusCode(HttpStatus.OK.value())
                        .statusMessage("회원탈퇴 완료")
                        .build()
                , HttpStatus.OK);
    }
}
