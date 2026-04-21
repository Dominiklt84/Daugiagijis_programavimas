package app.model;

public class PartialResult {

    private int processed;
    private int totalMatches;
    private int filesWithMatches;

    public void addMatch(int count) {
        totalMatches += count;
        if (count > 0) {
            filesWithMatches++;
        }
        processed++;
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
}