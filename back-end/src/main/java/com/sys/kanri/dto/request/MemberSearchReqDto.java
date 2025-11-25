package com.sys.kanri.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberSearchReqDto extends PaginationReqDto{
    private String keyword;
}
