package app.service;

import app.model.SearchSummary;
import app.progress.ProgressListener;

import java.nio.file.Path;

public interface SearchService {
    SearchSummary search(Path folder, String keyword, ProgressListener listener);
}
