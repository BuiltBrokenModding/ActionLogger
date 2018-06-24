package com.builtbroken.logger.database;

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

    private static Connection connection;

    public void start()
    {
        System.out.println("Loading driver...");

        try
        {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("Driver loaded!");
        }
        catch (ClassNotFoundException e)
        {
            throw new IllegalStateException("Cannot find the driver in the classpath!", e);
        }

        System.out.println("Connecting database...");

        try
        {
            connection = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected!");
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
