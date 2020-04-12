package com.company;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    private TextArea textArea;

    private Locale locale = new Locale("fi", "FI");
    private ResourceBundle labels;

    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        labels = ResourceBundle.getBundle("ui", locale);

        stage.setTitle(labels.getString("title"));


        Parent layout = initializeUI();

        Scene content = new Scene(layout, 640, 480);

        stage.setScene(content);
        stage.show();
    }

    private Parent initializeUI() {
        BorderPane layout = new BorderPane();

        textArea = new TextArea();
        textArea.setFont(Font.font("Monaco", FontWeight.NORMAL, 14));

        layout.setTop(createMenuBar());
        layout.setCenter(textArea);

        return layout;

    }

    private Node createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");

        MenuItem open = new MenuItem(labels.getString("open"));
        open.setOnAction(this::openFileChooser);

        MenuItem save = new MenuItem(labels.getString("save"));
        save.setOnAction(this::saveFileChooser);

        menuFile.getItems().addAll(new MenuItem("New"),
                open,
                save,
                new SeparatorMenuItem(),
                new MenuItem("Exit"));

        Menu menuEdit = new Menu("Edit");
        Menu menuView = new Menu("View");
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
        return menuBar;
    }

    private void openFileChooser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Text File");
        File file = fileChooser.showOpenDialog(this.stage);
        if(file != null) {
            open(file);
        }
    }
    private void saveFileChooser(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text File");
        File file = fileChooser.showSaveDialog(this.stage);
        if(file != null) {
            save(file);
        }
    }

    private void open(File file) {
        FileHandler.open(file.getPath(), (content, errorMsg) -> {
            if(errorMsg.isPresent()) {
                displayErrorMsg(errorMsg.get());
            } else {
                textArea.setText(content);
            }
        });
    }

    private void save(File file) {
        FileHandler.save(textArea.getText(), file.getPath(), (errorMsg) -> {
            System.out.println("done!");
        });
    }

    private void displayErrorMsg(String s) {
    }

    public static void main(String... args) {
        launch(args);
    }

}