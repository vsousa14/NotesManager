import java.awt.Color;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Main extends Application {

    private TextArea textArea;

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        ToolBar toolBar = createToolBar();
        textArea = new TextArea();

        root.setTop(toolBar);
        root.setCenter(textArea);

        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Rich Text Editor");
        primaryStage.show();
    }

    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar();

        ComboBox<String> fontBox = new ComboBox<>();
        fontBox.getItems().addAll(Font.getFamilies());
        fontBox.setValue("System");
        fontBox.setOnAction(e -> applyFont(fontBox.getValue()));

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(javafx.scene.paint.Color.BLACK);
        colorPicker.setOnAction(e -> applyColor(colorPicker.getValue()));

        Button boldButton = new Button("B");
        boldButton.setOnAction(e -> applyStyle("-fx-font-weight: bold;"));

        Button italicButton = new Button("I");
        italicButton.setOnAction(e -> applyStyle("-fx-font-style: italic;"));

        Button underlineButton = new Button("U");
        underlineButton.setOnAction(e -> applyStyle("-fx-underline: true;"));

        Button strikethroughButton = new Button("S");
        strikethroughButton.setOnAction(e -> applyStyle("-fx-strikethrough: true;"));

        toolBar.getItems().addAll(fontBox, colorPicker, boldButton, italicButton, underlineButton, strikethroughButton);
        return toolBar;
    }

    private void applyFont(String fontName) {
        textArea.setStyle("-fx-font-family: " + fontName + ";");
    }

    private void applyColor(javafx.scene.paint.Color color) {
        String hex = String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
        textArea.setStyle("-fx-text-fill: " + hex + ";");
    }

    private void applyStyle(String style) {
        String currentStyle = textArea.getStyle();
        if (currentStyle != null && !currentStyle.isEmpty()) {
            textArea.setStyle(currentStyle + " " + style);
        } else {
            textArea.setStyle(style);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
