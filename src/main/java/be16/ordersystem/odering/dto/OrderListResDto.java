package be16.ordersystem.odering.dto;

import be16.ordersystem.odering.domain.OrderDetail;
import be16.ordersystem.odering.domain.OrderStatus;
import be16.ordersystem.odering.domain.Ordering;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderListResDto {
    private Long id;
    private String memberEmail;
    private OrderStatus orderStatus;
    private List<OrderDetailDto> orderDetails;

    public static OrderListResDto fromEntity(Ordering ordering){
        List<OrderDetailDto> orderDetailDtoList = ordering.getOrderDetailList().stream().map(d -> OrderDetailDto.fromEntity(d)).collect(Collectors.toList());
        return OrderListResDto.builder()
                .id(ordering.getId())
                .memberEmail(ordering.getMember().getEmail())
                .orderStatus(ordering.getOrderStatus())
                .orderDetails(orderDetailDtoList)
                .build();
    }
}
