package com.ncp.team3.chat.service;

import com.ncp.team3.chat.controller.dto.request.ChatRequestDto;
import com.ncp.team3.chat.controller.dto.response.ChatResponseDto;
import com.ncp.team3.chat.controller.dto.response.ClientResponseDto;
import com.ncp.team3.chat.usecase.query.AskChatUseCase;
import com.ncp.team3.popup.controller.dto.response.GetPopupDetailResponse;
import com.ncp.team3.popup.domain.Popup;
import com.ncp.team3.popup.port.PopupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatQueryService implements AskChatUseCase {
    private final ChatApiService chatApiService;
    private final PopupRepository popupRepository;

    @Override
    public ClientResponseDto askToAi(Long memberId, ChatRequestDto request) {
        ChatResponseDto aiResult = chatApiService.getAnswerFromAi(memberId, request);
        List<Long> popupIds = aiResult.popupIds() == null ? List.of() : aiResult.popupIds();

        if (popupIds.isEmpty()) {
            return new ClientResponseDto(aiResult.answer(), List.of());
        }

        List<Popup> popups = popupRepository.findDetailsByIdIn(popupIds);
        Map<Long, Popup> popupMap = popups.stream()
                .collect(Collectors.toMap(Popup::getId, Function.identity()));

        List<GetPopupDetailResponse> recommendedPopups = popupIds.stream()
                .distinct()
                .map(popupMap::get)
                .filter(popup -> popup != null)
                .map(GetPopupDetailResponse::from)
                .toList();

        return new ClientResponseDto(aiResult.answer(), recommendedPopups);
    }
}
