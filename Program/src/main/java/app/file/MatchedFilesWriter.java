package app.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class MatchedFilesWriter {

    public void write(List<String> files, String path) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {

            for (String file : files) {
                writer.write(file);
                writer.newLine();
            }

        } catch (IOException e) {
            throw new RuntimeException("Error writing matched files", e);
        }
    }
}