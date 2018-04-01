package com.hereisalexius.v2r;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.text.ParsePosition;
import java.util.function.UnaryOperator;

public class App extends Application {

    //Refs to "root" controls
    private Stage primaryStage;
    private Scene scene;
    private BorderPane rootPane;

    //Refs to controls
    private ImageView view = new ImageView();
    private Button openButton = new Button("Open File");
    private Button exportButton = new Button("Export");
    private ComboBox<Integer> pageSelector;
    private ComboBox<Double> scaleSelector;
    private TextField dpiField;


    //Bind properties
    private StringProperty titleProp = new SimpleStringProperty("Vector 2 Raster");

    private VectorFileConverter vectorFile;

    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.titleProperty().bind(titleProp);
        initRoot();
        initComponents();
        this.primaryStage.show();
    }

    private void initRoot() {
        rootPane = new BorderPane();
        scene = new Scene(rootPane, 600, 400, Color.GRAY);
        primaryStage.setScene(scene);
    }

    private void initComponents() {
        initTop();
        rootPane.setCenter(new ScrollPane(view));
        initBottom();
    }

    private void initTop() {
        exportButton.setDisable(true);
        initPageSelector();
        exportButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                SnapshotParameters params = new SnapshotParameters();
                params.setFill(Color.TRANSPARENT);
                Image rotatedImage = view.snapshot(params, null);
                WritableImage image = new WritableImage(
                        rotatedImage.getPixelReader(),
                        (int) rotatedImage.getWidth(),
                        (int) rotatedImage.getHeight());

                FileChooser fc = new FileChooser();
                fc.setTitle("Export to JPG");
                fc.setInitialDirectory(new File(System.getProperty("user.home")));
                fc.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("JPEG", "*.jpg")
                );
                File file = fc.showSaveDialog(primaryStage);
                if (file != null) {

                    vectorFile.export(file,
                            pageSelector.getSelectionModel().getSelectedItem(),
                            scaleSelector.getSelectionModel().getSelectedItem(),
                            Double.parseDouble(dpiField.getText().replaceAll("\\s","")));
                }
            }
        });
        openButton.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                FileChooser fc = new FileChooser();
                fc.setTitle("Open VG file");
                fc.setInitialDirectory(new File(System.getProperty("user.home")));
                fc.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("SVG", "*.svg"),
                        new FileChooser.ExtensionFilter("PDF", "*.pdf")
                );

                File f = fc.showOpenDialog(primaryStage);

                if (f != null) {
                    if (f.getAbsolutePath().toLowerCase().endsWith(".svg")) {
                        vectorFile = new SVGFileConverter(f);
                    } else {
                        vectorFile = new PDFFileConverter(f);
                    }

                    //view.setImage(vectorFile.getImage(1));
                    pageSelector.getItems().clear();
                    for (int i = 1; i <= vectorFile.getPageCount(); i++) {
                        pageSelector.getItems().add(i);
                    }
                    pageSelector.getSelectionModel().selectFirst();

                    exportButton.setDisable(false);
                }
            }
        });

        ToolBar toolBar = new ToolBar(openButton, exportButton, new Label("Page : "), pageSelector);
        rootPane.setTop(toolBar);
    }

    private void initBottom() {
        Button rotateRight = new Button("<Rotate");
        Button rotateLeft = new Button("Rotate>");
        rotateRight.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (view.getImage() != null) {
                    view.setRotate(view.getRotate() - 90);
                }
            }
        });
        rotateLeft.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (view.getImage() != null) {
                    view.setRotate(view.getRotate() + 90);
                }
            }
        });

        initDpiField();
        initScaleSelector();

        ToolBar toolBar2 = new ToolBar(rotateRight, new Label("Scale : "), scaleSelector,new Label("Resolution : "), dpiField, rotateLeft);
        rootPane.setBottom(toolBar2);
    }

    private void initScaleSelector() {
        scaleSelector = new ComboBox<>();
        scaleSelector.getItems()
                .addAll(0.25,
                        0.33,
                        0.5,
                        0.67,
                        0.75,
                        0.8,
                        0.9,
                        1.0,
                        1.1,
                        1.25,
                        1.5,
                        1.75,
                        2.0,
                        2.5,
                        3.0,
                        4.0);

        scaleSelector.getSelectionModel().select(7);
        scaleSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                if (vectorFile != null) {
                    try {
                        Image img = vectorFile.getImage(pageSelector.getSelectionModel().getSelectedItem(), newValue, 0);
                        view.setImage(img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initDpiField() {
        dpiField = new TextField("144");
        NumberStringFilteredConverter converter = new NumberStringFilteredConverter();
        final TextFormatter<Number> formatter = new TextFormatter<>(
                converter,
                0,
                converter.getFilter()
        );

        dpiField.setTextFormatter(formatter);

        formatter.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if(Double.parseDouble(newValue.toString().replaceAll("\\s",""))>5000){
                        dpiField.setText("5000");
                    }

                    if (vectorFile != null) {
                        try {
                            Image img = vectorFile.getImage(pageSelector.getSelectionModel().getSelectedItem(),
                                    scaleSelector.getSelectionModel().getSelectedItem(),
                                    Double.parseDouble(newValue.toString().replaceAll("\\s","")));
                            view.setImage(img);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        dpiField.setText("144");

    }

    private void initPageSelector() {
        pageSelector = new ComboBox<>();
        pageSelector.getItems().add(1);
        pageSelector.getSelectionModel().selectFirst();
        pageSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
            @Override
            public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
                if (vectorFile != null && !pageSelector.getItems().isEmpty()) {
                    try {
                        Image img = vectorFile.getImage(newValue,
                                scaleSelector.getSelectionModel().getSelectedItem(),
                                Double.parseDouble(dpiField.getText().replaceAll("\\s","")));
                        view.setImage(img);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

    private class NumberStringFilteredConverter extends NumberStringConverter {


        public UnaryOperator<TextFormatter.Change> getFilter() {
            return change -> {
                String newText = change.getControlNewText();
                if (newText.isEmpty()) {
                    return change;
                }

                ParsePosition parsePosition = new ParsePosition(0);
                Object object = getNumberFormat().parse(newText, parsePosition);
                if (object == null || parsePosition.getIndex() < newText.length()) {
                    return null;
                } else {
                    return change;
                }
            };
        }
    }
}