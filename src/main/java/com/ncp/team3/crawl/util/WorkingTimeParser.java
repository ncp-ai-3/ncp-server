package com.ncp.team3.crawl.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ncp.team3.crawl.infrastructure.dto.WorkingTimeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class WorkingTimeParser {
    private final ObjectMapper objectMapper;

    public WorkingTimeParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WorkingTime parse(String workingTime) {
        if (workingTime == null || workingTime.isBlank()) {
            return WorkingTime.empty();
        }

        try {
            List<WorkingTimeDto> times = objectMapper.readValue(workingTime, new TypeReference<>() {
            });

            Optional<WorkingTimeDto> firstValidTime = times.stream()
                    .filter(time -> time.startDate() != null && time.endDate() != null)
                    .findFirst();

            if (firstValidTime.isEmpty()) {
                return WorkingTime.empty();
            }

            LocalTime openTime = LocalTime.parse(firstValidTime.get().startDate());
            LocalTime closeTime = LocalTime.parse(firstValidTime.get().endDate());

            if (!openTime.isBefore(closeTime)) {
                return WorkingTime.empty();
            }

            return new WorkingTime(openTime, closeTime);
        } catch (Exception e) {
            log.debug("[WORKING TIME PARSE FAILED] {}", e.getMessage());
            return WorkingTime.empty();
        }
    }

    public record WorkingTime(
            LocalTime openTime,
            LocalTime closeTime
    ) {
        private static WorkingTime empty() {
            return new WorkingTime(null, null);
        }
    }
}
