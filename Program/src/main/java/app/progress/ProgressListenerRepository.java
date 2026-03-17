package app.progress;

public interface ProgressListenerRepository {
    void onProgress(int processed, int total);
}
