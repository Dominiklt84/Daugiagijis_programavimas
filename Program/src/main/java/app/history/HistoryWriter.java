package app.history;

import app.model.SearchSummary;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class HistoryWriter {

    public void save(SearchSummary summary) {

        try (BufferedWriter writer =
                     new BufferedWriter(new FileWriter("history.txt", true))) {

            writer.write("Mode: " + summary.getModeName());
            writer.newLine();

            writer.write("Keyword: " + summary.getKeyword());
            writer.newLine();

            writer.write("Total files: " + summary.getTotalFiles());
            writer.newLine();

            writer.write("Matches: " + summary.getTotalMatches());
            writer.newLine();

            writer.write("Time: " + summary.getDurationMs() + " ms");
            writer.newLine();

            writer.write("----------------------");
            writer.newLine();

        } catch (IOException e) {
            throw new RuntimeException("Error saving history", e);
        }
    }
}