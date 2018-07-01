package dikanev.nikita.bot.api.objects;

public class UserObject extends ApiObject {

    private int id;

    private int idGroup;

    private String sName;

    private String name;

    public UserObject(int id, int idGroup, String sName, String name) {
        super("user");

        this.id = id;
        this.idGroup = idGroup;
        this.sName = sName;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public void setIdGroup(int idGroup) {
        this.idGroup = idGroup;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
