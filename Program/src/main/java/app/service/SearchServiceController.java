package app.service;

import app.file.FileReader;
import app.file.FileScanner;
import app.file.FileScannerImpl;

public class SearchServiceController {
    public static SearchService create(String mode, int threads, FileScanner fileScanner, FileReader fileReader){
        FileScanner scanner=new FileScannerImpl();

        switch (mode){
            case "Single Thread":
                return new SingleThreadSearchService(scanner,fileReader);
            case "ThreadPool":
                return new ThreadPoolSearchService(threads,scanner,fileReader);
            default:
                return new ManualThreadsSearchService(threads, scanner,fileReader);
        }
    }
}
