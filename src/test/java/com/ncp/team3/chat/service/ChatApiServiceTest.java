package com.ncp.team3.chat.service;

import com.ncp.team3.chat.controller.dto.request.ChatRequestDto;
import com.ncp.team3.chat.controller.dto.response.ChatResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatApiServiceTest {
    private final RestTemplateBuilder restTemplateBuilder = mock(RestTemplateBuilder.class);
    private final RestTemplate restTemplate = mock(RestTemplate.class);

    @Test
    void fastapi에_요청을_전달하고_응답을_반환한다() {
        ChatApiService chatApiService = chatApiService();
        ChatResponseDto response = new ChatResponseDto("답변", java.util.List.of(1L));
        when(restTemplate.postForObject(eq("http://localhost:8000/chat"), any(), eq(ChatResponseDto.class)))
                .thenReturn(response);

        ChatResponseDto result = chatApiService.getAnswerFromAi(new ChatRequestDto(1L, "질문"));

        assertThat(result).isEqualTo(response);
    }

    @Test
    void fastapi_응답이_null이면_fallback을_반환한다() {
        ChatApiService chatApiService = chatApiService();
        when(restTemplate.postForObject(eq("http://localhost:8000/chat"), any(), eq(ChatResponseDto.class)))
                .thenReturn(null);

        ChatResponseDto result = chatApiService.getAnswerFromAi(new ChatRequestDto(1L, "질문"));

        assertThat(result.answer()).isEqualTo("AI 서버 응답 오류");
        assertThat(result.popupIds()).isEmpty();
    }

    @Test
    void restClientException이_발생하면_fallback을_반환한다() {
        ChatApiService chatApiService = chatApiService();
        when(restTemplate.postForObject(eq("http://localhost:8000/chat"), any(), eq(ChatResponseDto.class)))
                .thenThrow(new RestClientException("fail"));

        ChatResponseDto result = chatApiService.getAnswerFromAi(new ChatRequestDto(1L, "질문"));

        assertThat(result.answer()).isEqualTo("AI 서버 응답 오류");
        assertThat(result.popupIds()).isEmpty();
    }

    private ChatApiService chatApiService() {
        when(restTemplateBuilder.setConnectTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.setReadTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);

        return new ChatApiService(restTemplateBuilder, "http://localhost:8000/chat");
    }
}
