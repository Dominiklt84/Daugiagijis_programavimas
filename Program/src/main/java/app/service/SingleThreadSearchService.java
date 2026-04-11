package app.service;

import app.model.SearchSummary;
import app.model.SearchMode;
import app.progress.ProgressListener;
import app.scanner.FileScanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SingleThreadSearchService implements SearchService {

    private final FileScanner scanner;

    public SingleThreadSearchService(FileScanner scanner) {
        this.scanner = scanner;
    }

    @Override
    public SearchSummary search(Path folder, String keyword, ProgressListener listener) {

        long startTime = System.currentTimeMillis();

        int totalFiles = 0;
        int totalMatches = 0;
        int filesWithMatches = 0;
        int processed = 0;

        try {

            List<Path> files;

            try (var stream = Files.walk(folder)) {
                files = stream.filter(Files::isRegularFile).toList();
            }

            totalFiles = files.size();

            for (Path file : files) {

                int count = scanner.countMatches(file, keyword);

                if (count > 0) {
                    filesWithMatches++;
                }

                totalMatches += count;
                processed++;

                if (listener != null) {
                    listener.onProgress(processed, totalFiles);
                }
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
                SearchMode.SINGLE_THREAD
        );
    }
}