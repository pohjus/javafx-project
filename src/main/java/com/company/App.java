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
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {

    private TextArea textArea;

    Locale locale = new Locale("fi", "FI");
    private ResourceBundle labels;

    @Override
    public void start(Stage stage) {
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
        open.setOnAction(this::open);

        menuFile.getItems().addAll(new MenuItem("New"),
                open,
                new MenuItem("Save..."),
                new SeparatorMenuItem(),
                new MenuItem("Exit"));

        Menu menuEdit = new Menu("Edit");
        Menu menuView = new Menu("View");
        menuBar.getMenus().addAll(menuFile, menuEdit, menuView);
        return menuBar;
    }

    private void open(ActionEvent actionEvent) {
        System.out.println(Thread.currentThread().getName());
        FileHandler.open("/Users/pohjus/Desktop/temp.txt", (content) -> {

            System.out.println(Thread.currentThread().getName());

            textArea.setText(content);
        });
    }

    public static void main(String... args) {
        launch(args);
    }

}