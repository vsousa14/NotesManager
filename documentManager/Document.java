package documentManager;

public class Document {
    private int _id;
    private String _title;
    private String _content;

    public Document(int id,String title, String content) {
        this._id = id;
        this._title = title;
        this._content = content;
    }

    public int getId() {
        return _id;
    }

    public String getTitle(){
        return _title;
    }

    public String getContent() {
        return _content;
    }

    public void setTitle(String newTitle){
        this._title = newTitle;
    }

    public void setContent(String content) {
        this._content = content;
    }

    @Override
    public String toString() {
        return "Document{" +
                "id=" + _id +
                ", title='" + _title + '\'' +
                ", content='" + _content + '\'' +
                '}';
    }
}