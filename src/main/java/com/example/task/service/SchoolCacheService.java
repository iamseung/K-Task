package com.example.task.service;

import com.example.task.processor.SchoolCacheProcessor;
import com.example.task.reader.CsvReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class SchoolCacheService {

    private final CsvReader csvReader;
    private final SchoolCacheProcessor schoolCacheProcessor;

    public void cacheSchools(Path csvPath) throws IOException {
        csvReader.readCsvInBatches(csvPath, schoolCacheProcessor);
    }
}
