package app.service;

import app.model.SearchSummary;
import app.progress.ProgressListenerRepository;

import java.nio.file.Path;

public interface ServiceRepository {
    SearchSummary search(Path folder, String keyword, ProgressListenerRepository listener);
}
