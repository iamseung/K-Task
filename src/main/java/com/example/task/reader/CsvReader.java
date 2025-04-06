package com.example.task.reader;

import com.example.task.processor.CsvProcessor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class CsvReader {

    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final int BATCH_SIZE = 500;

    public void readCsvInBatches(Path csvPath, CsvProcessor processor) throws IOException {
        try (Reader reader = Files.newBufferedReader(csvPath)) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withTrim()
                    .parse(reader);

            List<CSVRecord> batch = new ArrayList<>(BATCH_SIZE);
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (CSVRecord record : records) {
                batch.add(record);
                if (batch.size() == BATCH_SIZE) {
                    List<CSVRecord> copy = new ArrayList<>(batch);
                    futures.add(processBatch(copy, processor));
                    batch.clear();
                }
            }

            if (!batch.isEmpty()) {
                futures.add(processBatch(batch, processor));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        }
    }

    private CompletableFuture<Void> processBatch(List<CSVRecord> batch, CsvProcessor processor) {
        return CompletableFuture.runAsync(() -> processor.process(batch), executor);
    }
}
