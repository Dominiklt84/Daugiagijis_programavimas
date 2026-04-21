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
import java.util.List;

public class SingleThreadSearchService implements SearchService {
    private final FileScanner scanner;

    public SingleThreadSearchService(FileScanner scanner) {
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
            PartialResult result = new PartialResult();

            for (Path file : files) {
                int count = scanner.countMatches(file, keyword);
                result.addMatch(count);

                if (listener != null) {
                    listener.onProgress(result.getProcessed(), totalFiles);
                }
            }

            long duration = timer.stop();

            return new SearchSummary(
                    totalFiles,
                    result.getFilesWithMatches(),
                    result.getTotalMatches(),
                    duration,
                    SearchMode.SINGLE_THREAD
            );

        } catch (IOException e) {
            throw new RuntimeException("Error during single thread search", e);
        }
    }
}