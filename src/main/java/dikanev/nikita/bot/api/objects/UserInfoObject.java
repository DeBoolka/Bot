package dikanev.nikita.bot.api.objects;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.sql.Date;

public class UserInfoObject extends ApiObject {

    public int userId = 0;

    public String login = null;

    public String email = null;

    public Date age = null;

    public String phone = null;

    public String city = null;

    public String nameOnGame = null;

    private UserInfoObject() {
        super("userInfo");
    }

    public static UserInfoObject empty() {
        return new UserInfoObject();
    }

    public UserInfoObject(String type, int userId, String login, String email, Date age, String phone, String city, String nameOnGame) {
        super("userInfo");

        this.userId = userId;
        this.login = login;
        this.email = email;
        this.age = age;
        this.phone = phone;
        this.city = city;
        this.nameOnGame = nameOnGame;
    }

    @Override
    public void init(JsonElement js) {
        if (js.isJsonObject()) {
            JsonObject root = js.getAsJsonObject();
            if (root.has("userId")) {
                userId = root.get("userId").getAsInt();
            }
            if (root.has("login")) {
                login = root.get("login").getAsString();
            }
            if (root.has("email")) {
                email = root.get("email").getAsString();
            }
            if (root.has("age")) {
                age = Date.valueOf(root.get("age").getAsString());
            }
            if (root.has("phone")) {
                phone = root.get("phone").getAsString();
            }
            if (root.has("city")) {
                city = root.get("city").getAsString();
            }
            if (root.has("nameOnGame")) {
                nameOnGame = root.get("nameOnGame").getAsString();
            }
        }

    }
}
