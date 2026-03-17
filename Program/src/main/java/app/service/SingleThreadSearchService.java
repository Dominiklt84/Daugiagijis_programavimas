package app.service;

import app.model.SearchSummary;
import app.progress.ProgressListenerRepository;
import app.scanner.FileScannerRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SingleThreadSearchService implements ServiceRepository {

    private final FileScannerRepository scanner;

    public SingleThreadSearchService(FileScannerRepository scanner) {
        this.scanner = scanner;
    }

    @Override
    public SearchSummary search(Path folder, String keyword, ProgressListenerRepository listener) {

        long startTime = System.currentTimeMillis();

        int totalFiles = 0;
        int totalMatches = 0;
        int filesWithMatches = 0;
        int processed = 0;

        try {

            List<Path> files = Files.walk(folder)
                    .filter(Files::isRegularFile)
                    .toList();

            totalFiles = files.size();

            for (Path file : files) {

                int count = scanner.countMatches(file, keyword);

                if (count > 0) {
                    filesWithMatches++;
                }

                totalMatches += count;
                processed++;

                listener.onProgress(processed, totalFiles);
            }

        } catch (IOException e) {
            throw new RuntimeException("Error during single thread search", e);
        }

        long duration = System.currentTimeMillis() - startTime;

        return new SearchSummary(
                totalFiles,
                filesWithMatches,
                totalMatches,
                duration,
                "Single Thread"
        );
    }
}