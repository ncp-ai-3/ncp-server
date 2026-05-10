package com.ncp.team3.authentication.domain;

import com.ncp.team3.authentication.domain.enums.OAuthProvider;
import com.ncp.team3.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member_oauth", uniqueConstraints = {
        @UniqueConstraint(name = "uk_member_oauth_provider_provider_id", columnNames = {"oauth_provider", "provider_id"})
})
public class MemberOAuth extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Enumerated(EnumType.STRING)
    @Column(name = "oauth_provider", nullable = false, length = 20)
    private OAuthProvider provider;

    @Column(name = "provider_id", nullable = false, length = 512)
    private String providerId;

    @Builder(access = AccessLevel.PRIVATE)
    private MemberOAuth(Long memberId, OAuthProvider provider, String providerId) {
        this.memberId = memberId;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static MemberOAuth create(Long memberId, OAuthProvider provider, String providerId) {
        return MemberOAuth.builder()
                .memberId(memberId)
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
