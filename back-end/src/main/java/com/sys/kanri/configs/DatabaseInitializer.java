package com.sys.kanri.configs;

import com.sys.kanri.entities.Member;
import com.sys.kanri.entities.Role;
import com.sys.kanri.enums.RoleType;
import com.sys.kanri.repositories.MemberRepository;
import com.sys.kanri.repositories.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DatabaseInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String @NonNull ... args) throws Exception {
        // 1. Khởi tạo Role ADMIN nếu chưa có
        Role adminRole = roleRepository.findByName(RoleType.ADMIN)
                .orElseGet(() -> {
                    Role newRole = Role.builder()
                            .name(RoleType.ADMIN)
                            .build();
                    return roleRepository.save(newRole);
                });

        // 2. Khởi tạo Role CUSTOMER và SUPPORT (Optional - để đầy đủ database)
        if (roleRepository.findByName(RoleType.CUSTOMER).isEmpty()) {
            roleRepository.save(Role.builder().name(RoleType.CUSTOMER).build());
        }
        if (roleRepository.findByName(RoleType.SUPPORT).isEmpty()) {
            roleRepository.save(Role.builder().name(RoleType.SUPPORT).build());
        }

        // 3. Khởi tạo tài khoản Admin mặc định nếu chưa có
        String adminUsername = "admin";
        if (!memberRepository.existsByUsername(adminUsername)) {
            Member admin = Member.builder()
                    .username(adminUsername)
                    .password(passwordEncoder.encode("admin@123456")) // Mật khẩu mặc định
                    .fullName("System Administrator")
                    .email("admin@kanri.sys")
                    .phone("0000000000") // Validate regex yêu cầu số
                    .address("System HQ")
                    .gender("Other")
                    .role(adminRole)
                    .build();

            memberRepository.save(admin);
            System.out.println(">>> Đã tạo tài khoản Admin mặc định: admin / admin123");
        }
    }
}