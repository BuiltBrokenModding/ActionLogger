package com.builtbroken.logger.thread;

import com.builtbroken.logger.ActionLogger;
import com.builtbroken.logger.data.IEventData;

import java.sql.SQLException;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/21/2018.
 */
public class ThreadDatabase extends ThreadWriter
{
    boolean doingWrite = false;

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
        int writes = 0;
        while (!writeQueue.isEmpty() && writeQueue.peek() != null && (all || writes < 1000) && run)
        {
            IEventData data = writeQueue.poll();
            if (data != null)
            {
                doingWrite = true;
                try
                {
                    data.writeToDataBase(ActionLogger.getDbConnection().getConnection());
                    writes++;
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                    writeQueue.add(data); //Add to try again
                }
                doingWrite = false;
            }
        }
    }

    @Override
    public void saveAll(boolean exit)
    {
        if (exit)
        {
            run = false;

            if (doingWrite)
            {
                try
                {
                    Thread.sleep(100);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        writeToDatabase(true);
        if (exit)
        {
            ActionLogger.getDbConnection().stop();
        }
    }

    @Override
    public void stop()
    {
        super.stop();
    }
}
