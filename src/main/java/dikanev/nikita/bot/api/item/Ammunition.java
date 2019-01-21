package dikanev.nikita.bot.api.item;

import java.util.Arrays;

public class Ammunition {
    public int id;

    public int ownerId;

    public String name;

    public PhotoCore[] photos;

    @Override
    public String toString() {
        return "Ammunition{" +
                "id=" + id +
                ", ownerId=" + ownerId +
                ", name='" + name + '\'' +
                ", photos=" + Arrays.toString(photos) +
                '}';
    }
}
