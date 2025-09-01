package be16.ordersystem.member.domain;

import be16.ordersystem.common.domain.BaseTimeEntity;
import be16.ordersystem.product.domain.Product;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@ToString
// jpql을 제외하고 모든 조회 쿼리에 where del_yn = 'N'을 붙이는 효과
@SQLRestriction("del_yn = 'N'")
public class Member extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    @Builder.Default
    private String delYn = "N";
    @Column(nullable = false)
    @Builder.Default
    private Role role = Role.USER;

    public void deleteMember(String delYn) {
        this.delYn = delYn;
    }
}
