package documentManager;

public class Document {
    private int _id;
    private String _content;

    public Document(int id, String content) {
        this._id = id;
        this._content = content;
    }

    public int getId() {
        return _id;
    }

    public String getContent() {
        return _content;
    }

    public void setContent(String content) {
        this._content = content;
    }
}