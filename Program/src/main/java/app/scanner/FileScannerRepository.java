package app.scanner;

import java.nio.file.Path;

public interface FileScannerRepository {
    int countMatches(Path file, String keyword);
}
