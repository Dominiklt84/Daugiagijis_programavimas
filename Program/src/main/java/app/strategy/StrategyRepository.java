package app.strategy;

import app.model.SearchSummary;
import java.nio.file.Path;

public interface StrategyRepository {
    SearchSummary search(Path folder, String keyword);
}
