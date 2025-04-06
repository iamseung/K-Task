package com.example.task.parser;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RegexSchoolNameParser implements SchoolNameParser {

    private static final Pattern SCHOOL_NAME_PATTERN = Pattern.compile(
            "[가-힣]{2,}(여자|남자)?(초등학교|중학교|고등학교|대학교|여중|여고|초|중|고|대)"
    );

    private static final Pattern SCHOOL_NAME_REATABLE_PATTERN = Pattern.compile(
            "[가-힣]{2,}?(여자|남자)?(초등학교|중학교|고등학교|대학교|여중|여고|초|중|고|대)"
    );

    private static final Map<String, String> SUFFIX_NORMALIZER = createNormalizer();

    private static Map<String, String> createNormalizer() {
        Map<String, String> map = new LinkedHashMap<>();
        map.put("여고", "여자고등학교");
        map.put("여중", "여자중학교");
        map.put("남고", "남자고등학교");
        map.put("남중", "남자중학교");
        map.put("예고", "예술고등학교");
        map.put("예중", "예술중학교");
        map.put("초", "초등학교");
        map.put("중", "중학교");
        map.put("고", "고등학교");
        map.put("대", "대학교");
        return map;
    }

    private static final int MAX_SCHOOL_NAME = 20;
    private static final int MIN_SCHOOL_NAME = 5;

    public List<String> parseLine(String line) {
        List<String> schoolNames = new ArrayList<>();

        if (line != null) {
            line = lineFilter(line);

            String[] tokens = line.split("\\s+");

            for(String token : tokens) {
                Matcher matcher = SCHOOL_NAME_PATTERN.matcher(token);

                while (matcher.find()) {
                    String matched = matcher.group().replaceAll("\\s+", "");
                    String normalized = normalizeSchoolName(matched);

                    if (stringValidation(normalized)) {
                        schoolNames.add(normalized);
                    } else if (normalized.length() > MAX_SCHOOL_NAME) {
                        List<String> split = parseLongNormalizedString(normalized);
                        if (!split.isEmpty()) {
                            schoolNames.addAll(split);
                        }
                    }
                }
            }
        }

        return schoolNames;
    }

    private boolean stringValidation(String token) {
        int len = token.length();
        return len > MIN_SCHOOL_NAME && len < MAX_SCHOOL_NAME;
    }

    // 반복되는 텍스트에 대한 검증
    private List<String> parseLongNormalizedString(String text) {
        List<String> results = new ArrayList<>();
        int cursor = 0;

        while (cursor < text.length()) {
            Matcher matcher = SCHOOL_NAME_REATABLE_PATTERN.matcher(text.substring(cursor));

            if (matcher.find() && matcher.start() == 0) {
                String matched = matcher.group();
                String normalized = normalizeSchoolName(matched);

                if (stringValidation(normalized)) {
                    results.add(normalized);
                }

                cursor += matcher.end();
            } else {
                cursor++;
            }
        }

        return results;
    }

    // 특수 문자 및 자모 문자 제거
    private String lineFilter(String line) {
        line = line.replaceAll("[^가-힣0-9\\s]", " ");
        return line.replaceAll("[ㄱ-ㅎㅏ-ㅣ]", "");
    }

    private String normalizeSchoolName(String raw) {
        for (String suffix : SUFFIX_NORMALIZER.keySet()) {
            if (raw.endsWith(suffix)) {
                String base = raw.substring(0, raw.length() - suffix.length());
                return base + SUFFIX_NORMALIZER.get(suffix);
            }
        }

        return raw;
    }
}