package com.example.task.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SchoolNameResolveService {
    private static final String SUFFIX_KEY_FORMAT = "school:reverse_index:%s";
    private final RedisTemplate<String, String> redisTemplate;
    private static final int MIN_SUFFIX_LENGTH = 5; // 최소 유효한 서브스트링 길이

    public Optional<String> resolveToFullName(String parsed) {
        for (int i = 0; i <= parsed.length() - MIN_SUFFIX_LENGTH; i++) {
            String suffix = parsed.substring(i);
            String redisKey = String.format(SUFFIX_KEY_FORMAT, suffix);
            Set<String> candidates = redisTemplate.opsForSet().members(redisKey);

            if (candidates != null && candidates.size() == 1) {
                return Optional.of(candidates.iterator().next());
            }
        }

        return Optional.empty();
    }
}
