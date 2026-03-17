package app.strategy;

import app.model.SearchSummary;
import app.progress.ProgressListenerRepository;
import app.scanner.FileScanner;
import app.scanner.FileScannerRepository;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolSearch implements StrategyRepository {

    private int threadCount;

    public ThreadPoolSearch(int threadCount) {
        this.threadCount = threadCount;
    }

    @Override
    public SearchSummary search(Path folder, String keyword, ProgressListenerRepository listener) {

        long startTime = System.currentTimeMillis();

        FileScannerRepository scanner = new FileScanner();
        AtomicInteger processed = new AtomicInteger(0);

        int totalMatches = 0;

        ExecutorService pool = Executors.newFixedThreadPool(threadCount);

        try {

            List<Path> files = Files.walk(folder)
                    .filter(Files::isRegularFile)
                    .toList();

            final int totalFiles = files.size();

            List<Future<Integer>> futures = new CopyOnWriteArrayList<>();

            for (Path file : files) {

                Future<Integer> future = pool.submit(() -> {

                    int count = scanner.countMatches(file, keyword);


                    int done = processed.incrementAndGet();
                    listener.onProgress(done, totalFiles);

                    return count;
                });

                futures.add(future);
            }


            for (Future<Integer> future : futures) {
                totalMatches += future.get();
            }

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        pool.shutdown();

        long duration = System.currentTimeMillis() - startTime;

        return new SearchSummary(
                processed.get(),
                0,
                totalMatches,
                duration,
                "ThreadPool (" + threadCount + ")"
        );
    }
}