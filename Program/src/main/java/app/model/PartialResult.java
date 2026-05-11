package app.model;

import java.util.ArrayList;
import java.util.List;

public class PartialResult {

    private int processed;
    private int totalMatches;
    private int filesWithMatches;
    private final List<String> matchedFiles = new ArrayList<>();

    public void addMatch(int count) {
        totalMatches += count;
        if (count > 0) {
            filesWithMatches++;
        }
        processed++;
    }

    public void addMatchedFile(String fileName) {
        matchedFiles.add(fileName);
    }

    public int getProcessed() {
        return processed;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public int getFilesWithMatches() {
        return filesWithMatches;
    }

    public List<String> getMatchedFiles() {
        return matchedFiles;
    }
}