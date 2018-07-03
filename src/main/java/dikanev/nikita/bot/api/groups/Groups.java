package dikanev.nikita.bot.api.groups;

public enum Groups {
    ROOT("root", 0),
    ADMIN("admin", 1),
    UNKNOWN("unknown", 2),
    USER("user", 3),
    TESTER("tester", 4);

    private String name;

    private int id;

    Groups(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public static String getName(int id){
        Groups[] groups = Groups.values();
        for (Groups group : groups) {
            if (group.getId() == id) {
                return group.name;
            }
        }

        return "";
    }

    public int getId() {
        return id;
    }
}
