package dikanev.nikita.bot.api.objects;

public class GroupObject extends ApiObject {

    private int id;

    private String name;

    public GroupObject(int id, String name) {
        super("group");

        this.id = id;
        this.name = name;
    }
}
