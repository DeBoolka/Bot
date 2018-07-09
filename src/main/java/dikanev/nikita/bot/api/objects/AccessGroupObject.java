package dikanev.nikita.bot.api.objects;

public class AccessGroupObject extends ApiObject {

    private int idGroup;

    private String command;

    private boolean access;

    public AccessGroupObject() {
        super("accessGroup");
    }

    public AccessGroupObject(int idGroup, String command, boolean access) {
        super("accessGroup");

        this.idGroup = idGroup;
        this.command = command;
        this.access = access;
    }

    public int getIdGroup() {
        return idGroup;
    }

    public String getCommand() {
        return command;
    }

    public boolean hasAccess() {
        return access;
    }

}
