package com.sys.kanri.services;

import com.sys.kanri.dto.request.ChangePasswordReqDto;
import com.sys.kanri.dto.request.MemberSearchReqDto;
import com.sys.kanri.dto.request.RegisterReqDto;
import com.sys.kanri.dto.response.MemberResDto;
import com.sys.kanri.dto.response.PaginationResDto;
import com.sys.kanri.entities.Member;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Optional;

public interface MemberService extends UserDetailsService {
    Optional<Member> findByUsername(String username);
    MemberResDto getMemberById(Long id);
    void deleteById(Long id);
    void registerMember(RegisterReqDto member, String mode);
    void changePassword(String username, ChangePasswordReqDto request);
    PaginationResDto<MemberResDto> getAllMember(MemberSearchReqDto request);
}
