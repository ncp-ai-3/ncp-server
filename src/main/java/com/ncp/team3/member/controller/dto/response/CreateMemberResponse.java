package com.ncp.team3.member.controller.dto.response;

import com.ncp.team3.member.domain.Member;

public record CreateMemberResponse(
        Long id,
        String email
) {
    public static CreateMemberResponse from(Member member) {
        return new CreateMemberResponse(member.getId(), member.getEmail());
    }
}
