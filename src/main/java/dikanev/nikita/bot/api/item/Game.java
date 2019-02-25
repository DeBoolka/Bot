package dikanev.nikita.bot.api.item;

import java.sql.Timestamp;

public class Game {
    public int id;
    public String name;
    public int organizerId;
    public String city;
    public Timestamp date;

    public Game(int id, String name, int organizerId, String city, Timestamp date) {
        this.id = id;
        this.name = name;
        this.organizerId = organizerId;
        this.city = city;
        this.date = date;
    }
}
