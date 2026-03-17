package app.strategy;

import app.model.SearchSummary;
import app.progress.ProgressListenerRepository;
import app.scanner.FileScanner;
import app.scanner.FileScannerRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ManualThreadsSearch implements StrategyRepository {

    private int threadCount;

    public ManualThreadsSearch(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public SearchSummary search(Path folder, String keyword, ProgressListenerRepository listener) {

        long startTime = System.currentTimeMillis();

        FileScannerRepository scanner = new FileScanner();
        AtomicInteger processed = new AtomicInteger(0);

        int totalMatches = 0;

        try {

            List<Path> files = Files.walk(folder)
                    .filter(Files::isRegularFile)
                    .toList();

            final int totalFiles = files.size();

            List<Thread> threads = new ArrayList<>();
            List<Integer> results = new ArrayList<>();

            for (int i = 0; i < threadCount; i++) {
                results.add(0);
            }

            int chunkSize = Math.max(1, files.size() / threadCount);

            for (int i = 0; i < threadCount; i++) {

                int start = i * chunkSize;
                int end = (i == threadCount - 1)
                        ? files.size()
                        : Math.min(files.size(), start + chunkSize);

                int index = i;

                Thread thread = new Thread(() -> {

                    int localMatches = 0;

                    for (int j = start; j < end; j++) {

                        Path file = files.get(j);

                        int count = scanner.countMatches(file, keyword);
                        localMatches += count;

                        int done = processed.incrementAndGet();
                        listener.onProgress(done, totalFiles);
                    }

                    synchronized (results) {
                        results.set(index, localMatches);
                    }
                });

                threads.add(thread);
                thread.start();
            }

            // czekamy na wszystkie wątki
            for (Thread thread : threads) {
                thread.join();
            }

            // sumujemy wyniki
            for (int count : results) {
                totalMatches += count;
            }

            long duration = System.currentTimeMillis() - startTime;

            return new SearchSummary(
                    totalFiles,
                    0,
                    totalMatches,
                    duration,
                    "Manual Threads (" + threadCount + ")"
            );

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return new SearchSummary(0, 0, 0, 0, "Error");
    }
}