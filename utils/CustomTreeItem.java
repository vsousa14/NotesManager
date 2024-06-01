package utils;

import documentManager.Document;
import javafx.scene.control.TreeItem;

public class CustomTreeItem<T> extends TreeItem<T> {
    private Document document;

    public CustomTreeItem(T value, Document document) {
        super(value);
        this.document = document;
    }

    public Document getDocument() {
        return document;
    }

    public void setDocument(Document document) {
        this.document = document;
    }
}
