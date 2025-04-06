package com.example.task.processor;

import java.util.List;

public interface LineBatchProcessor {

    void process(List<String> lines);
}
