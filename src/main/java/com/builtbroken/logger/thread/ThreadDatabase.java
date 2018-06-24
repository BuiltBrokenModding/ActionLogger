package com.builtbroken.logger.thread;

import com.builtbroken.logger.ActionLogger;
import com.builtbroken.logger.data.IEventData;
import com.builtbroken.logger.database.DBConnection;
import com.builtbroken.logger.database.EventDatabase;

import java.sql.SQLException;

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
                writeToDatabase(false);
                Thread.sleep(1000);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void writeToDatabase(boolean all)
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

        int writes = 0;
        while (!writeQueue.isEmpty() && writeQueue.peek() != null && (all || writes < 1000))
        {
            IEventData data = writeQueue.poll();
            if (data != null)
            {
                try
                {
                    data.writeToDataBase(dbConnection.getConnection());
                    writes++;
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    writeQueue.add(data); //Add to try again
                }
            }
        }
    }

    @Override
    public void saveAll()
    {
        writeToDatabase(true);
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
