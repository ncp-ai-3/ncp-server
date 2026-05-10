package com.ncp.team3.crawl.infrastructure;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RagEmbeddingClientTest {

    @Test
    void embed_성공시_벡터를_반환한다() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();

        RagEmbeddingProperties props = new RagEmbeddingProperties();
        props.setBaseUrl("http://localhost:9999");
        props.setPath("/api/v1/embed");

        RagEmbeddingClient sut = new RagEmbeddingClient(restTemplate, props);

        server.expect(MockRestRequestMatchers.requestTo("http://localhost:9999/api/v1/embed"))
                .andExpect(MockRestRequestMatchers.method(HttpMethod.POST))
                .andExpect(MockRestRequestMatchers.content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andRespond(MockRestResponseCreators.withSuccess(
                        "{\"embedding\":[0.5,-0.25]}",
                        MediaType.APPLICATION_JSON
                ));

        List<Double> result = sut.embed("hello");

        assertThat(result).containsExactly(0.5, -0.25);
        server.verify();
    }

    @Test
    void baseUrl이_비어있으면_예외() {
        RagEmbeddingProperties props = new RagEmbeddingProperties();
        props.setBaseUrl("  ");

        assertThatThrownBy(() -> new RagEmbeddingClient(new RestTemplate(), props))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
