package dikanev.nikita.bot.api.item;

public class Login {
    private String login;

    public Login(String login) {
        this.login = login;
    }

    public boolean isValid() {
        return login != null
                && !login.trim().isEmpty()
                && login.length() <= 40;
    }

    public String getLogin() {
        return login;
    }

    @Override
    public String toString() {
        return getLogin();
    }
}
