package com.company.ui;

import com.company.util.Animations;
import com.company.util.FileHandler;
import com.company.util.JavaCompiler;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
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
import javafx.util.Duration;

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

    /**
     * Replaces tabs with four spaces in the textarea
     */
    private void replaceTabsWithSpaces(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.TAB) {
            int index = textArea.getCaretPosition();
            textArea.replaceText(index-1, index, "____");
        }
    }

    private Parent createToolBar() {
        ToolBar toolBar = new ToolBar();
        TextField searchTextField = new TextField();
        searchTextField.setPromptText(labels.getString("search"));

        // Create compile button for toolbar with icon
        Image img1 = new Image("run.png");
        ImageView img = new ImageView(img1);
        img.setFitHeight(12);
        img.setFitWidth(12);
        Button compile = new Button("", img);

        compile.setTooltip(new Tooltip(labels.getString("compile")));
        compile.setOnAction((e) -> {
            ParallelTransition p = Animations.addAnimationToButton(compile);
            // play the animation and compile
            p.play();
            this.compile(e);
        });


        // Create color picker
        ColorPicker colorPicker1 = new ColorPicker();

        // Create font selection
        ComboBox<String> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(
                "Monaco",
                "Inconsolita",
                "Courier"
        );
        comboBox.setValue("Monaco");

        // Create size font
        TextField sizeTextField = new TextField("12");
        sizeTextField.setMinWidth(50);
        sizeTextField.setPrefWidth(50);
        toolBar.getItems().addAll(compile, new Separator(), comboBox, sizeTextField, colorPicker1, new Separator(), searchTextField, new Button("<"), new Button(">"));
        return toolBar;
    }

    private Parent createSplitPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        this.terminal = new TextArea();
        this.terminal.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Monaco; " +
                "-fx-highlight-fill: #00ff00; -fx-highlight-text-fill: #000000; " +
                "-fx-text-fill: #00ff00;");

        BorderPane status = new BorderPane();
        status.setTop(new Label(labels.getString("terminal")));
        status.setCenter(terminal);
        splitPane.getItems().addAll(textArea, status);
        return splitPane;
    }

    private Parent initializeUI() {
        BorderPane layout = new BorderPane();
        textArea = new TextArea();

        // TODO fix font
        textArea.setFont(Font.font("Monaco", FontWeight.NORMAL, 14));
        textArea.setOnKeyPressed(this::replaceTabsWithSpaces);

        layout.setTop(new VBox(createMenuBar(), createToolBar()));
        layout.setCenter(createSplitPane());

        return layout;

    }

    private Node createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu(labels.getString("file"));

        MenuItem open = new MenuItem(labels.getString("open"));
        open.setOnAction(this::openFileChooser);

        MenuItem save = new MenuItem(labels.getString("save"));
        save.setOnAction(this::saveFileChooser);

        MenuItem exit = new MenuItem(labels.getString("exit"));
        menuFile.getItems().addAll(new MenuItem(labels.getString("new")),
                open,
                save,
                new MenuItem(labels.getString("settings")),
                new SeparatorMenuItem(),
                exit);

        exit.setOnAction(e -> System.exit(0));

        Menu menuEdit = new Menu(labels.getString("edit"));
        Menu menuRun = new Menu(labels.getString("run"));

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


