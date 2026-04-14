package app.service;

import app.model.SearchSummary;
import app.model.SearchMode;
import app.progress.ProgressListener;
import app.scanner.FileScanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolSearchService implements SearchService {
    private final int threadCount;
    private final FileScanner scanner;

    public ThreadPoolSearchService(int threadCount, FileScanner scanner) {
        this.threadCount = threadCount;
        this.scanner = scanner;
    }

    @Override
    public SearchSummary search(Path folder, String keyword, ProgressListener listener) {
        long startTime = System.currentTimeMillis();
        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger totalMatches = new AtomicInteger(0);
        AtomicInteger filesWithMatches = new AtomicInteger(0);

        try {
            List<Path> files;

            try (var stream = Files.walk(folder)) {
                files = stream.filter(Files::isRegularFile).toList();
            }

            int totalFiles = files.size();
            int actualThreadCount = threadCount;

            if (threadCount > totalFiles) {
                actualThreadCount = totalFiles;
            }

            if (actualThreadCount == 0) {
                actualThreadCount = 1;
            }

            ExecutorService pool = Executors.newFixedThreadPool(actualThreadCount);

            for (Path file : files) {
                pool.submit(() -> {
                    int count = scanner.countMatches(file, keyword);

                    if (count > 0) {
                        filesWithMatches.incrementAndGet();
                    }

                    totalMatches.addAndGet(count);
                    int done = processed.incrementAndGet();

                    if (listener != null) {
                        listener.onProgress(done, totalFiles);
                    }
                });
            }

            pool.shutdown();

            try {
                pool.awaitTermination(1, TimeUnit.HOURS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Thread interrupted", e);
            }

            long duration = System.currentTimeMillis() - startTime;

            return new SearchSummary(
                    totalFiles,
                    filesWithMatches.get(),
                    totalMatches.get(),
                    duration,
                    SearchMode.THREAD_POOL
            );

        } catch (IOException e) {
            throw new RuntimeException("Error during thread pool search", e);
        }
    }
}