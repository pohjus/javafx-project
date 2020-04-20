package com.company.ui;

import com.company.preferences.PrefsData;
import com.company.util.Animations;
import com.company.util.FileHandler;
import com.company.util.JavaCompiler;
import com.company.util.PreferencesHandler;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.tools.Tool;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Key;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.paint.Color;

public class App extends Application {

    private TextArea textArea;

    private Locale locale = Locale.getDefault();
    private ResourceBundle labels;

    private Stage stage;
    private TextArea terminal;

    private PreferencesHandler prefsHandler;
    private PrefsData prefsData;

    // Restore preferences
    @Override
    public void init() {
        prefsHandler = new PreferencesHandler();
        prefsHandler.restorePreferences();
        prefsData = prefsHandler.getPreferencesData();
    }

    // Save preferences
    @Override
    public void stop() {
        prefsHandler.savePreferences();
    }

    @Override
    public void start(Stage stage) {
        this.stage = stage;

        labels = ResourceBundle.getBundle("ui", locale);
        stage.setTitle(labels.getString("title"));

        Parent layout = initializeUI();

        Scene content = new Scene(layout, 1000, 480);
        content.getStylesheets().add("style.css");

        stage.setScene(content);
        stage.show();
    }

    /**
     * Replaces tabs with four spaces in the textarea
     */
    private void replaceTabsWithSpaces(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.TAB) {
            // tabulaattorimerkki (\t) on nyt tekstialueessa.

            // haetaan kursorin sijainti
            int index = textArea.getCaretPosition();

            // Korvataan yksi merkki, juuri tehty tabulaattori...
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

        String color = this.prefsData.getFontColor();
        Color prefColor = Color.web(color);

        ColorPicker foregroundColor = new ColorPicker(prefColor);

        foregroundColor.setTooltip(new Tooltip("Text color"));
        foregroundColor.setOnAction(e -> {
            Color c = foregroundColor.getValue();
            this.prefsData.setFontColor(this.prefsData.colorToString(c));

            this.textArea.setStyle(this.prefsData.getCSS());
        });



        var list = Font.getFontNames();

        ComboBox<String> fontSelection = new ComboBox<>();
        fontSelection.getItems().addAll(Font.getFontNames());

        fontSelection.setValue(this.prefsData.getFontName());

        fontSelection.setOnAction(actionEvent -> {
            this.prefsData.setFontName(fontSelection.getValue());
            this.textArea.setStyle(this.prefsData.getCSS());
        });

        // Tabs vs spaces
        RadioButton tab = new RadioButton(labels.getString("tab"));
        RadioButton spaces = new RadioButton(labels.getString("spaces"));
        tab.setSelected(this.prefsData.isTab());
        spaces.setSelected(!this.prefsData.isTab());

        ToggleGroup group = new ToggleGroup();
        spaces.setToggleGroup(group);
        tab.setToggleGroup(group);


        tab.setOnAction(this::toggleTabsVsSpaces);
        spaces.setOnAction(this::toggleTabsVsSpaces);

        // Create size font
        TextField sizeTextField = new TextField("" + prefsData.getFontSize());
        sizeTextField.setMinWidth(50);
        sizeTextField.setPrefWidth(50);
        sizeTextField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) {
                String fontSizeString = sizeTextField.getText();
                try {
                    int size = Integer.parseInt(fontSizeString);
                    if(size > 10 && size <= 100) {
                        prefsData.setFontSize(size);
                        textArea.setStyle(prefsData.getCSS());
                    } else {
                        throw new NumberFormatException();
                    }
                } catch(NumberFormatException e) {
                    displayErrorMsg("Please give valid number.");
                }
            }
        });

        toolBar.getItems().addAll(compile, new Separator(),
                fontSelection,
                sizeTextField,
                foregroundColor,
                tab, spaces,
                new Separator(),
                searchTextField,
                new Button("<"),
                new Button(">"));
        return toolBar;
    }

    private void toggleTabsVsSpaces(ActionEvent actionEvent) {
        RadioButton input = (RadioButton) actionEvent.getSource();
        if (input.getText().equals(labels.getString("tab"))) {
            this.prefsData.setTab(true);
        } else {
            this.prefsData.setTab(false);
        }

        System.out.println(this.prefsData);

    }


    private Parent createSplitPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setOrientation(Orientation.VERTICAL);

        this.terminal = new TextArea();
        this.terminal.setStyle("-fx-control-inner-background:#000000; -fx-font-family: Monaco; " +
                "-fx-highlight-fill: #00ff00; -fx-highlight-text-fill: #000000; " +
                "-fx-text-fill: #00ff00;");
        this.terminal.setEditable(false);

        BorderPane status = new BorderPane();
        status.setTop(new Label(labels.getString("terminal")));
        status.setCenter(terminal);
        splitPane.getItems().addAll(textArea, status);
        return splitPane;
    }

    private Parent initializeUI() {
        BorderPane layout = new BorderPane();
        textArea = new TextArea();
        System.out.println(this.prefsData.getCSS());
        textArea.setStyle(this.prefsData.getCSS());
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
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));

        MenuItem save = new MenuItem(labels.getString("save"));
        save.setOnAction(this::saveFileChooser);
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));

        MenuItem newMenuItem = new MenuItem(labels.getString("new"));
        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));


        MenuItem exit = new MenuItem(labels.getString("exit"));
        menuFile.getItems().addAll(newMenuItem,
                open,
                save,
                new MenuItem(labels.getString("settings")),
                new SeparatorMenuItem(),
                exit);

        exit.setOnAction(e -> System.exit(0));

        Menu menuEdit = new Menu(labels.getString("edit"));

        MenuItem copy = new MenuItem(labels.getString("copy"));
        copy.setAccelerator(new KeyCodeCombination(KeyCode.C, KeyCombination.SHORTCUT_DOWN));

        MenuItem paste = new MenuItem(labels.getString("paste"));
        paste.setAccelerator(new KeyCodeCombination(KeyCode.V, KeyCombination.SHORTCUT_DOWN));

        MenuItem cut = new MenuItem(labels.getString("cut"));
        cut.setAccelerator(new KeyCodeCombination(KeyCode.X, KeyCombination.SHORTCUT_DOWN));


        cut.setOnAction(this::cut);

        copy.setOnAction(this::copy);
        paste.setOnAction(this::paste);

        menuEdit.getItems().addAll(cut, copy, paste);

        Menu menuRun = new Menu(labels.getString("run"));

        MenuItem compile = new MenuItem(labels.getString("compile"));
        compile.setOnAction(this::compile);

        menuRun.getItems().add(compile);

        Menu menuAbout = new Menu(labels.getString("about"));
        MenuItem menuAboutApp = new MenuItem(labels.getString("aboutApp"));
        menuAboutApp.setOnAction(this::about);
        menuAbout.getItems().add(menuAboutApp);

        menuBar.getMenus().addAll(menuFile, menuEdit, menuRun, menuAbout);
        return menuBar;
    }

    private void about(ActionEvent e) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(labels.getString("title"));
        alert.setHeaderText(labels.getString("title"));
        alert.setContentText(labels.getString("coder"));

        alert.showAndWait();
    }
    private void copy(ActionEvent e) {
        textArea.copy();
    }

    private void cut(ActionEvent e) {
        textArea.cut();
    }

    private void paste(ActionEvent e) {
        textArea.paste();
    }

    private void compile(ActionEvent actionEvent) {
        String path = stage.getTitle();
        if(Files.exists(Paths.get(path))) {
            JavaCompiler.compileAndRun(path, (content, errormsg) -> {
                this.terminal.setText(content);
            });
        } else {
            displayErrorMsg("Please save your file first.");
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
        FileHandler.save(textArea.getText(), file.getPath(), msg -> {
            if(msg.isEmpty()) {
                stage.setTitle(file.getAbsolutePath());
            } else {
                displayErrorMsg(msg.get());
            }
        });
    }


    public static void main(String... args) {
        launch(args);
    }

    private void displayErrorMsg(String msg) {
        System.out.println(msg);
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Dialog");
        alert.setHeaderText(msg);
        alert.showAndWait();
    }

}


