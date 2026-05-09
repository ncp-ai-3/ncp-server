package com.ncp.team3.member.usecase.command;

import com.ncp.team3.member.controller.dto.request.CreateMemberRequest;
import com.ncp.team3.member.controller.dto.response.CreateMemberResponse;

public interface CreateMemberUseCase {
    CreateMemberResponse createMember(CreateMemberRequest request);
}
