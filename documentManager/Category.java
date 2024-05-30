package documentManager;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String _name;
    private List<Document> _documents;

    public Category(String nome) {
        this._name = nome;
        this._documents = new ArrayList<>();
    }

    public String getDocumentName() {
        return _name;
    }

    public List<Document> getDocuments() {
        return _documents;
    }

    public void addDocuments(Document documento) {
        _documents.add(documento);
    }

    public void removeDocuments(Document documento) {
        _documents.remove(documento);
    }
}