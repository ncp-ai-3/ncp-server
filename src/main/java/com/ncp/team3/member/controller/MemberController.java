package com.ncp.team3.member.controller;

import com.ncp.team3.member.controller.dto.request.CreateMemberRequest;
import com.ncp.team3.member.controller.dto.response.CreateMemberResponse;
import com.ncp.team3.member.usecase.command.CreateMemberUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor
@Tag(name = "유저")
public class MemberController {
    private final CreateMemberUseCase createMemberUseCase;

    @PostMapping
    public CreateMemberResponse createMember(@Valid @RequestBody CreateMemberRequest request) {
        return createMemberUseCase.createMember(request);
    }
}
