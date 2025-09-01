package be16.ordersystem.odering.domain;

import be16.ordersystem.common.domain.BaseTimeEntity;
import be16.ordersystem.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@ToString
public class Ordering extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.ORDERED;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "ordering", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<OrderDetail> orderDetailList = new ArrayList<>();

    public void updateOrderStatus(OrderStatus orderStatus){
        this.orderStatus = orderStatus;
    }
}
