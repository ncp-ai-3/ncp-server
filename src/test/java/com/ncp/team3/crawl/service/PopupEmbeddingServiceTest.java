package com.ncp.team3.crawl.service;

import com.ncp.team3.crawl.infrastructure.EmbeddingClient;
import com.ncp.team3.crawl.infrastructure.PopupEmbeddingJdbcRepository;
import com.ncp.team3.popup.domain.Popup;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.Environment;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PopupEmbeddingServiceTest {
    private final EmbeddingClient embeddingClient = mock(EmbeddingClient.class);
    private final PopupEmbeddingJdbcRepository popupEmbeddingJdbcRepository = mock(PopupEmbeddingJdbcRepository.class);
    private final Environment environment = mock(Environment.class);
    private final PopupEmbeddingService popupEmbeddingService;

    PopupEmbeddingServiceTest() {
        when(environment.getProperty(eq("rag.embedding.dimension"), eq(Integer.class), eq(768))).thenReturn(3);
        this.popupEmbeddingService = new PopupEmbeddingService(
                embeddingClient,
                popupEmbeddingJdbcRepository,
                environment
        );
    }

    @Test
    void 임베딩_content를_정해진_형식으로_생성한다() {
        Popup popup = popup();

        String content = popupEmbeddingService.buildEmbeddingContent(popup, List.of("뷰티/헬스"));

        assertThat(content).isEqualTo("""
                제목: 성수 뷰티 팝업
                브랜드: 브랜드
                설명: 신제품 체험
                해시태그: 성수, 뷰티
                카테고리: 뷰티, 헬스
                주소: 서울 성동구 성수동
                상태: scheduled
                """.stripTrailing());
    }

    @Test
    void description이_null이어도_content를_생성한다() {
        Popup popup = Popup.create(
                null,
                "성수 팝업",
                null,
                null,
                null,
                "서울 성동구",
                37.1,
                127.1,
                2L,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                null,
                null,
                null,
                null,
                "hash"
        );

        String content = popupEmbeddingService.buildEmbeddingContent(popup, List.of());

        assertThat(content).contains("설명: ");
        assertThat(content).contains("카테고리: 기타");
    }

    @Test
    void vector_dimension이_다르면_저장하지_않는다() {
        Popup popup = popup();
        when(embeddingClient.embed(any())).thenReturn(List.of(0.1, 0.2));

        boolean result = popupEmbeddingService.createOrUpdateEmbedding(popup, List.of("뷰티/헬스"));

        assertThat(result).isFalse();
        verify(popupEmbeddingJdbcRepository, never()).upsertEmbedding(any(), any(), any());
    }

    @Test
    void vector_dimension이_맞으면_저장한다() {
        Popup popup = popup();
        List<Double> vector = List.of(0.1, 0.2, 0.3);
        when(embeddingClient.embed(any())).thenReturn(vector);

        boolean result = popupEmbeddingService.createOrUpdateEmbedding(popup, List.of("뷰티/헬스"));

        assertThat(result).isTrue();
        verify(popupEmbeddingJdbcRepository).upsertEmbedding(eq(popup.getId()), any(), eq(vector));
    }

    private Popup popup() {
        return Popup.create(
                null,
                "성수 뷰티 팝업",
                "브랜드",
                "성수,뷰티",
                "신제품 체험",
                "서울 성동구 성수동",
                37.1,
                127.1,
                1L,
                LocalDate.now(),
                LocalDate.now().plusDays(1),
                null,
                null,
                null,
                "scheduled",
                "hash"
        );
    }
}
