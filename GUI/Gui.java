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
import utils.LanguageManager;
import utils.PasswordDialog;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Optional;

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
        try {
            documentManager = DocumentManager.loadData("data.ser");
        } catch (IOException | ClassNotFoundException e) {
            documentManager = new DocumentManager();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("notes.ser"))) {
            noteList = (ArrayList<Note>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            noteList = new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("encryptedNotes.ser"))) {
            encryptedNoteList = (ArrayList<EncryptedNote>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            encryptedNoteList = new ArrayList<>();
        }

        LanguageManager.loadLanguage("en_us");

        treeView = createTreeView();
        ToolBar toolBar = createToolBar();
        textArea = new TextArea();
        textArea.setText(LanguageManager.get("welcome"));
        textArea.setDisable(true);  // Initially, disable the text area for homepage view
        textArea.setOnKeyReleased(event -> {
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null && selectedItem instanceof CustomTreeItem) {
                CustomTreeItem<String> treeItem = (CustomTreeItem<String>) selectedItem;
                Document document = treeItem.getDocument();

                if (document != null) {
                    document.setContent(textArea.getText());
                    treeItem.setDocument(document);
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
                treeView.getSelectionModel().clearSelection();
            }
        });
    }

    @Override
    public void stop() {
        saveData();
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
        searchField.setPromptText(LanguageManager.get("filterby"));
        Button createButton = new Button(LanguageManager.get("create"));

        createButton.setOnAction(e -> createNewDocument());

        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTreeView(newValue));

        hbox.getChildren().addAll(searchField, createButton);
        hbox.setSpacing(5);
        return hbox;
    }

    private void filterTreeView(String filter) {
        TreeItem<String> root = treeView.getRoot();
        if (filter == null || filter.isEmpty()) {
            restoreTreeItems(root);
        } else {
            applyFilter(root, filter.toLowerCase());
        }
    }
    
    private void restoreTreeItems(TreeItem<String> root) {
        root.getChildren().clear();
        for (Document document : documentManager.getDocuments().values()) {
            CustomTreeItem<String> documentItem = new CustomTreeItem<>("[D] " + document.getTitle(), document);
            root.getChildren().add(documentItem);
            for (Note note : noteList) {
                if (note.getDocument().getId() == document.getId()) {
                    documentItem.getChildren().add(new CustomTreeItem<>("[N] " + note.getTitle(), note));
                }
            }
            for (EncryptedNote encryptedNote : encryptedNoteList) {
                if (encryptedNote.getDocument().getId() == document.getId()) {
                    documentItem.getChildren().add(new CustomTreeItem<>("[*N] " + encryptedNote.getTitle(), encryptedNote));
                }
            }
        }
    }
    
    private void applyFilter(TreeItem<String> root, String filter) {
        root.getChildren().clear();
        for (Document document : documentManager.getDocuments().values()) {
            if (document.getTitle().toLowerCase().contains(filter)) {
                CustomTreeItem<String> documentItem = new CustomTreeItem<>("[D] " + document.getTitle(), document);
                root.getChildren().add(documentItem);
            } else {
                CustomTreeItem<String> documentItem = new CustomTreeItem<>("[D] " + document.getTitle(), document);
                boolean hasMatchingChild = false;
                for (Note note : noteList) {
                    if (note.getDocument().getId() == document.getId() && note.getTitle().toLowerCase().contains(filter)) {
                        documentItem.getChildren().add(new CustomTreeItem<>("[N] " + note.getTitle(), note));
                        hasMatchingChild = true;
                    }
                }
                for (EncryptedNote encryptedNote : encryptedNoteList) {
                    if (encryptedNote.getDocument().getId() == document.getId() && encryptedNote.getTitle().toLowerCase().contains(filter)) {
                        documentItem.getChildren().add(new CustomTreeItem<>("[*N] " + encryptedNote.getTitle(), encryptedNote));
                        hasMatchingChild = true;
                    }
                }
                if (hasMatchingChild) {
                    root.getChildren().add(documentItem);
                }
            }
        }
    }

    private void createNewDocument() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(LanguageManager.get("createDocument"));
        dialog.setHeaderText(LanguageManager.get("documentName"));
        dialog.setContentText(LanguageManager.get("name"));

        dialog.showAndWait().ifPresent(name -> {
            int documentId = documentManager.getDocuments().size() + 1; // Generate new ID
            Document newDocument = new Document(documentId, name, LanguageManager.get("documentContent")+" "+ name);
            documentManager.addDocument(newDocument);

            CustomTreeItem<String> newDocumentItem = new CustomTreeItem<>("[D] " + name, newDocument);
            treeView.getRoot().getChildren().add(newDocumentItem);
        });
    }

    private void showHomepage() {
        treeView.getSelectionModel().clearSelection();
        textArea.setText(LanguageManager.get("welcome"));
        textArea.setDisable(true);
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
                    textArea.setText(doc.getContent());
                    textArea.setDisable(false); 
                } else {
                    // Handle document selection
                    textArea.setText(doc.getContent());
                    textArea.setDisable(false); 
                }
            }
        });
    
        treeView.setOnContextMenuRequested(this::showContextMenu);
    
        // Populate the tree view with documents and notes
        restoreTreeItems(rootItem);
    
        return treeView;
    }

    private void handleEncryptedNoteSelection(EncryptedNote encryptedNote) {
        PasswordDialog passwordDialog = new PasswordDialog(LanguageManager.get("EncryptedDialogText"),LanguageManager.get("EncryptedDialogHeader"),LanguageManager.get("EncryptedPassField"));
        Optional<String> result = passwordDialog.showAndWait();

        result.ifPresent(password -> {
            try {
                String content = encryptedNote.desencriptar(password);
                textArea.setText(content);
                textArea.setDisable(false);
            } catch (wrongPasswordException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erro");
                alert.setHeaderText(LanguageManager.get("wrongPassword"));
                alert.setContentText(LanguageManager.get("wrongPasswordDesc"));
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
            MenuItem createNoteItem = new MenuItem(LanguageManager.get("createNote"));
            MenuItem createEncryptedNoteItem = new MenuItem(LanguageManager.get("createEncryptedNote"));
            createNoteItem.setOnAction(e -> createNoteForDocument(customSelectedItem));
            createEncryptedNoteItem.setOnAction(e -> createEncryptedNoteForDocument(customSelectedItem));
            contextMenu.getItems().addAll(createNoteItem, createEncryptedNoteItem);

            MenuItem deleteItem = new MenuItem(LanguageManager.get("delete"));
            deleteItem.setOnAction(e -> deleteItem(selectedItem));
            contextMenu.getItems().add(deleteItem);

            treeView.setContextMenu(contextMenu);
            contextMenu.show(treeView, event.getScreenX(), event.getScreenY());
        }
    }

    private void createNoteForDocument(CustomTreeItem<String> documentItem) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(LanguageManager.get("createNote"));
        dialog.setHeaderText(LanguageManager.get("noteName"));
        dialog.setContentText(LanguageManager.get("name"));

        dialog.showAndWait().ifPresent(name -> {
            int noteId = (int) (Math.random() * 8000) + 1;

            // Retrieve the associated document
            Document document = documentItem.getDocument();

            // Create and associate the note with the document
            Note newNote = new Note(noteId, name, LanguageManager.get("noteContent")+" " + name, document);
            noteList.add(newNote);
            documentItem.getChildren().add(new CustomTreeItem<>("[N] " + name, newNote));
        });
    }

    private void createEncryptedNoteForDocument(CustomTreeItem<String> documentItem) {
        PasswordDialog passwordDialog = new PasswordDialog(LanguageManager.get("EncryptedDialogText"),LanguageManager.get("EncryptedDialogHeader"),LanguageManager.get("EncryptedPassField"));
        Optional<String> result = passwordDialog.showAndWait();

        result.ifPresent(password -> {
            TextInputDialog noteNameDialog = new TextInputDialog();
            noteNameDialog.setTitle(LanguageManager.get("createEncryptedNote"));
            noteNameDialog.setHeaderText(LanguageManager.get("encryptedNoteName"));
            noteNameDialog.setContentText(LanguageManager.get("name"));

            noteNameDialog.showAndWait().ifPresent(name -> {
                int noteId = (int) (Math.random() * 8000) + 1;

                // Retrieve the associated document
                Document document = documentItem.getDocument();

                // Create and associate the encrypted note with the document
                EncryptedNote newEncryptedNote = new EncryptedNote(noteId, name, LanguageManager.get("noteContent")+" " + name, document, password);
                encryptedNoteList.add(newEncryptedNote);
                documentItem.getChildren().add(new CustomTreeItem<>("[*N] " + name, newEncryptedNote));
            });
        });
    }

    private void deleteItem(TreeItem<String> item) {
        if (item.getParent() != null) {
            CustomTreeItem<String> customItem = (CustomTreeItem<String>) item;
            Document document = customItem.getDocument();
    
            // Remove document or note from the respective list
            if (document instanceof Note) {
                noteList.remove(document);
            } else if (document instanceof EncryptedNote) {
                encryptedNoteList.remove(document);
            } else if (document instanceof Document) {
                documentManager.removeDocument(document.getId());
                noteList.removeIf(note -> note.getDocument().getId() == document.getId());
                encryptedNoteList.removeIf(encryptedNote -> encryptedNote.getDocument().getId() == document.getId());
            }
    
            item.getParent().getChildren().remove(item);
            if (treeView.getRoot().getChildren().isEmpty()) {
                showHomepage();
            }
    
            // Save the updated lists to the respective files
            saveData();
        }
    }

    private void saveData() {
        try {
            documentManager.saveData("data.ser");

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("notes.ser"))) {
                oos.writeObject(noteList);
            }

            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("encryptedNotes.ser"))) {
                oos.writeObject(encryptedNoteList);
            }

        } catch (IOException e) {
            e.printStackTrace();
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
