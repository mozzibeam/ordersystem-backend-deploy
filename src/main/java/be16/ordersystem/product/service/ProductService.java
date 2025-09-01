package be16.ordersystem.product.service;

import be16.ordersystem.member.domain.Member;
import be16.ordersystem.member.repository.MemberRepository;
import be16.ordersystem.product.domain.Product;
import be16.ordersystem.product.dto.ProductCreateDto;
import be16.ordersystem.product.dto.ProductResDto;
import be16.ordersystem.product.dto.ProductSearchDto;
import be16.ordersystem.product.dto.ProductUpdateDto;
import be16.ordersystem.product.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final S3Client s3Client;

    @Value("${jwt.secretKeyAt}")
    private String secretKey;
    @Value("${cloud.s3.bucket}")
    private String bucket;

    public ProductService(ProductRepository productRepository, MemberRepository memberRepository, S3Client s3Client) {
        this.productRepository = productRepository;
        this.memberRepository = memberRepository;
        this.s3Client = s3Client;
    }

    public Long addProduct(ProductCreateDto productCreateDto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

        Product product = productRepository.save(productCreateDto.toEntity(member));

        if (productCreateDto.getProductImage() != null && !productCreateDto.getProductImage().isEmpty()) {
            String fileName = "product-" + product.getId() + "-productImage-" + productCreateDto.getProductImage().getOriginalFilename();

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(productCreateDto.getProductImage().getContentType())
                    .build();

            try {
                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(productCreateDto.getProductImage().getBytes()));
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new IllegalArgumentException("이미지 업로드 실패");
            }

            String imgUrl = s3Client.utilities().getUrl(a -> a.bucket(bucket).key(fileName)).toExternalForm();
            try {
                String decodedUrl = java.net.URLDecoder.decode(imgUrl, "UTF-8");
                product.updateImageUrl(decodedUrl);
            } catch (UnsupportedEncodingException e) {
                log.error(e.getMessage());
                product.updateImageUrl(imgUrl);
            }
        }

        return product.getId();
    }

    // 1. 동시에 접근하는 상황에서 update 값의 정합성이 깨지고 갱신이상이 발생
    // 2. spring 버전이나 mysql 버전에 따라 jpa에서 강제에러(deadlock)를 유발시켜 대부분의 요청 실패 발생
    public Long productUpdate(Long targetId, ProductUpdateDto productUpdateDto) {
        Product target = productRepository.findById(targetId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));
        target.updateProduct(productUpdateDto);
        String targetFileName = target.getImagePath().substring(target.getImagePath().lastIndexOf("/") + 1);

        if (productUpdateDto.getProductImage() != null && !productUpdateDto.getProductImage().isEmpty()) {
            // 이미지 삭제 후 다시 저장
            s3Client.deleteObject(a -> a.bucket(bucket).key(targetFileName));

            String fileName = "product-" + target.getId() + "-productImage-" + productUpdateDto.getProductImage().getOriginalFilename();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(fileName)
                    .contentType(productUpdateDto.getProductImage().getContentType())
                    .build();

            try {
                s3Client.putObject(putObjectRequest, RequestBody.fromBytes(productUpdateDto.getProductImage().getBytes()));
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new IllegalArgumentException("이미지 업로드 실패");
            }

            String imgUrl = s3Client.utilities().getUrl(a -> a.bucket(bucket).key(fileName)).toExternalForm();
            target.updateImageUrl(imgUrl);
        } else {
            target.updateImageUrl(null);
        }
        return target.getId();
    }

    public Page<ProductResDto> findAll(Pageable pageable, ProductSearchDto productSearchDto) {
        Specification<Product> specification = new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                // root: 엔티티의 소속을 접근하기 위한 객체, critetiaBuilder: 쿼리를 생성하기 위한 객체
                List<Predicate> predicateList = new ArrayList<>();

                if (productSearchDto.getCategory() != null) {
                    predicateList.add(criteriaBuilder.equal(root.get("category"), productSearchDto.getCategory()));
                }
                if (productSearchDto.getProductName() != null) {
                    predicateList.add(criteriaBuilder.like(root.get("name"), "%" + productSearchDto.getProductName() + "%"));
                }

                Predicate[] predicateArr = new Predicate[predicateList.size()];
                for (int i = 0; i < predicateList.size(); i++) {
                    predicateArr[i] = predicateList.get(i);
                }

                Predicate predicate = criteriaBuilder.and(predicateArr);
                return predicate;
            }
        };

        Page<Product> productList = productRepository.findAll(specification, pageable);
        return productList.map(p -> ProductResDto.fromEntity(p));
    }

    public ProductResDto productDetail(Long id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품번호입니다."));
        return ProductResDto.fromEntity(product);
    }
}
