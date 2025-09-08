package com.kms.springboard.member.entity;

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
                @Index(name = "idx_members_email", columnList = "email", unique = true)
        }
)
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

    @Column(name = "email" ,nullable = false, length = 255, unique = true)
    private String email;

    @PrePersist @PreUpdate
    private void normalize(){
        if(this.userId != null) this.userId = this.userId.trim().toLowerCase(Locale.ROOT);
        if(this.email != null) this.email = this.email.trim().toLowerCase(Locale.ROOT);
        if(this.username != null) this.username = this.username.trim();
    }


}
