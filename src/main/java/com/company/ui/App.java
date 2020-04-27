package com.company.ui;

import com.company.preferences.MyState;
import com.company.preferences.PrefsData;
import com.company.timestamp.TimeHandler;
import com.company.util.Animations;
import com.company.util.FileHandler;
import com.company.util.JavaCompiler;
import com.company.preferences.PreferencesHandler;
import javafx.animation.ParallelTransition;
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
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class App extends Application {


    private FileHandler fileHandler;

    private TextArea textArea;

    private TextField searchTextField;

    private Locale locale = Locale.getDefault();
    private ResourceBundle labels;

    private Stage stage;
    private TextArea terminal;

    private PreferencesHandler prefsHandler;
    private PrefsData prefsData;

    private MenuItem save;

    // Restore preferences
    @Override
    public void init() {

        prefsHandler = new PreferencesHandler();
        prefsHandler.restorePreferences();
        prefsData = prefsHandler.getPreferencesData();

        TimeHandler.getInstance().restoreTimeStamp();
        TimeHandler.getInstance().setStart(System.currentTimeMillis());
    }

    // Save preferences
    @Override
    public void stop() {
        prefsHandler.savePreferences();
        TimeHandler.getInstance().setEnd(System.currentTimeMillis());
        System.out.println("hep");
        TimeHandler.getInstance().saveTimeStamp();
    }

    @Override
    public void start(Stage stage) {
        this.fileHandler = FileHandler.getInstance();

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
        if(keyEvent.getCode() == KeyCode.TAB && !this.prefsData.isTab()) {
            // tabulaattorimerkki (\t) on nyt tekstialueessa.

            // haetaan kursorin sijainti
            int index = textArea.getCaretPosition();

            // Korvataan yksi merkki, juuri tehty tabulaattori...
            textArea.replaceText(index-1, index, "____");
        }
    }

    private void search(String text) {
        int index = this.textArea.getText().indexOf(text);
        textArea.selectRange(index, index + text.length());
    }

    private Parent createToolBar() {
        ToolBar toolBar = new ToolBar();
        searchTextField = new TextField();
        searchTextField.setPromptText(labels.getString("search"));

        searchTextField.setOnKeyPressed(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.ENTER) {
                search(searchTextField.getText());
            }
        });

        // Create compile button for toolbar with icon
        Image img1 = new Image("run.png");
        ImageView img = new ImageView(img1);
        img.setFitHeight(12);
        img.setFitWidth(12);
        Button compile = new Button("", img);
        Light.Distant light = new Light.Distant();
        light.setAzimuth(-135.0f);
        Lighting l = new Lighting();
        l.setLight(light);
        l.setSurfaceScale(5.0f);
        compile.setEffect(l);

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

        Button next = new Button(labels.getString("next"));
        Button prev = new Button(labels.getString("prev"));

        next.setOnAction(this::next);
        prev.setOnAction(this::prev);
        toolBar.getItems().addAll(compile, new Separator(),
                fontSelection,
                sizeTextField,
                foregroundColor,
                tab, spaces,
                new Separator(),
                searchTextField,
                prev,
                next);
        return toolBar;
    }

    private void next(ActionEvent e) {
        IndexRange selection = textArea.getSelection();
        int start = selection.getEnd();

        int index = this.textArea.getText().indexOf(searchTextField.getText(), start);
        if(index != -1) {
            textArea.selectRange(index, index + searchTextField.getText().length());
        } else {
            // TODO
            displayErrorMsg("Last occurance of the word");
        }
    }

    private void prev(ActionEvent e) {
        IndexRange selection = textArea.getSelection();
        int start = selection.getStart() - 1;

        int index = this.textArea.getText().lastIndexOf(searchTextField.getText(), start);
        if(index != -1) {
            textArea.selectRange(index, index + searchTextField.getText().length());
        } else {
            displayErrorMsg("Nothing to search for.");
        }



    }

    private void toggleTabsVsSpaces(ActionEvent actionEvent) {
        RadioButton input = (RadioButton) actionEvent.getSource();
        if (input.getText().equals(labels.getString("tab"))) {
            this.prefsData.setTab(true);
        } else {
            this.prefsData.setTab(false);
        }

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
        TabPane tabPane = new TabPane();


        BorderPane layout = new BorderPane();
        textArea = new TextArea();
        System.out.println(this.prefsData.getCSS());
        textArea.setStyle(this.prefsData.getCSS());
        textArea.setOnKeyPressed(this::replaceTabsWithSpaces);

        layout.setTop(new VBox(createMenuBar(), createToolBar()));
        layout.setCenter(createSplitPane());

        Tab tab1 = new Tab("Editor", layout);
        Tab tab2 = new Tab("Diagram", new Label("diagram"));

        tabPane.getTabs().addAll(tab1, tab2);

        return tabPane;

    }

    private Node createMenuBar() {
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu(labels.getString("file"));

        MenuItem open = new MenuItem(labels.getString("open"));
        open.setOnAction(this::openFileChooser);
        open.setAccelerator(new KeyCodeCombination(KeyCode.O, KeyCombination.SHORTCUT_DOWN));

        MenuItem saveAs = new MenuItem(labels.getString("saveas"));
        saveAs.setOnAction(this::saveFileChooser);
        saveAs.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN, KeyCombination.SHIFT_DOWN));

        save = new MenuItem(labels.getString("save"));
        save.setOnAction(e -> this.save(new File(this.fileHandler.getFilePath())));
        save.setAccelerator(new KeyCodeCombination(KeyCode.S, KeyCombination.SHORTCUT_DOWN));

        if(!this.fileHandler.getIsFileOpen()) {
            save.setDisable(true);
        }

        MenuItem newMenuItem = new MenuItem(labels.getString("new"));
        newMenuItem.setAccelerator(new KeyCodeCombination(KeyCode.N, KeyCombination.SHORTCUT_DOWN));


        MenuItem exit = new MenuItem(labels.getString("exit"));
        menuFile.getItems().addAll(newMenuItem,
                open,
                saveAs,
                save,
                new SeparatorMenuItem(),
                new MenuItem(labels.getString("settings")),
                new SeparatorMenuItem(),
                exit);

        exit.setOnAction(e -> {
            this.stop();
            System.exit(0);
        });

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

    private void runJava() {
        String filePath = FileHandler.getInstance().getFilePath();
        JavaCompiler compiler = JavaCompiler.getInstance();
        compiler.setFile(filePath);
        compiler.run(((content, errorMsg) -> {
            // TODO errormsg
            terminal.setText(content);
        }));
    }
    private void compile(ActionEvent actionEvent) {
        String filePath = FileHandler.getInstance().getFilePath();

        // If file exists
        if(Files.exists(Paths.get(filePath))) {
            JavaCompiler compiler = JavaCompiler.getInstance();
            compiler.setFile(filePath);
            compiler.compile((content, errorMsg) -> {
                System.out.println(content);
                // If javac compiler errors
                if(!content.equals("")) {
                    terminal.setText(content);
                } else {
                    runJava();
                }
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
        Optional<File> file = Optional.ofNullable(fileChooser.showSaveDialog(this.stage));
        file.ifPresent((f) -> save(f));
    }

    private void open(File file) {
        this.fileHandler.setFilePath(file.getAbsolutePath());
        this.fileHandler.open((content, errorMsg) -> {
            errorMsg.ifPresentOrElse(msg -> displayErrorMsg(msg), () -> {
                stage.setTitle(this.fileHandler.getFilePath());
                textArea.setText(content);
                save.setDisable(false);
            });
        });
    }

    private void save(File file) {
        this.fileHandler.setFilePath(file.getAbsolutePath());

        this.fileHandler.save(textArea.getText(), msg -> {
            if(msg.isEmpty()) {
                stage.setTitle(this.fileHandler.getFilePath());

                this.save.setDisable(false);
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


