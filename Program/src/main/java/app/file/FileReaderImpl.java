package app.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileReaderImpl implements FileReader{
    @Override
    public List<Path> getFiles(Path folder) {
        List<Path> files = new ArrayList<>();

        try (var stream = Files.walk(folder)) {
            for (Path p : stream.toList()) {
                if (Files.isRegularFile(p)) {
                    files.add(p);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading files from folder: " + folder, e);
        }
        return files;
    }
}
