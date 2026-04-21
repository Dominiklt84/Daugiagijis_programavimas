package app.service;

import app.scanner.FileScanner;
import app.scanner.FileScannerImpl;

public class SearchServiceController {
    public static SearchService create(String mode, int threads){
        FileScanner scanner=new FileScannerImpl();

        switch (mode){
            case "Single Thread":
                return new SingleThreadSearchService(scanner);
            case "ThreadPool":
                return new ThreadPoolSearchService(threads,scanner);
            default:
                return new ManualThreadsSearchService(threads, scanner);
        }
    }
}
