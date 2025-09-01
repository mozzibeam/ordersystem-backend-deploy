package be16.ordersystem.product.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ProductUpdateDto {
    private String name;
    private String category;
    private int price;
    private int stockQuantity;
    private MultipartFile productImage;
}
