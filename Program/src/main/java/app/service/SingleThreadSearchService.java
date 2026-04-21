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
import java.util.List;

public class SingleThreadSearchService implements SearchService {
    private final FileScanner scanner;
    private final FileReader fileReader;

    public SingleThreadSearchService(FileScanner scanner, FileReader fileReader) {
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

        } catch (Exception e) {
            throw new RuntimeException("Error during single thread search", e);
        }
    }
}