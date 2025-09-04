package com.kms.springboard.member.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "members")
public class MemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long memberId;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "userId", nullable = false, unique = true,length = 50)
    private String userId;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "email" ,nullable = false, length = 255)
    private String email;



}
