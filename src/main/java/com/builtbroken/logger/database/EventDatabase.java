package com.builtbroken.logger.database;

import com.builtbroken.logger.ActionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/24/2018.
 */
public class EventDatabase
{
    private static final String CREATE_INTERACTION_TABLE = "CREATE TABLE INTERACTION " +
            "(ID int NOT NULL AUTO_INCREMENT, " +
            "time BIGINT, " +
            "dim int, " +
            "x int, " +
            "y int, " +
            "z int, " +
            "face TINYINT, " +
            "action TINYINT, " +
            "username varchar(255), " +
            "uuid varchar(255), " +
            "item varchar(255), " +
            "block varchar(255), " +
            "meta TINYINT, " +
            "tile varchar(255), " +
            "PRIMARY KEY (ID))";

    private static final String CREATE_BLOCK_BREAK_TABLE = "CREATE TABLE BLOCK_BREAK " +
            "(ID int NOT NULL AUTO_INCREMENT, " +
            "time BIGINT, " +
            "dim int, " +
            "x int, " +
            "y int, " +
            "z int, " +
            "username varchar(255), " +
            "uuid varchar(255), " +
            "item varchar(255), " +
            "block varchar(255), " +
            "meta TINYINT, " +
            "tile varchar(255), " +
            "PRIMARY KEY (ID))";

    public static void generateTablesIfMissing(Connection connection)
    {
        createTableIfMissing(connection, "INTERACTION", CREATE_INTERACTION_TABLE);
        createTableIfMissing(connection, "BLOCK_BREAK", CREATE_BLOCK_BREAK_TABLE);
    }

    protected static void createTableIfMissing(Connection connection, String name, String create_query)
    {
        if (!hasTable(connection, ActionLogger.database_name, name))
        {
            ActionLogger.logger.info("EventDatabase: Failed to find '" + name + "' table, creating...");
            try
            {
                PreparedStatement preparedStmt = connection.prepareStatement(create_query);
                preparedStmt.execute();
                ActionLogger.logger.info("EventDatabase: Created '" + name + "' table");
            }
            catch (SQLException e)
            {
                throw new RuntimeException("Failed to create table" + name, e);
            }
        }
    }

    protected static boolean hasTable(Connection connection, String db, String table)
    {
        try
        {
            String query = "SELECT count(*)" +
                    "FROM information_schema.tables " +
                    "WHERE table_schema = '" + db + "' AND table_name = '" + table + "'";

            PreparedStatement preparedStmt = connection.prepareStatement(query);
            preparedStmt.execute();

            ResultSet resultSet = preparedStmt.getResultSet();
            return resultSet.next() ? resultSet.getInt(1) > 0 : false;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
}
