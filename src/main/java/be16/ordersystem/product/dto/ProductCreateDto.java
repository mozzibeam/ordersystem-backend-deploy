package be16.ordersystem.product.dto;

import be16.ordersystem.member.domain.Member;
import be16.ordersystem.product.domain.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductCreateDto {
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private MultipartFile productImage;

    public Product toEntity(Member member){
        return Product.builder()
                .name(this.name)
                .category(this.category)
                .price(this.price)
                .stockQuantity(this.stockQuantity)
                .member(member)
                .build();
    }

}
