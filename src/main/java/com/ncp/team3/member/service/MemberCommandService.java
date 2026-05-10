package com.ncp.team3.member.service;

import com.ncp.team3.member.controller.dto.request.CreateMemberRequest;
import com.ncp.team3.member.controller.dto.response.CreateMemberResponse;
import com.ncp.team3.member.domain.Member;
import com.ncp.team3.member.port.MemberRepository;
import com.ncp.team3.member.usecase.command.CreateMemberUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberCommandService implements CreateMemberUseCase {
    private final MemberRepository memberRepository;

    @Override
    public CreateMemberResponse createMember(CreateMemberRequest request) {
        Member member = Member.create(request.email(), request.name(), request.imageUrl());
        Member savedMember = memberRepository.save(member);

        return CreateMemberResponse.from(savedMember);
    }
}
