package app.strategy;

import app.model.SearchSummary;
import app.scanner.FileScanner;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class SingleThreadSearch implements StrategyRepository {
    @Override
    public SearchSummary search(Path folder, String keyword) {

        long startTime = System.currentTimeMillis();

        FileScanner scanner = new FileScanner();

        int totalFiles = 0;
        int totalMatches = 0;
        int filesWithMatches = 0;

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
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        return new SearchSummary(
                totalFiles,
                filesWithMatches,
                totalMatches,
                duration,
                "Single Thread"
        );
    }
}
