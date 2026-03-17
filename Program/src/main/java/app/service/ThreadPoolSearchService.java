package app.service;

import app.model.SearchSummary;
import app.progress.ProgressListenerRepository;
import app.scanner.FileScannerRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolSearchService implements ServiceRepository {

    private final int threadCount;
    private final FileScannerRepository scanner;

    public ThreadPoolSearchService(int threadCount, FileScannerRepository scanner) {
        this.threadCount = threadCount;
        this.scanner = scanner;
    }

    @Override
    public SearchSummary search(Path folder, String keyword, ProgressListenerRepository listener) {

        long startTime = System.currentTimeMillis();

        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger totalMatches = new AtomicInteger(0);
        AtomicInteger filesWithMatches = new AtomicInteger(0);

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        try {

            List<Path> files = Files.walk(folder)
                    .filter(Files::isRegularFile)
                    .toList();

            final int totalFiles = files.size();

            List<Future<?>> futures = new ArrayList<>();

            for (Path file : files) {

                Future<?> future = pool.submit(() -> {

                    int count = scanner.countMatches(file, keyword);

                    if (count > 0) {
                        filesWithMatches.incrementAndGet();
                    }

                    totalMatches.addAndGet(count);

                    int done = processed.incrementAndGet();
                    listener.onProgress(done, totalFiles);
                });

                futures.add(future);
            }

            for (Future<?> future : futures) {
                future.get();
            }

            long duration = System.currentTimeMillis() - startTime;

            return new SearchSummary(
                    totalFiles,
                    filesWithMatches.get(),
                    totalMatches.get(),
                    duration,
                    "ThreadPool (" + threadCount + ")"
            );

        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException("Error during thread pool search", e);
        } finally {
            pool.shutdown();
        }
    }
}