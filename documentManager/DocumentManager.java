package documentManager;

import java.util.HashMap;
import java.util.Map;

public class DocumentManager {
    private Map<Integer, Document> _documents;

    public DocumentManager() {
        _documents = new HashMap<>();
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
}
