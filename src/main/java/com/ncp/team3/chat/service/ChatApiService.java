package com.ncp.team3.chat.service;

import com.ncp.team3.chat.controller.dto.request.ChatRequestDto;
import com.ncp.team3.chat.controller.dto.request.FastApiChatRequestDto;
import com.ncp.team3.chat.controller.dto.response.ChatResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;

@Slf4j
@Service
public class ChatApiService {
    private final RestTemplate restTemplate;
    private final String fastApiUrl;

    public ChatApiService(RestTemplateBuilder restTemplateBuilder,
                          @Value("${ai.fastapi.url}") String fastApiUrl) {
        this.restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(60))
                .build();
        this.fastApiUrl = fastApiUrl;
    }

    public ChatResponseDto getAnswerFromAi(Long memberId, ChatRequestDto requestDto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        FastApiChatRequestDto fastApiRequest = new FastApiChatRequestDto(memberId, requestDto.question());
        HttpEntity<FastApiChatRequestDto> requestEntity = new HttpEntity<>(fastApiRequest, headers);

        try {
            ChatResponseDto response = restTemplate.postForObject(
                    fastApiUrl,
                    requestEntity,
                    ChatResponseDto.class
            );

            return response != null
                    ? response
                    : new ChatResponseDto("AI 서버 응답 오류", List.of());
        } catch (RestClientException e) {
            log.warn("[CHAT API ERROR] url={}, message={}", fastApiUrl, e.getMessage());
            return new ChatResponseDto("AI 서버 응답 오류", List.of());
        }
    }
}
