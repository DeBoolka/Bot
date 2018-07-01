package dikanev.nikita.bot.model.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBStorage {

    private static final Logger LOG = LoggerFactory.getLogger(DBStorage.class);

    private static DBStorage ourInstance = new DBStorage();

    private Connection connection = null;

    private boolean enabled = false;

    private String url = null;

    private String login = null;

    private String password = null;

    private long tokenDeleteTimeMinutes = 60*24*31;

    public DBStorage() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            LOG.warn("Not found class: com.mysql.cj.jdbc.Driver");
        }
    }

    public static DBStorage getInstance() {
        return ourInstance;
    }

    //Инициализация хранилища БД
    public void init(Properties properties){
        String enabled = properties.getProperty("db.enabled", "true");
        if (!enabled.equals("true")) {
            LOG.warn("DB is not included");
            return;
        }

        this.enabled = true;
        this.url = properties.getProperty("db.url");
        this.login = properties.getProperty("db.login");
        this.password = properties.getProperty("db.password");

        connect();
    }

    //Подключение к БД
    private void connect() throws IllegalStateException{
        if (!enabled) {
            LOG.warn("The DB module is not enabled.");
            throw new IllegalStateException("The DB module is not enabled.");
        }

        try {
            if (connection != null) {
                connection.close();
            }

            connection = DriverManager.getConnection(url, login, password);
            LOG.info("Connect DB");
        } catch (SQLException e) {
            connection = null;
            LOG.error("Can't connect to the DB");
            throw new IllegalStateException(e);
        }
    }

    public Connection getConnection() throws SQLException{
        if(connection.isValid(30)){
            return connection;
        }

        connect();

        return connection;
    }

    public void setTokenDeleteTime(long minutes) {
        tokenDeleteTimeMinutes = minutes;
    }

    public long getTokenDeleteTimeMinutes(){
        return tokenDeleteTimeMinutes;
    }
}
