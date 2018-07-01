package dikanev.nikita.bot.controller.db.groups;

import dikanev.nikita.bot.model.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessGroupDBController {

    private static final Logger LOG = LoggerFactory.getLogger(AccessGroupDBController.class);

    private static AccessGroupDBController ourInstance = new AccessGroupDBController();

    private PreparedStatement prStatement;

    public static AccessGroupDBController getInstance() {
        return ourInstance;
    }

    //Устанавливает доступ к команде для группы
    public boolean createAccess(int idGroup, int idCommand, boolean privilege) throws SQLException {
        String sql = "INSERT INTO groups_privilege(id_group, id_command, access) " +
                "VAlUES(?, ?, ?);";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.setInt(2, idCommand);
        prStatement.setBoolean(3, privilege);
        int res = prStatement.executeUpdate();

        prStatement.close();
        return res > 0;
    }

    //Устанавливает доступ к командам для группы
    public boolean createAccess(int idGroup, Integer[] idCommands, boolean privilege) throws SQLException {
        String sql = "INSERT INTO groups_privilege(id_group, id_command, access) " +
                "VAlUES(?, ?, ?);";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.setBoolean(3, privilege);

        int res = 0;
        for (int idCommand : idCommands) {
            prStatement.setInt(2, idCommand);
            res += prStatement.executeUpdate();
        }

        prStatement.close();
        return res > 0;
    }

    //Проверяет доступна ли комманда пользователю
    public boolean hasAccessUser(int idUser, int idCommand) throws SQLException {
        String sql = "SELECT access " +
                "FROM users LEFT JOIN groups_privilege USING(id_group) " +
                "WHERE users.id = ? AND groups_privilege.id_command = ? " +
                "LIMIT 1;";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idUser);
        prStatement.setInt(2, idCommand);
        ResultSet res = prStatement.executeQuery();

        boolean access = false;
        while (res.next()) {
            access = res.getBoolean("access");
        }

        res.close();
        return access;
    }

    //Проверяет доступна ли комманда группе
    public boolean hasAccessGroup(int idGroup, int idCommand) throws SQLException {
        String sql = "SELECT access " +
                "FROM groups_privilege " +
                "WHERE id_group = ? AND id_command = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.setInt(2, idCommand);
        ResultSet res = prStatement.executeQuery();

        boolean access = false;
        while (res.next()) {
            access = res.getBoolean("access");
        }

        res.close();
        return access;
    }

    //Изменяет доступ к команде для группы
    public boolean editAccess(int idGroup, int idCommand, boolean privilege) throws SQLException {
        String sql = "UPDATE groups_privilege " +
                "SET access = ? " +
                "WHERE id_group = ? AND id_command = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setBoolean(1, privilege);
        prStatement.setInt(2, idGroup);
        prStatement.setInt(3, idCommand);
        int res = prStatement.executeUpdate();

        prStatement.close();
        return res > 0;
    }

    //Удаляет из БД запись с доступом к команде
    public boolean deleteAccess(int idGroup, int idCommand) throws SQLException {
        String sql = "DELETE FROM groups_privilege " +
                "WHERE id_group = ? AND id_command = ? " +
                "LIMIT 1";

        prStatement = DBStorage.getInstance().getConnection().prepareStatement(sql);
        prStatement.setInt(1, idGroup);
        prStatement.setInt(2, idCommand);
        int res = prStatement.executeUpdate();

        prStatement.close();
        return res > 0;
    }
}
