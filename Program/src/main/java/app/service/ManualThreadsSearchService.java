package app.service;

import app.file.FileReader;
import app.model.PartialResult;
import app.model.SearchMode;
import app.model.SearchSummary;
import app.progress.ProgressListener;
import app.file.FileScanner;
import app.time.ExecutionTimer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class ManualThreadsSearchService implements SearchService {
    private final int threadCount;
    private final FileScanner scanner;
    private final FileReader fileReader;

    public ManualThreadsSearchService(int threadCount, FileScanner scanner,FileReader fileReader) {
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

            List<Thread> threads = new ArrayList<>();
            List<PartialResult> results=new ArrayList<>();
            int chunkSize = Math.max(1, totalFiles / actualThreadCount);
            int[] processedTotal = {0};

            for (int i = 0; i < actualThreadCount; i++) {
                int start = i * chunkSize;
                int end;

                if (i == actualThreadCount - 1) {
                    end = totalFiles;
                } else {
                    end = Math.min(totalFiles, start + chunkSize);
                }

                PartialResult partialResult=new PartialResult();
                results.add(partialResult);

                Thread thread = new Thread(() -> {
                    for (int j = start; j < end; j++) {
                        Path file = files.get(j);
                        int count = scanner.countMatches(file, keyword);

                        partialResult.addMatch(count);
                        processedTotal[0]++;

                        if (listener != null) {
                            listener.onProgress(processedTotal[0], totalFiles);
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
                    SearchMode.MANUAL_THREADS
            );

        } catch (Exception e) {
            throw new RuntimeException("Error during search", e);
        }
    }
}