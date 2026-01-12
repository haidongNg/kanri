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
import com.sys.kanri.mapper.MemberMapper;
import com.sys.kanri.repositories.MemberRepository;
import com.sys.kanri.repositories.RoleRepository;
import com.sys.kanri.services.MemberService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final MemberMapper memberMapper;

    /**
     * Finds a member by their username.
     *
     * @param username the username of the member to be retrieved
     * @return an {@link Optional} containing the {@link Member} if found, or an empty {@link Optional} if no member with the given username exists
     */
    @Override
    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    /**
     * Retrieves a member by their unique identifier.
     *
     * @param id the unique identifier of the member to be retrieved
     * @return a MemberResDto object containing the details of the member
     * @throws ApiException if the member with the specified id is not found
     */
    @Override
    public MemberResDto getMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        USERNAME_EXISTS.message,
                        USERNAME_EXISTS.code,
                        USERNAME_EXISTS.status
                ));
        return memberMapper.toDto(member);
    }

    /**
     * Deletes a member entity identified by the given ID.
     * This method removes the entity with the specified ID from the repository.
     * If no entity with the given ID exists, the method will not throw an exception.
     *
     * @param id the unique identifier of the entity to be deleted
     */
    @Override
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    /**
     * Registers a new member in the system. This method performs checks to ensure
     * the uniqueness of the username and email, assigns a role based on the provided
     * mode, and saves the new member to the database.
     *
     * @param request an object containing the registration details of the member
     *                such as username, password, full name, email, phone, address,
     *                image URL, and gender.
     * @param mode a string that determines the role to be assigned to the member.
     *             If the mode matches "SUPPORT", the SUPPORT role will be assigned;
     *             otherwise, the CUSTOMER role will be assigned.
     * @throws ApiException if the provided username or email already exists in the system,
     *                      or if the specified role is not found in the database.
     */
    @Override
    @Transactional
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
        Member newMember = Member.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .imageUrl(request.getImageUrl())
                .gender(request.getGender())
                .build();

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
    @Transactional
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
     * Retrieves a paginated list of members based on the given search criteria.
     *
     * @param request the object containing search criteria such as page number, page size,
     *                and optional keyword for filtering members.
     * @return a {@code PaginationResDto<MemberResDto>} containing the current page's data,
     *         pagination details (like total elements, total pages, etc.), and flags
     *         indicating the position within the pagination (e.g., if this is the first
     *         or last page).
     */
    @Override
    public PaginationResDto<MemberResDto> getAllMember(MemberSearchReqDto request) {
        // Tạo đối tượng Pageable từ PaginationRequest
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Member> memberPage;
        // Kiểm tra xem có từ khóa tìm kiếm không
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            String keyword = request.getKeyword().trim();
            memberPage = memberRepository.searchByKeyword(keyword, keyword, pageable);
        } else {
            // Nếu không có từ khóa, trả về tất cả
            memberPage = memberRepository.findAll(pageable);
        }

        // Chuyển đổi danh sách đối tượng Member (Entity) sang danh sách MemberResDto (DTO).
        List<MemberResDto> memberResDtoList = memberPage.getContent().stream()
                .map(memberMapper::toDto) // Sử dụng stream để ánh xạ từng đối tượng.
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
     * Loads the user details associated with the provided username.
     *
     * @param username the username of the user whose details are to be retrieved
     * @return the UserDetails object containing the user's information
     * @throws UsernameNotFoundException if no user is found with the specified username
     */
    @Override
    @NonNull
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }
}