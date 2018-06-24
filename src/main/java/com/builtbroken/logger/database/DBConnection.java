package com.builtbroken.logger.database;

import com.builtbroken.logger.ActionLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public class DBConnection
{
    public String url = "jdbc:mysql://localhost:3306/javabase";
    public String username = "root";
    public String password = "";

    private Connection connection;

    public Connection getConnection()
    {
        return connection;
    }

    public void start()
    {
        ActionLogger.logger.info("Loading driver...");

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            ActionLogger.logger.info("Driver loaded!");
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }

        ActionLogger.logger.info("Connecting database...");

        try
        {
            connection = DriverManager.getConnection(url, username, password);
            ActionLogger.logger.info("Database connected!");
        }
        catch (SQLException e)
        {
            throw new IllegalStateException("Cannot connect the database!", e);
        }
    }

    public void stop()
    {
        if (connection != null)
        {
            try
            {
                connection.close();
                connection = null;
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}
