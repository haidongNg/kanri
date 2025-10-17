package com.sys.kanri.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationReqDto {
    @Min(value = 0, message = "Số trang không được nhỏ hơn 0.")
    private int page = 0;

    @Min(value = 1, message = "Kích thước trang không được nhỏ hơn 1.")
    private int size = 10;
}
