package app.model;

public class SearchSummary {
    private int totalFiles;
    private int filesWithMatches;
    private int totalMatches;
    private long durationMs;
    private String modeName;

    public SearchSummary(int totalFiles, int filesWithMatches, int totalMatches, long durationMs, String modeName) {
        this.totalFiles = totalFiles;
        this.filesWithMatches = filesWithMatches;
        this.totalMatches = totalMatches;
        this.durationMs = durationMs;
        this.modeName = modeName;
    }

    public int getTotalFiles() {
        return totalFiles;
    }

    public int getFilesWithMatches() {
        return filesWithMatches;
    }

    public int getTotalMatches() {
        return totalMatches;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public String getModeName() {
        return modeName;
    }
}
