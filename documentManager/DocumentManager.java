package documentManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class DocumentManager implements Serializable {
    private static final long serialVersionUID = 1L;
    private Map<Integer, Document> _documents;

    public DocumentManager() {
        _documents = new HashMap<>();
    }

    public Map<Integer,Document> getDocuments() {
        return _documents;
    }

    public void addDocument(Document document) {
        _documents.put(document.getId(), document);
    }

    public void removeDocument(int id) {
        _documents.remove(id);
    }

    public Document searchDocument(int id) {
        return _documents.get(id);
    }

    public void editDocument(int id, String newContent) {
        Document doc = _documents.get(id);
        if (doc != null) {
            doc.setContent(newContent);
        }
    }
    
    public void saveData(String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(this);
        }
    }

    public static DocumentManager loadData(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (DocumentManager) ois.readObject();
        }
    }
}
