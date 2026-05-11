package app.file;

import app.model.SearchSummary;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ResultWriter {
    public void writeToFile(SearchSummary summary, String filePath) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            writer.write("Search Results\n");
            writer.write("-------------------------\n");
            writer.write("Mode: " + summary.getModeName() + "\n");
            writer.write("Keyword: " + summary.getKeyword() + "\n");
            writer.write("Total files: " + summary.getTotalFiles() + "\n");
            writer.write("Files with matches: " + summary.getFilesWithMatches() + "\n");
            writer.write("Total matches: " + summary.getTotalMatches() + "\n");
            writer.write("Duration: " + summary.getDurationMs() + " ms\n");

        } catch (IOException e) {
            throw new RuntimeException("Error writing results to file", e);
        }
    }
}
