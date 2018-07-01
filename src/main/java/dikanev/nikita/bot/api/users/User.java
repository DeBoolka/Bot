package dikanev.nikita.bot.api.users;

import dikanev.nikita.bot.api.exceptions.NoAccessException;
import dikanev.nikita.bot.api.groups.Group;
import dikanev.nikita.bot.controller.groups.AccessGroupController;
import dikanev.nikita.bot.controller.users.UserController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Map;

public class User {

    private static final Logger LOG = LoggerFactory.getLogger(User.class);

    public static final int DEFAULT_ID = 2;

    public static final int DEFAULT_GROUP = 3;

    private int id;

    private int idGroup;

    private String sName;

    private String name;

    public User(int id, int idGroup, String sName, String name) {
        init(id, idGroup, sName, name);
    }

    public User(int id, int idGroup) {
        init(id, idGroup, "", "");
    }

    public User(int id) {
        init(id, 3, "", "");
    }

    public void init(int id, int idGroup, String sName, String name) {
        this.id = id;
        this.idGroup = idGroup;
        this.sName = sName;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public int getIdGroup() {
        return idGroup;
    }

    //Проверка доступа по текущей группе
    public boolean hasRightByGroup(int idCommand){
        return Group.hasRight(idGroup, idCommand);
    }

    //Проверка доступа по id
    public static boolean hasRightByUser(int idUser, int idCommand){
        try {
            return AccessGroupController.getInstance().hasAccessUser(idUser, idCommand);
        } catch (SQLException ignore) {
        }

        return false;
    }

    //Проверка доступа по текущему id
    public boolean hasRightByUser(int idCommand) {
        return hasRightByUser(id, idCommand);
    }
}
