package com.builtbroken.logger.thread;

import com.builtbroken.logger.ActionLogger;
import com.builtbroken.logger.database.DBConnection;
import com.builtbroken.logger.database.EventDatabase;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/21/2018.
 */
public class ThreadDatabase extends ThreadWriter
{
    DBConnection dbConnection;

    @Override
    public void run()
    {
        while (run)
        {
            try
            {
                writeToDatabase();
                Thread.sleep(100);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void writeToDatabase()
    {
        if (dbConnection == null)
        {
            dbConnection = new DBConnection();
            dbConnection.url = ActionLogger.database_url + ActionLogger.database_name;
            dbConnection.username = ActionLogger.database_username;
            dbConnection.password = ActionLogger.database_password;
            dbConnection.start();

            EventDatabase.generateTablesIfMissing(dbConnection.getConnection());
        }
    }

    @Override
    public void saveAll()
    {
        writeToDatabase();
    }

    @Override
    public void stop()
    {
        super.stop();
        if (dbConnection != null)
        {
            dbConnection.stop();
        }
    }
}
