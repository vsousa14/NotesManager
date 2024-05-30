package documentManager;

import exceptions.wrongPasswordException;

public class EncryptedNote extends Note {
    private String _password;

    public EncryptedNote(int id, String content, Document document, String password) {
        super(id, content, document);
        this._password = password;
    }

    public String getSenha() {
        return _password;
    }

    public void setSenha(String senha) {
        this._password = senha;
    }

    public String desencriptar(String senha) throws wrongPasswordException {
        if (this._password.equals(senha)) {
            return getContent();
        } else {
            throw new wrongPasswordException("Wrong Password, try again!");
        }
    }
}
