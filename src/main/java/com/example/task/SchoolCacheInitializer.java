package com.example.task;

import com.example.task.service.SchoolCacheService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@RequiredArgsConstructor
public class SchoolCacheInitializer implements ApplicationRunner {

    private final SchoolCacheService schoolCacheService;
    private static final Logger log = LoggerFactory.getLogger(SchoolCountRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("학교 데이터 캐싱 시작");
        Path csvPath1 = Paths.get("data/schoolV1.csv"); // V1
        Path csvPath2 = Paths.get("data/schoolV2.csv"); // V2
        schoolCacheService.cacheSchools(csvPath1);
        schoolCacheService.cacheSchools(csvPath2);
        log.info("학교 데이터 캐싱 완료");
    }
}