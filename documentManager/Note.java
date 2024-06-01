package documentManager;

public class Note extends Document {
    private Document _document;

    public Note(int id,String title, String content, Document document) {
        super(id,title, content);
        this._document = document;
    }

    public int getNoteId() {
        return getId();
    }

    public String getNoteTitle() {
        return getTitle();
    }

    public String getNoteContent() {
        return getContent();
    }

    public Document getDocument() {
        return _document;
    }

    public void setNoteTitle(String title) {
        setTitle(title);
    }

    public void setNoteContent(String content) {
        setContent(content);
    }

    public void setNote(Document document) {
        this._document = document;
    }
    
}
