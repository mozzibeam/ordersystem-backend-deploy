package be16.ordersystem.member.dto;

import be16.ordersystem.member.domain.Member;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MemberCreateDto {
    @NotEmpty(message = "이름을 입력해 주세요.")
    private String name;
    @NotEmpty(message = "이메일을을 입력해 주세요.")
    private String email;
    @NotEmpty(message = "비밀번호를 입력해 주세요.")
    @Size(min = 8, message = "비밀번호가 너무 짧습니다.")
    private String password;

    public Member toEntity(String encodePassword){
        return Member.builder()
                .name(this.name)
                .email(this.email)
                .password(encodePassword)
                .build();
    }
}
