package com.builtbroken.logger.thread;

import com.builtbroken.logger.ActionLogger;
import com.builtbroken.logger.data.IEventData;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public abstract class ThreadWriter implements Runnable
{
    public final ConcurrentLinkedQueue<IEventData> writeQueue = new ConcurrentLinkedQueue();
    public boolean run = true;

    public Thread thread;

    public abstract void saveAll(boolean exit);

    public void start()
    {
        ActionLogger.logger.info("ThreadWriter: Starting...");
        thread = new Thread(this, "ActionLoggerThread");
        thread.start();
    }

    public void stop()
    {
        ActionLogger.logger.info("ThreadWriter: Ending...");
        run = false;
        saveAll(true);
    }
}
