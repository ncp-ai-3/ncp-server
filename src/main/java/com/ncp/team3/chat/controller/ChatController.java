package com.ncp.team3.chat.controller;

import com.ncp.team3.chat.controller.dto.request.ChatRequestDto;
import com.ncp.team3.chat.controller.dto.response.ClientResponseDto;
import com.ncp.team3.chat.usecase.query.AskChatUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/chats")
@RequiredArgsConstructor
@Tag(name = "채팅")
public class ChatController {
    private final AskChatUseCase askChatUseCase;

    @PostMapping
    public ClientResponseDto askToAi(@Valid @RequestBody ChatRequestDto request) {
        return askChatUseCase.askToAi(request);
    }
}
