package GUI;

import documentManager.*;
import exceptions.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import utils.CustomTreeItem;

import java.util.ArrayList;

public class Gui extends Application {
    private TextArea textArea;
    private TreeView<String> treeView;
    private TextField searchField;
    private DocumentManager documentManager;
    ArrayList<Note> noteList = new ArrayList<>();
    ArrayList<EncryptedNote> encryptedNoteList = new ArrayList<>();

    @Override
    public void start(Stage primaryStage) {
        SplitPane splitPane = new SplitPane();
        documentManager = new DocumentManager();

        treeView = createTreeView();
        ToolBar toolBar = createToolBar();
        textArea = new TextArea();
        textArea.setText("Bem-vindo ao Notes Manager! Selecione ou crie um documento para começar.");
        textArea.setDisable(true);  // Initially, disable the text area for homepage view

        textArea.setOnKeyReleased(event -> {
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            System.out.println("ITEM SELECIONADO -> " + selectedItem);
            if (selectedItem != null && selectedItem instanceof CustomTreeItem) {
                CustomTreeItem<String> treeItem = (CustomTreeItem<String>) selectedItem;
                Document document = treeItem.getDocument();
                
                if (document != null) {
                    document.setContent(textArea.getText());
                    treeItem.setDocument(document);
                    System.out.println("Conteúdo atualizado para o item selecionado.");
                } else {
                    System.out.println("Erro: Documento não encontrado.");
                }
            }
        });
        
        VBox rightPane = new VBox(toolBar, textArea);
        rightPane.setVgrow(textArea, Priority.ALWAYS);

        VBox leftPane = new VBox(createHomepageButton(), createSearchBar(), treeView);
        splitPane.getItems().addAll(leftPane, rightPane);
        splitPane.setDividerPositions(0.3);

        Scene scene = new Scene(splitPane, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Notes Manager");
        primaryStage.show();

        leftPane.setOnMouseClicked(event -> {
            if (treeView.getSelectionModel().getSelectedItem() != null) {
                // Verifica se o clique foi fora da TreeView
                treeView.getSelectionModel().clearSelection();
            }
        });
    }

    private VBox createHomepageButton() {
        VBox vbox = new VBox();
        Button homepageButton = new Button("Homepage");

        homepageButton.setMaxWidth(Double.MAX_VALUE);

        homepageButton.setOnAction(e -> showHomepage());

        vbox.getChildren().add(homepageButton);
        vbox.setSpacing(5);
        vbox.setMargin(homepageButton, new Insets(10, 0, 10, 0));
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
            int documentId = documentManager.getDocuments().size() + 1; // Generate new ID
            Document newDocument = new Document(documentId, name, "Conteúdo do documento " + name);
            documentManager.addDocument(newDocument);

            CustomTreeItem<String> newDocumentItem = new CustomTreeItem<>("[D] " + name, newDocument);
            treeView.getRoot().getChildren().add(newDocumentItem);
        });
    }

    private void showHomepage() {
        treeView.getSelectionModel().clearSelection();
        textArea.setText("Bem-vindo ao Notes Manager! Selecione ou crie um documento para começar.");
        textArea.setDisable(true); // Disable editing in homepage view
    }

    private TreeView<String> createTreeView() {
        TreeItem<String> rootItem = new TreeItem<>("Notes Manager");
        rootItem.setExpanded(true);
    
        TreeView<String> treeView = new TreeView<>(rootItem);
        treeView.setShowRoot(false);
    
        treeView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && newValue instanceof CustomTreeItem) {
                CustomTreeItem<String> selectedItem = (CustomTreeItem<String>) newValue;
                Document doc = selectedItem.getDocument();
    
                if (doc instanceof EncryptedNote) {
                    handleEncryptedNoteSelection((EncryptedNote) doc);
                } else if (doc instanceof Note) {
                    // Handle regular note selection
                    System.out.println("NOTA SELECIONADA");
                    textArea.setText(doc.getContent());
                    textArea.setDisable(false); // Enable editing when a note or document is selected
                } else {
                    // Handle document selection
                    System.out.println("DOCUMENTO SELECIONADO");
                    textArea.setText(doc.getContent());
                    textArea.setDisable(false); // Enable editing when a note or document is selected
                }
            }
        });
    
        treeView.setOnContextMenuRequested(this::showContextMenu);
    
        return treeView;
    }
    

    private void handleEncryptedNoteSelection(EncryptedNote encryptedNote) {
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Nota Criptografada");
        passwordDialog.setHeaderText("Digite a senha para visualizar o conteúdo:");
        passwordDialog.setContentText("Senha:");

        passwordDialog.showAndWait().ifPresent(password -> {
            try {
                String content = encryptedNote.desencriptar(password);
                textArea.setText(content);
                textArea.setDisable(false); // Enable editing when the password is correct
            } catch (wrongPasswordException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText("Senha incorreta");
                alert.setContentText("A senha que você digitou está incorreta. Tente novamente.");
                alert.showAndWait();
            }
        });
    }
    
  

    private void showContextMenu(ContextMenuEvent event) {
        treeView.setContextMenu(null);
    
        TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
        if (selectedItem != null && selectedItem instanceof CustomTreeItem) {
            CustomTreeItem<String> customSelectedItem = (CustomTreeItem<String>) selectedItem;
            Document document = customSelectedItem.getDocument();
            ContextMenu contextMenu = new ContextMenu();
            MenuItem createNoteItem = new MenuItem("Criar Nota");
            MenuItem createEncryptedNoteItem = new MenuItem("Criar Nota Criptografada");
            createNoteItem.setOnAction(e -> createNoteForDocument(customSelectedItem));
            createEncryptedNoteItem.setOnAction(e -> createEncryptedNoteForDocument(customSelectedItem));
            contextMenu.getItems().addAll(createNoteItem, createEncryptedNoteItem);
    
            MenuItem deleteItem = new MenuItem("Eliminar");
            deleteItem.setOnAction(e -> deleteItem(selectedItem));
            contextMenu.getItems().add(deleteItem);
    
            treeView.setContextMenu(contextMenu);
            contextMenu.show(treeView, event.getScreenX(), event.getScreenY());
        }
    }
    

    private void createNoteForDocument(CustomTreeItem<String> documentItem) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Criar Nota");
        dialog.setHeaderText("Nome da nova nota:");
        dialog.setContentText("Nome:");
    
        dialog.showAndWait().ifPresent(name -> {
            int noteId = (int) (Math.random() * 8000) + 1; // Generate new ID
    
            // Retrieve the associated document
            Document document = documentItem.getDocument();
    
            // Create and associate the note with the document
            Note newNote = new Note(noteId, name, "Conteúdo da nota " + name, document);
            noteList.add(newNote);
            documentItem.getChildren().add(new CustomTreeItem<>("[N] " + name, newNote));
        });
    }
    
    private void createEncryptedNoteForDocument(CustomTreeItem<String> documentItem) {
        TextInputDialog passwordDialog = new TextInputDialog();
        passwordDialog.setTitle("Criar Nota Criptografada");
        passwordDialog.setHeaderText("Digite a senha para a nova nota criptografada:");
        passwordDialog.setContentText("Senha:");
    
        passwordDialog.showAndWait().ifPresent(password -> {
            TextInputDialog noteNameDialog = new TextInputDialog();
            noteNameDialog.setTitle("Criar Nota Criptografada");
            noteNameDialog.setHeaderText("Nome da nova nota criptografada:");
            noteNameDialog.setContentText("Nome:");
    
            noteNameDialog.showAndWait().ifPresent(name -> {
                int noteId = (int) (Math.random() * 8000) + 1; // Generate new ID
    
                // Retrieve the associated document
                Document document = documentItem.getDocument();
    
                // Create and associate the encrypted note with the document
                EncryptedNote newEncryptedNote = new EncryptedNote(noteId, name, "Conteúdo da nota " + name, document, password);
                encryptedNoteList.add(newEncryptedNote);
                documentItem.getChildren().add(new CustomTreeItem<>("[*N] " + name, newEncryptedNote));
            });
        });
    }
    

    private void deleteItem(TreeItem<String> item) {
        if (item.getParent() != null) {
            item.getParent().getChildren().remove(item);
            if (treeView.getRoot().getChildren().isEmpty()) {
                showHomepage();
            }
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
