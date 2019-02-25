package dikanev.nikita.bot.service.client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class SQLRequest {

    private Connection connection;

    private PreparedStatement prStatement;


    public SQLRequest(Connection connection) {
        this.connection = connection;
    }

    public SQLRequest build(String sql) throws SQLException {
        prStatement = connection.prepareStatement(sql);
        return this;
    }

    public SQLRequest set(PrSt... statements) throws SQLException {
        for (PrSt temp : statements) {
            temp.set(prStatement);
        }
        return this;
    }

    public int executeUpdate() throws SQLException {
        return prStatement.executeUpdate();
    }

    public ResultSet executeQuery() throws SQLException {
        return prStatement.executeQuery();
    }

    public SQLRequest close() throws SQLException {
        if (!prStatement.isClosed()) {
            prStatement.close();
        }
        return this;
    }

    public interface PrSt{
        void set(PreparedStatement preparedStatement) throws SQLException;
    }
}
