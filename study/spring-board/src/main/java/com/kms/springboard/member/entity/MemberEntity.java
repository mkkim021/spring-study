package com.kms.springboard.member.entity;

import com.kms.springboard.member.dto.MemberDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Locale;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(
        name="members",
        indexes = {
                @Index(name = "uk_members_userId", columnList = "userId",unique = true),
                @Index(name = "uk_members_email", columnList = "email", unique = true)
        }
)
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long id;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "userId", nullable = false, length = 50)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email" ,nullable = false, length = 255)
    private String email;






}
