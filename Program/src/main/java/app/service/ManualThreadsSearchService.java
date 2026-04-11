package app.service;

import app.model.SearchMode;
import app.model.SearchSummary;
import app.progress.ProgressListener;
import app.scanner.FileScanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.atomic.AtomicInteger;

public class ManualThreadsSearchService implements SearchService {

    private final int threadCount;
    private final FileScanner scanner;

    public ManualThreadsSearchService(int threadCount, FileScanner scanner) {
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

            List<Thread> threads = new ArrayList<>();

            int chunkSize = Math.max(1, totalFiles / actualThreadCount);

            for (int i = 0; i < actualThreadCount; i++) {

                int start = i * chunkSize;
                int end;

                if (i == actualThreadCount - 1) {
                    end = totalFiles;
                } else {
                    end = Math.min(totalFiles, start + chunkSize);
                }

                Thread thread = new Thread(() -> {

                    for (int j = start; j < end; j++) {

                        Path file = files.get(j);

                        int count = scanner.countMatches(file, keyword);

                        if (count > 0) {
                            filesWithMatches.incrementAndGet();
                        }

                        totalMatches.addAndGet(count);

                        int done = processed.incrementAndGet();

                        if (listener != null) {
                            listener.onProgress(done, totalFiles);
                        }
                    }
                });

                threads.add(thread);
                thread.start();
            }

            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted", e);
                }
            }

            long duration = System.currentTimeMillis() - startTime;

            return new SearchSummary(
                    totalFiles,
                    filesWithMatches.get(),
                    totalMatches.get(),
                    duration,
                    SearchMode.MANUAL_THREADS
            );

        } catch (IOException e) {
            throw new RuntimeException("Error during search", e);
        }
    }
}