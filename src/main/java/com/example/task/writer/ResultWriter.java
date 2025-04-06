package com.example.task.writer;

import java.io.IOException;
import java.util.Map;

public interface ResultWriter {

    void writeResults(Map<String, Long> results, String destinationIdentifier) throws IOException;
}