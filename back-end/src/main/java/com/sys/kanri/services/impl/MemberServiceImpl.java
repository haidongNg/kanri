package com.sys.kanri.services.impl;

import com.sys.kanri.dto.request.ChangePasswordReqDto;
import com.sys.kanri.dto.request.MemberSearchReqDto;
import com.sys.kanri.dto.request.RegisterReqDto;
import com.sys.kanri.dto.response.MemberResDto;
import com.sys.kanri.dto.response.PaginationResDto;
import com.sys.kanri.entities.Member;
import com.sys.kanri.entities.Role;
import com.sys.kanri.enums.RoleType;
import com.sys.kanri.exceptions.ApiException;
import com.sys.kanri.repositories.MemberRepository;
import com.sys.kanri.repositories.RoleRepository;
import com.sys.kanri.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.sys.kanri.enums.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    /**
     * @param username
     * @return
     */
    @Override
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    /**
     * @param id
     * @return
     */
    @Override
    public MemberResDto getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        USERNAME_EXISTS.message,
                        USERNAME_EXISTS.code,
                        USERNAME_EXISTS.status
                ));
        return convertToDto(member);
    }

    /**
     * @param id
     */
    @Override
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    @Override
    public void registerMember(RegisterReqDto request, String mode) {
        boolean memberFound = memberRepository.existsByUsername(request.getUsername());
        if (memberFound) {
            throw new ApiException(USERNAME_EXISTS.message, USERNAME_EXISTS.code, USERNAME_EXISTS.status);
        }

        boolean emailFound = memberRepository.existsByEmail(request.getEmail());
        if (emailFound) {
            throw new ApiException(EMAIL_EXISTS.message, EMAIL_EXISTS.code, EMAIL_EXISTS.status);
        }

        // 2. Tạo member mới
        Member newMember = new Member();
        newMember.setUsername(request.getUsername());
        newMember.setPassword(passwordEncoder.encode(request.getPassword())); // mã hóa luôn
        newMember.setFullName(request.getFullName());
        newMember.setEmail(request.getEmail());
        newMember.setPhone(request.getPhone());
        newMember.setAddress(request.getAddress());
        newMember.setImageUrl(request.getImageUrl());
        newMember.setGender(request.getGender());

        // 3. Gán role nếu có mode
        RoleType type = RoleType.SUPPORT.name().equalsIgnoreCase(mode) ? RoleType.SUPPORT : RoleType.CUSTOMER;
        Role role = roleRepository.findByName(type)
                .orElseThrow(() -> new ApiException(ROLE_NOT_FOUND.message, ROLE_NOT_FOUND.code, ROLE_NOT_FOUND.status));

        newMember.setRole(role);
        // 4. Save
        memberRepository.save(newMember);
    }

    /**
     * Hàm đổi mật khẩu cho người dùng.
     *
     * @param username Tên người dùng.
     * @param request  DTO chứa mật khẩu cũ và mới.
     * @throws ApiException nếu mật khẩu cũ không đúng hoặc người dùng không tồn tại.
     */
    @Override
    public void changePassword(String username, ChangePasswordReqDto request) {

        try {
            // Tìm người dùng trong database
            Optional<Member> member = memberRepository.findByUsername(username);
            if (member.isEmpty()) {
                throw new ApiException(USERNAME_EXISTS.message, USERNAME_EXISTS.code, USERNAME_EXISTS.status);
            }

            if (!passwordEncoder.matches(request.getOldPassword(), member.get().getPassword())) {
                throw new ApiException(OLD_PASSWORD_MISMATCH.message, OLD_PASSWORD_MISMATCH.code, OLD_PASSWORD_MISMATCH.status);
            }

            if (passwordEncoder.matches(request.getNewPassword(), member.get().getPassword())) {
                throw new ApiException(NEW_PASSWORD_SAME_AS_OLD.message, NEW_PASSWORD_SAME_AS_OLD.code, NEW_PASSWORD_SAME_AS_OLD.status);
            }
            // Mã hóa mật khẩu mới và cập nhật
            String newHashedPassword = passwordEncoder.encode(request.getNewPassword());
            member.get().setPassword(newHashedPassword);
            // Lưu thay đổi vào database
            memberRepository.save(member.get());
        } catch (IllegalArgumentException e) {
            throw new ApiException(PASSWORD_CHANGE_FAILED.message, PASSWORD_CHANGE_FAILED.code, PASSWORD_CHANGE_FAILED.status);
        }
    }

    /**
     * @param request
     * @return
     */
    @Override
    public PaginationResDto<MemberResDto> getAllMember(MemberSearchReqDto request) {
        // Tạo đối tượng Pageable từ PaginationRequest
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Member> memberPage;
        // Kiểm tra xem có từ khóa tìm kiếm không
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            String keyword = request.getKeyword().trim();
            memberPage = memberRepository.findByUsernameOrEmail(keyword, keyword, pageable);
        } else {
            // Nếu không có từ khóa, trả về tất cả
            memberPage = memberRepository.findAll(pageable);
        }

        // Chuyển đổi danh sách đối tượng Member (Entity) sang danh sách MemberResDto (DTO).
        List<MemberResDto> memberResDtoList = memberPage.getContent().stream()
                .map(this::convertToDto) // Sử dụng stream để ánh xạ từng đối tượng.
                .collect(Collectors.toList());

        // Chuyển đổi Page thành PaginationResponse
        return new PaginationResDto<MemberResDto>(
                memberResDtoList, // Dữ liệu của trang hiện tại
                memberPage.getNumber(),// Số trang hiện tại (bắt đầu từ 0)
                memberPage.getSize(),// Kích thước trang
                memberPage.getTotalElements(),// Tổng số phần tử
                memberPage.getTotalPages(),// Tổng số trang
                memberPage.isLast(),// Có phải trang cuối không?
                memberPage.isFirst(),// Có phải trang đầu không?
                memberPage.hasNext(),// Có trang tiếp theo không?
                memberPage.hasPrevious() // Có trang trước không?
        );
    }

    /**
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    /**
     * Phương thức helper để chuyển đổi một đối tượng Member sang MemberResDto.
     * Việc tách riêng logic chuyển đổi giúp code rõ ràng và dễ bảo trì.
     *
     * @param member Đối tượng Member từ cơ sở dữ liệu.
     * @return MemberResDto tương ứng.
     */
    private MemberResDto convertToDto(Member member) {
        MemberResDto dto = new MemberResDto();
        dto.setId(member.getId());
        dto.setUsername(member.getUsername());
        dto.setEmail(member.getEmail());
        dto.setAddress(member.getAddress());
        dto.setFullName(member.getFullName());
        dto.setImageUrl(member.getImageUrl());
        return dto;
    }
}