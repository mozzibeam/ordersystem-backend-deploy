package be16.ordersystem.odering.controller;

import be16.ordersystem.common.dto.CommonDto;
import be16.ordersystem.odering.domain.Ordering;
import be16.ordersystem.odering.dto.OrderCreateDto;
import be16.ordersystem.odering.service.OrderingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ordering")
public class OrderingController {
    private final OrderingService orderingService;

    public OrderingController(OrderingService orderingService) {
        this.orderingService = orderingService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody List<OrderCreateDto> orderCreateDtoList) {
        Long id = orderingService.createOrdering(orderCreateDtoList);
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(id)
                        .statusCode(HttpStatus.OK.value())
                        .statusMessage("주문 완료")
                        .build(),
                HttpStatus.OK);
    }

    /// 객체 안에 객체가 있을 경우
//    @PostMapping("/create")
//    public ResponseEntity<?> create(@RequestBody OrderDetailDto orderDetailDto){
//        System.out.println(orderDetailDto);
//        return null;
//    }
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> orderList() {
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(orderingService.orderingList())
                        .statusCode(HttpStatus.OK.value())
                        .statusMessage("주문목록 조회")
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping("/myorders")
    public ResponseEntity<?> myOrders() {
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(orderingService.myOrders())
                        .statusCode(HttpStatus.OK.value())
                        .statusMessage("나의 주문목록 조회")
                        .build(),
                HttpStatus.OK);
    }

    @DeleteMapping("/cancel/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> orderCancel(@PathVariable Long id){
        Ordering ordering = orderingService.cancel(id);
        return new ResponseEntity<>(
                CommonDto.builder()
                        .result(ordering.getId())
                        .statusCode(HttpStatus.OK.value())
                        .statusMessage("주문취소 완료")
                        .build(),
                HttpStatus.OK);
    }

}
