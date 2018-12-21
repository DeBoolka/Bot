package dikanev.nikita.bot.logic.connector.db.groups;

import dikanev.nikita.bot.service.client.SQLRequest;
import dikanev.nikita.bot.service.storage.DBStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class GroupDBConnector {

    private static final Logger LOG = LoggerFactory.getLogger(GroupDBConnector.class);

    //Создание группы
    public static int createGroup(String name) throws SQLException {
        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection())
                .build("INSERT groups(id, name) VALUES (NULL, ?)")
                .set(p -> p.setString(1, name));

        int res = req.executeUpdate();
        req.close();

        if (res == 0) {
            LOG.warn("Failed to create a group with the name: " + name);
            throw new IllegalStateException("Failed to create a group with the name: " + name);
        }

        return getLastId();
    }

    //Удаление группы
    public static boolean deleteGroup(int idGroup) throws SQLException {
        String sql = "DELETE FROM groups " +
                "WHERE id = ? " +
                "LIMIT 1";

        SQLRequest req = new SQLRequest(DBStorage.getInstance().getConnection())
                .build(sql)
                .set(p -> p.setInt(1, idGroup));

        int countDelete = req.executeUpdate();
        req.close();

        if (countDelete == 0) {
            LOG.warn("Failed to delete group with id: " + idGroup);
            return false;
        }

        sql = "DELETE FROM groups_privilege " +
                "WHERE id_group = ? ";

        req.build(sql).set(p -> p.setInt(1, idGroup)).executeUpdate();
        req.close();

        sql = "UPDATE users SET id_group = 3 " +
                "WHERE id_group = ?";

        req.build(sql).set(p -> p.setInt(1, idGroup)).executeUpdate();
        req.close();

        return true;
    }

    //Получение имени группы
    public static String getName(int idGroup) throws SQLException {
        ResultSet res =  new SQLRequest(DBStorage.getInstance().getConnection())
                .build("SELECT name " +
                        "FROM groups " +
                        "WHERE id = ? " +
                        "LIMIT 1;")
                .set(p -> p.setInt(1, idGroup))
                .executeQuery();

        String nameGroup = null;
        while (res.next()) {
            nameGroup = res.getString("name");
        }

        res.close();
        return nameGroup;
    }

    //Получение последнего вставленного id
    private static int getLastId() throws SQLException{
        String sql = "SELECT LAST_INSERT_ID() AS id";
        int lastId = -1;
        Statement statement = DBStorage.getInstance().getConnection().createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            lastId = resultSet.getInt("id");
        }

        statement.close();
        return lastId;
    }
}
