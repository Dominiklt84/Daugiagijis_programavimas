package app.scanner;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileScannerImpl implements FileScanner {

    @Override
    public int countMatches(Path file, String keyword) {
        int count = 0;

        try (BufferedReader reader = Files.newBufferedReader(file)) {
            String line;
            String lowerKeyword = keyword.toLowerCase();

            while ((line = reader.readLine()) != null) {
                if (line.toLowerCase().contains(lowerKeyword)) {
                    count++;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: "+file,e);
        }
        return count;
    }

}
