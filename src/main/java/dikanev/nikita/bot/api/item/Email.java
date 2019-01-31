package dikanev.nikita.bot.api.item;

import java.util.regex.Pattern;

public class Email {
    private final static Pattern pattern = Pattern.compile("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-z]{2,})$", Pattern.CASE_INSENSITIVE);

    private String email;

    public Email(String email) {
        this.email = email;
    }

    public boolean isValid(){
        return email != null
                && !email.trim().isEmpty()
                && pattern.matcher(email).matches();
    }

    @Override
    public String toString() {
        return email;
    }
}
