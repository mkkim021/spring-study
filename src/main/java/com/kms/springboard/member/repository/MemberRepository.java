package com.kms.springboard.member.repository;

import com.kms.springboard.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, Long> {
    Optional<MemberEntity> findByUserId(String userId);
    Optional<MemberEntity> findByEmail(String email);
    boolean existsByEmail(String email);
    boolean existsByUserId(String userId);

}
