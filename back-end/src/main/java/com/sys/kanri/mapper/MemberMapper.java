package com.sys.kanri.mapper;

import com.sys.kanri.dto.response.MemberResDto;
import com.sys.kanri.entities.Member;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface MemberMapper {

    @Mapping(target = "role", source = "role.name")
    MemberResDto toDto(Member member);
}
