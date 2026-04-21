package app.file;

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
                String lowerLine = line.toLowerCase();
                int index = 0;

                while ((index = lowerLine.indexOf(lowerKeyword, index)) != -1) {
                    count++;
                    index += lowerKeyword.length();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: "+file,e);
        }
        return count;
    }

}
