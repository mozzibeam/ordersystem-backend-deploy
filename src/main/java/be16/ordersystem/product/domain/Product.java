package be16.ordersystem.product.domain;

import be16.ordersystem.common.domain.BaseTimeEntity;
import be16.ordersystem.member.domain.Member;
import be16.ordersystem.product.dto.ProductUpdateDto;
import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@ToString
public class Product extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String category;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private int stockQuantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    private String imagePath;

    public void updateImageUrl(String imgUrl) {
        this.imagePath = imgUrl;
    }

    public void updateProduct(ProductUpdateDto productUpdateDto) {
        this.name = productUpdateDto.getName();
        this.category = productUpdateDto.getCategory();
        this.price = productUpdateDto.getPrice();
        this.stockQuantity = productUpdateDto.getStockQuantity();
    }

    public void updateStockQuantity(int quantity){
        this.stockQuantity -= quantity;
    }

    public void cacelStockQuantity(int quantity){
        this.stockQuantity += quantity;
    }
}
