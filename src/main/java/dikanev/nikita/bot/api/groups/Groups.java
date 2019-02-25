package dikanev.nikita.bot.api.groups;

public enum Groups {
    ROOT("root", 1),
    ADMIN("admin", 2),
    UNKNOWN("unknown", 3),
    USER("user", 4),
    TESTER("tester", 5);

    public static final int DEFAULT_USER_ID = 2;
    public static final int DEFAULT_GROUP = 3;

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
