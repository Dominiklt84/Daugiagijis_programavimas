package app.service;

import app.file.FileReader;
import app.file.FileReaderImpl;
import app.model.PartialResult;
import app.model.SearchSummary;
import app.model.SearchMode;
import app.progress.ProgressListener;
import app.file.FileScanner;
import app.time.ExecutionTimer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class ThreadPoolSearchService implements SearchService {
    private final int threadCount;
    private final FileScanner scanner;
    private final FileReader fileReader;

    public ThreadPoolSearchService(int threadCount, FileScanner scanner,FileReader fileReader) {
        this.threadCount = threadCount;
        this.scanner = scanner;
        this.fileReader=fileReader;
    }

    @Override
    public SearchSummary search(Path folder, String keyword, ProgressListener listener) {
        ExecutionTimer timer = new ExecutionTimer();
        timer.start();

        try {
            List<Path> files = fileReader.getFiles(folder);

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

        } catch (Exception e) {
            throw new RuntimeException("Error during thread pool search", e);
        }
    }
}