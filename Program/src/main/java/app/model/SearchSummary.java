package app.model;

import java.util.List;

public class SearchSummary {
    private String keyword;
    private int totalFiles;
    private int filesWithMatches;
    private int totalMatches;
    private long durationMs;
    private SearchMode modeName;
    private List<String> matchedFiles;

    public SearchSummary(String keyword, int totalFiles, int filesWithMatches, int totalMatches, long durationMs, SearchMode modeName, List<String> matchedFiles) {
        this.keyword = keyword;
        this.totalFiles = totalFiles;
        this.filesWithMatches = filesWithMatches;
        this.totalMatches = totalMatches;
        this.durationMs = durationMs;
        this.modeName = modeName;
        this.matchedFiles = matchedFiles;
    }

    public String getKeyword() {
        return keyword;
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

    public SearchMode getModeName() {
        return modeName;
    }

    public List<String> getMatchedFiles() {
        return matchedFiles;
    }
}
