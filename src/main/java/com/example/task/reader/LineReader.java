package com.example.task.reader;

import com.example.task.processor.LineBatchProcessor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

@Service
public class LineReader {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static final int BATCH_SIZE = 500;

    public void readLineBatches(Path path, LineBatchProcessor processor) throws IOException {
        try (Stream<String> lines = Files.lines(path)) {
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            List<String> batch = new ArrayList<>(BATCH_SIZE);

            for (String line : (Iterable<String>) lines::iterator) {
                batch.add(line);
                if (batch.size() == BATCH_SIZE) {
                    List<String> copy = new ArrayList<>(batch);
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

    private CompletableFuture<Void> processBatch(List<String> batch, LineBatchProcessor processor) {
        return CompletableFuture.runAsync(() -> processor.process(batch), executor);
    }
}
