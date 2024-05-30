package utils;

import javafx.scene.control.TreeItem;

public class CustomTreeItem<T> extends TreeItem<String> {
    private T associatedDocument;

    public CustomTreeItem(String value, T associatedDocument) {
        super(value);
        this.associatedDocument = associatedDocument;
    }

    public T getAssociatedDocument() {
        return associatedDocument;
    }
}