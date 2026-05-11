package app.history;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class HistoryReader {

    public String read() {

        try {

            Path path = Path.of("history.txt");

            if (!Files.exists(path)) {
                return "No history yet.";
            }

            return Files.readString(path);

        } catch (IOException e) {
            throw new RuntimeException("Error reading history", e);
        }
    }
}