package be16.ordersystem.product.dto;

import be16.ordersystem.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ProductResDto {
    private Long id;
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private String imagePath;

    public static ProductResDto fromEntity(Product product){
        return ProductResDto.builder()
                .id(product.getId())
                .name(product.getName())
                .category(product.getCategory())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .imagePath(product.getImagePath())
                .build();
    }
}
