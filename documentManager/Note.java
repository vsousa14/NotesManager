package documentManager;

import java.io.Serializable;

public class Note extends Document  implements Serializable {
    private Document _document;
    private static final long serialVersionUID = 1L;

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

   

    public void setNoteContent(String content) {
        setContent(content);
    }

    public void setNote(Document document) {
        this._document = document;
    }
    
}
