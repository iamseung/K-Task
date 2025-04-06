package com.example.task.writer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class FileOutputResultWriter implements ResultWriter {

    private static final Logger log = LoggerFactory.getLogger(FileOutputResultWriter.class);

    @Override
    public void writeResults(Map<String, Long> results, String destinationIdentifier) throws IOException {
        if (results == null || results.isEmpty()) {
            log.warn("결과 데이터가 비어있어 '{}' 파일에 쓸 내용이 없습니다. 빈 파일을 생성합니다.", destinationIdentifier);
            try {
                Files.write(Paths.get(destinationIdentifier), new byte[0]);
            } catch (IOException e) {
                throw e;
            }
            return;
        }

        List<String> lines = sortResults(results);

        try {
            Files.write(Paths.get(destinationIdentifier), lines, StandardCharsets.UTF_8);
            log.info("{} 라인을 '{}' 파일에 성공적으로 작성했습니다.", lines.size(), destinationIdentifier);
        } catch (IOException e) {
            log.error("'{}' 파일 작성 중 오류 발생", destinationIdentifier, e);
            throw e;
        }
    }

    // 형식: 학교 이름<TAB>카운트, 학교 이름 가나다순 정렬
    private List<String> sortResults(Map<String, Long> results) {
        return results.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> entry.getKey() + "\t" + entry.getValue())
                .collect(Collectors.toList());
    }
}