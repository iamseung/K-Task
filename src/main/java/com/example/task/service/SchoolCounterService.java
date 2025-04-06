package com.example.task.service;

import com.example.task.parser.SchoolNameParser;
import com.example.task.reader.LineReader;
import com.example.task.writer.ResultWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SchoolCounterService {

    private static final Logger log = LoggerFactory.getLogger(SchoolCounterService.class);

    private final LineReader lineReader;
    private final SchoolNameParser schoolNameParser;
    private final ResultWriter resultWriter;
    private final SchoolNameResolveService schoolNameResolveService;

    @Autowired
    public SchoolCounterService(LineReader lineReader,
                                SchoolNameParser schoolNameParser,
                                ResultWriter resultWriter,
                                SchoolNameResolveService schoolNameResolveService) {
        this.lineReader = lineReader;
        this.schoolNameParser = schoolNameParser;
        this.resultWriter = resultWriter;
        this.schoolNameResolveService = schoolNameResolveService;
    }

    public void processSchoolCounts(String inputSource, String outputDestination) {
        Map<String, Long> schoolCounts = new ConcurrentHashMap<>();

        try {
            lineReader.readLineBatches(Path.of(inputSource), lines -> {
                for (String line : lines) {
                    try {
                        processLine(line, schoolCounts);
                    } catch (Exception e) {
                        // 개별 라인 파싱 실패 시 무시
                    }
                }
            });

            log.info("카운팅 완료. 총 {}개의 고유 학교 발견.", schoolCounts.size());
        } catch (IOException e) {
            log.error("데이터 처리 중 I/O 오류 발생: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("데이터 처리 중 예상치 못한 오류 발생: {}", e.getMessage(), e);
        }

        writeResultsToFile(schoolCounts, outputDestination);
    }

    private void processLine(String line, Map<String, Long> schoolCounts) {
        schoolNameParser.parseLine(line).stream()
                .map(schoolNameResolveService::resolveToFullName)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .forEach(fullName -> schoolCounts.merge(fullName, 1L, Long::sum));
    }

    private void writeResultsToFile(Map<String, Long> schoolCounts, String outputDestination) {
        try {
            resultWriter.writeResults(schoolCounts, outputDestination);
        } catch (IOException e) {
            log.error("결과 파일 '{}' 작성 중 I/O 오류 발생: {}", outputDestination, e.getMessage(), e);
        } catch (Exception e) {
            log.error("결과 파일 '{}' 작성 중 예상치 못한 오류 발생: {}", outputDestination, e.getMessage(), e);
        }
    }
}