package be16.ordersystem.odering.service;

import be16.ordersystem.common.service.SseAlarmService;
import be16.ordersystem.member.domain.Member;
import be16.ordersystem.member.repository.MemberRepository;
import be16.ordersystem.odering.domain.OrderDetail;
import be16.ordersystem.odering.domain.OrderStatus;
import be16.ordersystem.odering.domain.Ordering;
import be16.ordersystem.odering.dto.OrderCreateDto;
import be16.ordersystem.odering.dto.OrderListResDto;
import be16.ordersystem.odering.repository.OrderDetailRepository;
import be16.ordersystem.odering.repository.OrderingRepository;
import be16.ordersystem.product.domain.Product;
import be16.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
public class OrderingService {
    private final OrderingRepository orderingRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final SseAlarmService sseAlarmService;

    public OrderingService(OrderingRepository orderingRepository, OrderDetailRepository orderDetailRepository, OrderDetailRepository orderDetailRepository1, MemberRepository memberRepository, ProductRepository productRepository, OrderDetailRepository orderDetailRepository2, SseAlarmService sseAlarmService) {
        this.orderingRepository = orderingRepository;
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
        this.sseAlarmService = sseAlarmService;
    }

    public Long createOrdering(List<OrderCreateDto> orderCreateDtoList) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
        Ordering ordering = Ordering.builder()
                .member(member)
                .build();
        Long id = orderingRepository.save(ordering).getId();

        for (OrderCreateDto orderCreateDto : orderCreateDtoList) {
            Product product = productRepository.findById(orderCreateDto.getProductId()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
            if (product.getStockQuantity() < orderCreateDto.getProductCount()) {
                // 예외를 강제 발생시켜 모든 임시 저장 사항들을 rollback 처리
                throw new IllegalArgumentException("재고가 부족합니다.");
            }

            OrderDetail orderDetail = OrderDetail.builder()
                    .product(product)
                    .quantity(orderCreateDto.getProductCount())
                    .ordering(ordering)
                    .build();
//            orderDetailRepository.save(orderDetail);          // cascade 미사용
            ordering.getOrderDetailList().add(orderDetail);     // cascade 사용
            product.updateStockQuantity(orderCreateDto.getProductCount());
        }
        // 주문 성공 시 admin 유저에게 알림 메시지 전송
        sseAlarmService.publishMessage("admin@naver.com", email, ordering.getId());

        return id;
    }

    public List<OrderListResDto> orderingList() {
        return orderingRepository.findAll().stream().map(o -> OrderListResDto.fromEntity(o)).collect(Collectors.toList());
    }

    public List<OrderListResDto> myOrders() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        return orderingRepository.findAllByMember(member).stream().map(o -> OrderListResDto.fromEntity(o)).collect(Collectors.toList());
    }

    public Ordering cancel(Long id) {
        // Ordering DB에 상태값 변경 CANCELED
        Ordering ordering = orderingRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 주문입니다."));
        ordering.updateOrderStatus(OrderStatus.CANCELED);

        // redis의 재고값 증가, rdb 재고값 증가
        for (OrderDetail orderDetail : ordering.getOrderDetailList()) {
            orderDetail.getProduct().cacelStockQuantity(orderDetail.getQuantity());
        }
        return ordering;
    }
}
