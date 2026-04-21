package app.service;

import app.model.PartialResult;
import app.model.SearchSummary;
import app.model.SearchMode;
import app.progress.ProgressListener;
import app.scanner.FileScanner;
import app.time.ExecutionTimer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolSearchService implements SearchService {
    private final int threadCount;
    private final FileScanner scanner;

    public ThreadPoolSearchService(int threadCount, FileScanner scanner) {
        this.threadCount = threadCount;
        this.scanner = scanner;
    }

    @Override
    public SearchSummary search(Path folder, String keyword, ProgressListener listener) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

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
            List<PartialResult> results=new ArrayList<>();
            int[] processedTotal = {0};

            for (Path file : files) {
                PartialResult partialResult=new PartialResult();
                results.add(partialResult);

                pool.submit(() -> {
                    int count = scanner.countMatches(file, keyword);

                    partialResult.addMatch(count);
                    processedTotal[0]++;

                    if (listener != null) {
                        listener.onProgress(processedTotal[0], totalFiles);
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

            int totalMatches = 0;
            int filesWithMatches = 0;

            for (PartialResult r : results) {
                totalMatches += r.getTotalMatches();
                filesWithMatches += r.getFilesWithMatches();
            }

            long duration = timer.stop();

            return new SearchSummary(
                    totalFiles,
                    filesWithMatches,
                    totalMatches,
                    duration,
                    SearchMode.THREAD_POOL
            );

        } catch (IOException e) {
            throw new RuntimeException("Error during thread pool search", e);
        }
    }
}