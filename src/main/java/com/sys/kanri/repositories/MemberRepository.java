package com.sys.kanri.repositories;

import com.sys.kanri.entities.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String username);
    Page<Member> findByUsernameOrEmail(String username, String email, Pageable pageable);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
