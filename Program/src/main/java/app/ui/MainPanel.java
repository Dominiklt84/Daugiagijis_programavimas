package app.ui;

import app.file.*;
import app.model.SearchSummary;
import app.service.SearchServiceController;
import app.service.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.util.ResourceBundle;

public class MainPanel implements Initializable {
    private static final String MODE_SINGLE = "Single Thread";
    private static final String MODE_THREADS = "Manual Threads";
    private static final String MODE_POOL = "ThreadPool";

    @FXML private TextField folderField;
    @FXML private Button browseButton;

    @FXML private TextField keywordField;
    @FXML private ComboBox<String> modeCombo;

    @FXML private Spinner<Integer> threadsSpinner;
    @FXML private Button startButton;

    @FXML private Label timeLabel;
    @FXML private ProgressBar progressBar;
    @FXML private TextArea outputArea;

    @Override
    public void initialize(URL location, ResourceBundle resources){
        SpinnerValueFactory<Integer> valueFactory= new SpinnerValueFactory.IntegerSpinnerValueFactory(1,16,4);
        threadsSpinner.setValueFactory(valueFactory);
        threadsSpinner.setEditable(true);

        modeCombo.getItems().addAll(MODE_SINGLE, MODE_THREADS, MODE_POOL);
        modeCombo.setPromptText("Select execution mode");

        modeCombo.setOnAction(e -> {
            String mode = modeCombo.getValue();
            threadsSpinner.setDisable(MODE_SINGLE.equals(mode));
        });

        progressBar.setProgress(0);
        timeLabel.setText("0");
        outputArea.clear();

        browseButton.setOnAction(e->handleBrowse());
        startButton.setOnAction(e->handleStart());
    }

    private void handleBrowse(){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Folder");

        Stage stage = (Stage) browseButton.getScene().getWindow();
        File selectedDirectory = directoryChooser.showDialog(stage);

        if(selectedDirectory!=null){
            folderField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    private void handleStart(){
        FileScanner fileScanner=new FileScannerImpl();
        FileReader fileReader=new FileReaderImpl();
        String folderPath = folderField.getText();
        String keyword = keywordField.getText();
        String mode = modeCombo.getValue();
        int threads = threadsSpinner.getValue();

        if(folderPath == null || folderPath.isEmpty()){
            showAlert("Please select a folder.");
            return;
        }

        if(keyword == null || keyword.isEmpty()){
            showAlert("Please enter a keyword.");
            return;
        }

        if(mode == null){
            showAlert("Please select execution mode.");
            return;
        }

        outputArea.clear();
        progressBar.setProgress(0);
        timeLabel.setText("0");

        outputArea.appendText("Folder: "+folderPath+"\n");
        outputArea.appendText("Keyword: "+keyword+"\n");
        outputArea.appendText("Mode: "+mode+"\n");
        if (MODE_SINGLE.equals(mode)) {
            outputArea.appendText("Threads: 1\n");
        } else {
            outputArea.appendText("Threads: " + threads + "\n");
        }
        outputArea.appendText("Starting search...\n");

        SearchService strategy = SearchServiceController.create(mode, threads,fileScanner,fileReader);
        Path folder = Path.of(folderPath);

        startButton.setDisable(true);
        Thread worker = new Thread(() -> {

            SearchSummary summary = strategy.search(folder,keyword,(processed, total) -> {
                double progress = (double) processed / total;

                javafx.application.Platform.runLater(() -> {
                    progressBar.setProgress(progress);
                });
            });

            javafx.application.Platform.runLater(() -> {

                timeLabel.setText(summary.getDurationMs() + " ms");

                outputArea.appendText("\nSearch finished!\n");
                outputArea.appendText("Mode: " + summary.getModeName() + "\n");
                outputArea.appendText("Total files: " + summary.getTotalFiles() + "\n");
                outputArea.appendText("Files with matches: " + summary.getFilesWithMatches() + "\n");
                outputArea.appendText("Total matches: " + summary.getTotalMatches() + "\n");

                ResultWriter writer = new ResultWriter();
                writer.writeToFile(summary, "results.txt");

                progressBar.setProgress(1);
                startButton.setDisable(false);
            });

        });
        worker.setDaemon(true);
        worker.start();
    }

    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
