package be16.ordersystem.odering.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderCreateDto {
    private Long productId;
    private Integer productCount;
}
