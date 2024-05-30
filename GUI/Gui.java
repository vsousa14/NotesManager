package GUI;

import documentManager.*;
import exceptions.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class Gui extends Application {
    private TextArea textArea;
    private TreeView<String> treeView;
    private TextField searchField;

    @Override
    public void start(Stage primaryStage) {
        SplitPane splitPane = new SplitPane();

        treeView = createTreeView();
        ToolBar toolBar = createToolBar();
        textArea = new TextArea();
        textArea.setText("Bem-vindo ao Notes Manager! Selecione ou crie um documento para começar.");
        textArea.setDisable(true);  // Initially, disable the text area for homepage view

        VBox rightPane = new VBox(toolBar, textArea);
        rightPane.setVgrow(textArea, Priority.ALWAYS);

        VBox leftPane = new VBox(createHomepageButton(), createSearchBar(), treeView);
        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.3);

        Scene scene = new Scene(splitPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Notes Manager");
        primaryStage.show();
    }

    private VBox createHomepageButton() {
        VBox vbox = new VBox();
        Button homepageButton = new Button("Homepage");

        homepageButton.setOnAction(e -> showHomepage());

        vbox.getChildren().add(homepageButton);
        vbox.setSpacing(5);
        return vbox;
    }

    private HBox createSearchBar() {
        HBox hbox = new HBox();
        searchField = new TextField();
        searchField.setPromptText("Filtrar por categoria");
        Button createButton = new Button("Criar");

        createButton.setOnAction(e -> createNewDocument());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTreeView(newValue));

        hbox.getChildren().addAll(searchField, createButton);
        hbox.setSpacing(5);
        return hbox;
    }

    private void filterTreeView(String filter) {
        // Implement your filtering logic here
    }

    private void createNewDocument() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Criar Documento");
        dialog.setHeaderText("Nome do novo documento:");
        dialog.setContentText("Nome:");

        dialog.showAndWait().ifPresent(name -> {
            TreeItem<String> newDocument = new TreeItem<>("[D] " + name);
            treeView.getRoot().getChildren().add(newDocument);
        });
    }

    private void showHomepage() {
        textArea.setText("Bem-vindo ao Notes Manager! Selecione ou crie um documento para começar.");
        textArea.setDisable(true); // Disable editing in homepage view
    }

    private TreeView<String> createTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Notes Manager");
        rootItem.setExpanded(true);

        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(false);

        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue.isLeaf()) {
                // Handle note selection
                textArea.setText("Conteúdo de " + newValue.getValue());
                textArea.setDisable(false); // Enable editing when a note is selected
            } else if (newValue != null) {
                // Handle document selection
                textArea.setText("Conteúdo de " + newValue.getValue());
                textArea.setDisable(false); // Enable editing when a document is selected
            }
        });

        treeView.setOnContextMenuRequested(this::showContextMenu);

        return treeView;
    }

    private void showContextMenu(ContextMenuEvent event) {
        TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            ContextMenu contextMenu = new ContextMenu();
            if (selectedItem.getValue().contains("[D]")) {
                MenuItem createNoteItem = new MenuItem("Criar Nota");
                MenuItem createEncryptedNoteItem = new MenuItem("Criar Nota Criptografada");
                createNoteItem.setOnAction(e -> createNoteForDocument(selectedItem));
                createEncryptedNoteItem.setOnAction(e -> createEncryptedNoteForDocument(selectedItem));
                contextMenu.getItems().addAll(createNoteItem, createEncryptedNoteItem);
            }
            MenuItem deleteItem = new MenuItem("Eliminar");
            deleteItem.setOnAction(e -> deleteItem(selectedItem));
            contextMenu.getItems().add(deleteItem);

            contextMenu.show(treeView, event.getScreenX(), event.getScreenY());
        }
    }

    private void createNoteForDocument(TreeItem<String> documentItem) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Criar Nota");
        dialog.setHeaderText("Nome da nova nota:");
        dialog.setContentText("Nome:");

        dialog.showAndWait().ifPresent(name -> {
            TreeItem<String> newNote = new TreeItem<>("[N] "+name);
            documentItem.getChildren().add(newNote);
            documentItem.setExpanded(true);
        });
    }

    private void createEncryptedNoteForDocument(TreeItem<String> documentItem) {
        // Show password dialog
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Criar Nota Criptografada");
        passwordDialog.setHeaderText("Digite a senha para a nova nota criptografada:");
        passwordDialog.setContentText("Senha:");

        passwordDialog.showAndWait().ifPresent(password -> {
            // Create encrypted note
            TextInputDialog noteNameDialog = new TextInputDialog();
            noteNameDialog.setTitle("Criar Nota Criptografada");
            noteNameDialog.setHeaderText("Nome da nova nota criptografada:");
            noteNameDialog.setContentText("Nome:");

            noteNameDialog.showAndWait().ifPresent(name -> {
                TreeItem<String> newNote = new TreeItem<>("[*N] "+name);
                documentItem.getChildren().add(newNote);
                documentItem.setExpanded(true);
                // Store the password or use it accordingly
            });
        });
    }

    private void deleteItem(TreeItem<String> item) {
        if (item.getParent() != null) {
            item.getParent().getChildren().remove(item);
        }
    }

    private ToolBar createToolBar() {
        ToolBar toolBar = new ToolBar();

        ComboBox<String> fontBox = new ComboBox<>();
        fontBox.getItems().addAll(Font.getFamilies());
        fontBox.setValue("System");
        fontBox.setOnAction(e -> applyFont(fontBox.getValue()));

        ColorPicker colorPicker = new ColorPicker();
        colorPicker.setValue(Color.BLACK);
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

    private void applyColor(Color color) {
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
