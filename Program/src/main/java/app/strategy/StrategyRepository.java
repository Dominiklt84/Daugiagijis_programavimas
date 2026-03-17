package app.strategy;

import app.model.SearchSummary;
import app.progress.ProgressListenerRepository;

import java.nio.file.Path;

public interface StrategyRepository {
    SearchSummary search(Path folder, String keyword, ProgressListenerRepository listener);
}
