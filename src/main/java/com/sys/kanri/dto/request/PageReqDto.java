package com.sys.kanri.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PageReqDto {
    private int page = 0;        // mặc định trang 0
    private int size = 10;       // mặc định 10 item / trang
    private String keyword = ""; // search keyword
}
