package app.file;

import java.nio.file.Path;

public interface FileScanner {
    int countMatches(Path file, String keyword);
}
