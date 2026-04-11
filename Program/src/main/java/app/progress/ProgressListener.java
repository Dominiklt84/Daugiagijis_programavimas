package app.progress;

public interface ProgressListener {
    void onProgress(int processed, int total);
}
