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
            "(ID int NOT NULL AUTO_INCREMENT," +
            "time UNSIGNED BIGINT," +
            "dim int," +
            "x int," +
            "y int," +
            "z int," +
            "face TINYINT," +
            "action TINYINT," +
            "username varchar(255)," +
            "uuid varchar(255)," +
            "item varchar(255)," +
            "block varchar(255)," +
            "meta TINYINT," +
            "tile varchar(255)," +
            "PRIMARY KEY (ID))";

    public static void generateTablesIfMissing(Connection connection)
    {
        ActionLogger.logger.info("EventDatabase: Checking that database has required tables");
        if (!hasTable(connection, ActionLogger.database_name, "INTERACTION"))
        {
            ActionLogger.logger.info("EventDatabase: Failed to find 'interaction' table, creating...");
            try
            {
                PreparedStatement preparedStmt = connection.prepareStatement(CREATE_INTERACTION_TABLE);
                preparedStmt.execute();
                ActionLogger.logger.info("EventDatabase: Created 'interaction' table");
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    protected static boolean hasTable(Connection connection, String db, String table)
    {
        try
        {
            String query = "SELECT count(*)" +
                    "FROM information_schema.tables" +
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
