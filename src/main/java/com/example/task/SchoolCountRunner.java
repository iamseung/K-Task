package com.example.task;

import com.example.task.service.SchoolCounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SchoolCountRunner implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(SchoolCountRunner.class);
    private static final String INPUT_FILENAME = "data/comments.csv"; // 입력 파일 이름
    private static final String OUTPUT_FILENAME = "result.txt";   // 출력 파일 이름

    private final SchoolCounterService schoolCounterService;

    @Autowired
    public SchoolCountRunner(SchoolCounterService schoolCounterService) {
        this.schoolCounterService = schoolCounterService;
    }

    @Override
    public void run(String... args) throws Exception {
        schoolCounterService.processSchoolCounts(INPUT_FILENAME, OUTPUT_FILENAME);
    }
}