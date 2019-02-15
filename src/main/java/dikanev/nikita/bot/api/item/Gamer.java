package dikanev.nikita.bot.api.item;

import java.sql.Timestamp;

public class Gamer {
    public int gameId;
    public int userId = 0;
    public String userGroup = "verifiable";
    public String status = "awaiting";
    public int roleId = 0;
    public int money = 0;
    public Timestamp fillingTime = null;

    public Gamer() {
    }

    public Gamer(int gameId, int userId, String userGroup, String status, int roleId, int money, Timestamp fillingTime) {
        this.gameId = gameId;
        this.userId = userId;
        this.userGroup = userGroup;
        this.status = status;
        this.roleId = roleId;
        this.money = money;
        this.fillingTime = fillingTime;
    }

    public String getStatus() {
        return status;
    }

    public String getLocaleStatus() {
        return getStatus();
    }
}


