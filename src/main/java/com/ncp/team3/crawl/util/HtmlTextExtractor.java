package com.ncp.team3.crawl.util;

import org.springframework.web.util.HtmlUtils;

public final class HtmlTextExtractor {
    private HtmlTextExtractor() {
    }

    public static String toPlainText(String html) {
        if (html == null || html.isBlank()) {
            return null;
        }

        String text = html
                .replaceAll("(?i)<br\\s*/?>", "\n")
                .replaceAll("(?i)</p>", "\n")
                .replaceAll("(?i)</div>", "\n")
                .replaceAll("<[^>]+>", "");

        text = HtmlUtils.htmlUnescape(text)
                .replace('\u00A0', ' ')
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ")
                .replaceAll("\\n\\s*\\n+", "\n")
                .trim();

        return text.isBlank() ? null : text;
    }
}
