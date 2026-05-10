package com.ncp.team3.member.domain;

import com.ncp.team3.common.BaseEntity;
import com.ncp.team3.common.Role;
import com.ncp.team3.member.domain.exception.MemberDomainException;
import com.ncp.team3.member.domain.exception.MemberErrorCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    @Column(name = "id")
    private Long id;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "name", length = 100)
    private String name;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role = Role.USER;

    @Builder(access = AccessLevel.PRIVATE)
    private Member(String email, String name, String imageUrl, Role role) {
        this.email = email;
        this.name = name;
        this.imageUrl = imageUrl;
        this.role = role == null ? Role.USER : role;
    }

    public static Member create(String email, String name, String imageUrl) {
        validateEmail(email);
        validateName(name);
        validateImageUrl(imageUrl);

        return Member.builder()
                .email(email)
                .name(name)
                .imageUrl(imageUrl)
                .role(Role.USER)
                .build();
    }

    public static Member createSocial(String email, String name, String imageUrl) {
        return create(email, name, imageUrl);
    }

    private static void validateEmail(String email) {
        if (email == null || email.isBlank() || email.length() > 255) {
            throw new MemberDomainException(MemberErrorCode.INVALID_MEMBER_EMAIL);
        }
    }

    private static void validateName(String name) {
        if (name != null && (name.isBlank() || name.length() > 100)) {
            throw new MemberDomainException(MemberErrorCode.INVALID_MEMBER_NAME);
        }
    }

    private static void validateImageUrl(String imageUrl) {
        if (imageUrl != null && (imageUrl.isBlank() || imageUrl.length() > 500)) {
            throw new MemberDomainException(MemberErrorCode.INVALID_MEMBER_IMAGE_URL);
        }
    }
}
