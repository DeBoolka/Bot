package dikanev.nikita.bot.api.groups;

import dikanev.nikita.bot.controller.groups.AccessGroupController;
import dikanev.nikita.bot.controller.groups.GroupController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Objects;

public class Group {

    private static final Logger LOG = LoggerFactory.getLogger(Group.class);

    private int id;

    private String name;

    public Group(int id) {
        this.id = id;
        this.name = "";
    }

    public Group(int id, String name) {
        this.id = id;
        this.name = name;
    }

    //Проверка на доступ к команде по id группы
    public static boolean hasRight(int idGroup, int idCommand){
        try {
            return AccessGroupController.getInstance().hasAccessGroup(idGroup, idCommand);
        } catch (SQLException e) {
            LOG.warn(e.getSQLState());
        }

        return false;
    }

    //Проверка на доступ к команде
    public boolean hasRight(int idCommand){
        return hasRight(this.id, idCommand);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Group that = (Group) obj;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
