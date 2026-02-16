package app.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
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

        modeCombo.getItems().addAll("Single Thread","Threads","ThreadPool");
        modeCombo.setPromptText("Select execution mode");

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
        outputArea.appendText("Threads: "+threads+"\n");

        outputArea.appendText("Ready to start search...\n");
    }

    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
