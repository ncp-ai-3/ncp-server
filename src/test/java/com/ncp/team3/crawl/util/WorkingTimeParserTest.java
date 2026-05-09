package com.ncp.team3.crawl.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

class WorkingTimeParserTest {
    private final WorkingTimeParser workingTimeParser = new WorkingTimeParser(new ObjectMapper());

    @Test
    void workingTimeJson이면_오픈시간과_마감시간을_파싱한다() {
        WorkingTimeParser.WorkingTime result = workingTimeParser.parse("""
                [{"startDate":"10:00","endDate":"20:00"}]
                """);

        assertThat(result.openTime()).isEqualTo(LocalTime.of(10, 0));
        assertThat(result.closeTime()).isEqualTo(LocalTime.of(20, 0));
    }

    @Test
    void workingTimeJson이_깨져있으면_null로_처리한다() {
        WorkingTimeParser.WorkingTime result = workingTimeParser.parse("not-json");

        assertThat(result.openTime()).isNull();
        assertThat(result.closeTime()).isNull();
    }
}
