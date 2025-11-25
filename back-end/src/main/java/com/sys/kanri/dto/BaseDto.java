package com.sys.kanri.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class BaseDto {
    private Long id;
    private boolean isActive;
}

