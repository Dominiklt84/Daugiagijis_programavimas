package app.file;

import java.nio.file.Path;
import java.util.List;

public interface FileReader {
    List<Path> getFiles(Path folder);
}
