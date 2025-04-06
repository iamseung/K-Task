package com.example.task.processor;

import org.apache.commons.csv.CSVRecord;

import java.util.List;

public interface CsvProcessor {
    void process(List<CSVRecord> batch);
}
