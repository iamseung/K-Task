package com.example.task.processor;

import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SchoolCacheProcessor implements CsvProcessor {

    private final RedisTemplate<String, String> redisTemplate;
    private static final String SUFFIX_KEY_FORMAT = "school:reverse_index:%s";
    private static final String SCHOOL_RECORD_NAME = "학교명";
    private static final String NOT_INCLUDE_TEXT = "대학원";
    private static final int MIN_SUFFIX_LENGTH = 5; // 최소 유효한 서브스트링 길이

    @Override
    public void process(List<CSVRecord> batch) {
        for (CSVRecord record : batch) {
            String fullName = record.get(SCHOOL_RECORD_NAME).trim();

            if (fullName.endsWith(NOT_INCLUDE_TEXT)) {
                continue;
            }

            // 역 인덱스 캐싱, 접미사 기준 (Set 사용)
            for (int i = 0; i <= fullName.length() - MIN_SUFFIX_LENGTH; i++) {
                String suffix = fullName.substring(i);
                String suffixKey = String.format(SUFFIX_KEY_FORMAT, suffix);

                redisTemplate.opsForSet().add(suffixKey, fullName);
            }
        }
    }
}
