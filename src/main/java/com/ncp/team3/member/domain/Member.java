package com.ncp.team3.member.domain;

import com.ncp.team3.common.BaseEntity;
import com.ncp.team3.member.domain.exception.MemberDomainException;
import com.ncp.team3.member.domain.exception.MemberErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "password", length = 255)
    private String password;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static Member create(String email, String password) {
        validateEmail(email);
        validatePassword(password);

        return Member.builder()
                .email(email)
                .password(password)
                .build();
    }

    public void updatePassword(String password) {
        validatePassword(password);

        this.password = password;
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank() || email.length() > 255) {
            throw new MemberDomainException(MemberErrorCode.INVALID_MEMBER_EMAIL);
        }
    }

    private static void validatePassword(String password) {
        if (password == null || password.isBlank() || password.length() > 255) {
            throw new MemberDomainException(MemberErrorCode.INVALID_MEMBER_PASSWORD);
        }
    }
}
