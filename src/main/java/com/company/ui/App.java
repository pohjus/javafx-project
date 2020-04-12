package com.company.ui;

import com.company.util.FileHandler;
import com.company.util.JavaCompiler;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    private TextArea textArea;

    private Locale locale = new Locale("fi", "FI");
    private ResourceBundle labels;

    private Stage stage;
    private TextArea terminal;

    @Override
    public void start(Stage stage) {
        this.stage = stage;


        labels = ResourceBundle.getBundle("ui", locale);

        stage.setTitle(labels.getString("title"));


        Parent layout = initializeUI();

        Scene content = new Scene(layout, 640, 480);
        content.getStylesheets().add("style.css");

        stage.setScene(content);
        stage.show();
    }

    private Parent initializeUI() {
        BorderPane layout = new BorderPane();

        textArea = new TextArea();
        textArea.setFont(Font.font("Monaco", FontWeight.NORMAL, 14));

        textArea.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent keyEvent) {
                System.out.println(keyEvent.getCode());
                if(keyEvent.getCode() == KeyCode.TAB) {
                    int index = textArea.getCaretPosition();
                    System.out.println(index);
                    textArea.replaceText(index-1, index, "____");
                }
            }
        });
        layout.setTop(createMenuBar());
        layout.setCenter(textArea);

        this.terminal = new TextArea();
        this.terminal.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Monaco; -fx-highlight-fill: #00ff00; -fx-highlight-text-fill: #000000; -fx-text-fill: #00ff00;");


        VBox status = new VBox();
        status.getChildren().addAll(new Label("Terminal"), terminal, new Label("done."));

        terminal.setPrefHeight(100);
        layout.setBottom(status);

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
        Menu menuRun = new Menu("Run");

        MenuItem compile = new MenuItem(labels.getString("compile"));
        compile.setOnAction(this::compile);

        menuRun.getItems().add(compile);

        menuBar.getMenus().addAll(menuFile, menuEdit, menuView, menuRun);
        return menuBar;
    }

    private void compile(ActionEvent actionEvent) {
        try {
            String result = JavaCompiler.compile("/Users/pohjus/Desktop/Main.java");
            this.terminal.setText(result);
            this.terminal.setText(JavaCompiler.run("/Users/pohjus/Desktop/Main"));

        } catch (IOException e) {
            e.printStackTrace();
        }
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