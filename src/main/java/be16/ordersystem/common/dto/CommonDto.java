package be16.ordersystem.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CommonDto {
    private Object result;
    private int statusCode;
    private String statusMessage;
}
