package com.ncp.team3.crawl.infrastructure;

import java.util.List;

public interface EmbeddingClient {
    List<Double> embed(String content);
}
