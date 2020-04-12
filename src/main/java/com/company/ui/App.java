package com.company.ui;

import com.company.util.FileHandler;
import com.company.util.JavaCompiler;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.tools.Tool;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
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

        ToolBar toolBar = new ToolBar();
        Button button1 = new Button("+");
        Button button2 = new Button("-");

        Image img1 = new Image("run.png");
        ImageView img = new ImageView(img1);
        img.setFitHeight(12);
        img.setFitWidth(12);

        Button compile = new Button("", img);
        compile.setTooltip(new Tooltip("Compile and run..."));
        compile.setOnAction(this::compile);

        toolBar.getItems().addAll(compile, new Separator(), button1, button2);

        VBox up = new VBox();
        up.getChildren().addAll(createMenuBar(), toolBar);


        layout.setTop(up);

        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);


        this.terminal = new TextArea();
        this.terminal.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Monaco; -fx-highlight-fill: #00ff00; -fx-highlight-text-fill: #000000; -fx-text-fill: #00ff00;");

        BorderPane status = new BorderPane();
        status.setTop(new Label("terminal"));
        status.setCenter(terminal);

        splitPane.getItems().addAll(textArea, status);
        layout.setCenter(splitPane);

        return layout;

    }

    private Node createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");

        MenuItem open = new MenuItem(labels.getString("open"));
        open.setOnAction(this::openFileChooser);

        MenuItem save = new MenuItem(labels.getString("save"));
        save.setOnAction(this::saveFileChooser);

        MenuItem exit = new MenuItem("Exit");
        menuFile.getItems().addAll(new MenuItem("New"),
                open,
                save,
                new SeparatorMenuItem(),
                exit);

        exit.setOnAction(e -> System.exit(0));

        Menu menuEdit = new Menu("Edit");
        Menu menuRun = new Menu("Run");

        MenuItem compile = new MenuItem(labels.getString("compile"));
        compile.setOnAction(this::compile);

        menuRun.getItems().add(compile);

        menuBar.getMenus().addAll(menuFile, menuEdit, menuRun);
        return menuBar;
    }

    private void compile(ActionEvent actionEvent) {
        String path = stage.getTitle();
        JavaCompiler.compileAndRun(path, (content, errormsg) -> {
            this.terminal.setText(content);
        });
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
        Optional<File> file = Optional.ofNullable(fileChooser.showSaveDialog(this.stage));
        file.ifPresent((f) -> save(f));
    }

    private void open(File file) {
        FileHandler.open(file.getPath(), (content, errorMsg) -> {
            errorMsg.ifPresentOrElse(msg -> displayErrorMsg(msg), () -> {
                stage.setTitle(file.getAbsolutePath());
                textArea.setText(content);
            });
        });
    }

    private void save(File file) {
        FileHandler.save(textArea.getText(), file.getPath(), System.out::println);
    }


    public static void main(String... args) {
        launch(args);
    }

    private void displayErrorMsg(String msg) {
        System.out.println(msg);
    }

}


