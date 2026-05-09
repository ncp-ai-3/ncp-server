package com.ncp.team3.crawl.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;

public final class ContentHashGenerator {
    private ContentHashGenerator() {
    }

    public static String generate(String title, String description, List<String> categoryNames, String address) {
        return generate(title, null, null, description, categoryNames, address, null);
    }

    public static String generate(String title, String mainBrand, String hashtags, String description,
                                  List<String> categoryNames, String address, String status) {
        String source = String.join("|",
                normalize(title),
                normalize(mainBrand),
                normalize(hashtags),
                normalize(description),
                normalizeCategories(categoryNames),
                normalize(address),
                normalize(status)
        );

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(source.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 알고리즘을 사용할 수 없습니다.", e);
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static String normalizeCategories(List<String> categoryNames) {
        if (categoryNames == null || categoryNames.isEmpty()) {
            return "";
        }

        return categoryNames.stream()
                .filter(categoryName -> categoryName != null && !categoryName.isBlank())
                .map(String::trim)
                .sorted(Comparator.naturalOrder())
                .reduce((left, right) -> left + "," + right)
                .orElse("");
    }
}
