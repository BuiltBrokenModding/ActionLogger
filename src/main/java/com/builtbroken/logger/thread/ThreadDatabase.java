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
        ActionLogger.logger.info("ThreadDatabase: Starting...");
        while (run)
        {
            try
            {
                writeToDatabase(false);
                Thread.sleep(ActionLogger.database_save_time);
            }
            catch (Exception e)
            {
                ActionLogger.logger.info("ThreadDatabase: Error database thread has hit unexpected error", e);
            }
        }
        ActionLogger.logger.info("ThreadDatabase: Ending...");
    }

    protected void writeToDatabase(boolean all)
    {
        int writes = 0;
        while (
                !writeQueue.isEmpty()
                        && writeQueue.peek() != null
                        && (all || writes < ActionLogger.database_write_limit || ActionLogger.database_save_time <= -1)
                        && run)
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
                    ActionLogger.logger.info("ThreadDatabase: Error, failed to write to DB", e);
                    writeQueue.add(data); //Add to try again
                }
                doingWrite = false;
            }
        }

        if (ActionLogger.database_log_writes)
        {
            ActionLogger.logger.info("ThreadDatabase: Wrote " + writes + " entries to the database");
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
                    ActionLogger.logger.info("ThreadDatabase: Error sleeping after save-all... can sorta ignore.", e);
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
