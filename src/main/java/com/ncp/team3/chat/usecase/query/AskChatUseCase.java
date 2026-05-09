package com.ncp.team3.chat.usecase.query;

import com.ncp.team3.chat.controller.dto.request.ChatRequestDto;
import com.ncp.team3.chat.controller.dto.response.ClientResponseDto;

public interface AskChatUseCase {
    ClientResponseDto askToAi(ChatRequestDto request);
}
