package com.ncp.team3.chat.controller.dto.request;

public record FastApiChatRequestDto(
        Long userId,
        String question
) {
}
