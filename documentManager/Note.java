package documentManager;

public class Note extends Document {
    private Document _document;

    public Note(int id, String content, Document document) {
        super(id, content);
        this._document = document;
    }

    public Document getNote() {
        return _document;
    }

    public void setNote(Document document) {
        this._document = document;
    }
}
