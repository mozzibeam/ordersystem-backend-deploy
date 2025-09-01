package be16.ordersystem.odering.dto;

import be16.ordersystem.odering.domain.OrderDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OrderDetailDto {
    private Long detailId;
    private String productName;
    private int productCount;

    public static OrderDetailDto fromEntity(OrderDetail orderDetail){
        return OrderDetailDto.builder()
                .detailId(orderDetail.getId())
                .productName(orderDetail.getProduct().getName())
                .productCount(orderDetail.getQuantity())
                .build();
    }
}
