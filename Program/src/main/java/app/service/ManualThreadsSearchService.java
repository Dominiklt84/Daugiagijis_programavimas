package app.service;

import app.model.SearchSummary;
import app.progress.ProgressListenerRepository;
import app.scanner.FileScannerRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.atomic.AtomicInteger;

public class ManualThreadsSearchService implements ServiceRepository {

    private final int threadCount;
    private final FileScannerRepository scanner;

    public ManualThreadsSearchService(int threadCount, FileScannerRepository scanner) {
        this.threadCount = threadCount;
        this.scanner = scanner;
    }

    @Override
    public SearchSummary search(Path folder, String keyword, ProgressListenerRepository listener) {

        long startTime = System.currentTimeMillis();

        AtomicInteger processed = new AtomicInteger(0);
        AtomicInteger totalMatches = new AtomicInteger(0);
        AtomicInteger filesWithMatches = new AtomicInteger(0);

        try {

            List<Path> files = Files.walk(folder).filter(Files::isRegularFile).toList();

            final int totalFiles = files.size();

            List<Thread> threads = new ArrayList<>();

            int chunkSize = Math.max(1, files.size() / threadCount);

            for (int i = 0; i < threadCount; i++) {

                int start = i * chunkSize;
                int end = (i == threadCount - 1)
                        ? files.size()
                        : Math.min(files.size(), start + chunkSize);

                Thread thread = new Thread(() -> {

                    for (int j = start; j < end; j++) {

                        Path file = files.get(j);

                        int count = scanner.countMatches(file, keyword);

                        if (count > 0) {
                            filesWithMatches.incrementAndGet();
                        }

                        totalMatches.addAndGet(count);

                        int done = processed.incrementAndGet();
                        listener.onProgress(done, totalFiles);
                    }
                });

                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                thread.join();
            }

            long duration = System.currentTimeMillis() - startTime;

            return new SearchSummary(
                    totalFiles,
                    filesWithMatches.get(),
                    totalMatches.get(),
                    duration,
                    "Manual Threads (" + threadCount + ")"
            );

        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Error during search", e);
        }
    }
}