package be16.ordersystem.member.dto;

import be16.ordersystem.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class MemberResDto {
    private Long id;
    private String name;
    private String email;

    public static MemberResDto fromEntity(Member member){
        return MemberResDto.builder()
                .id(member.getId())
                .name(member.getName())
                .email(member.getEmail())
                .build();
    }
}
