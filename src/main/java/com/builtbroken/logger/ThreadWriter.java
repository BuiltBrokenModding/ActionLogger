package com.builtbroken.logger;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/21/2018.
 */
public class ThreadWriter implements Runnable
{
    public final ConcurrentLinkedQueue<String> writeQueue = new ConcurrentLinkedQueue();
    public boolean run = true;

    public static String dateFormat = "yyyy-MM-dd hh-mm";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public void logAction(World world, int x, int y, int z, String type, String data, EntityPlayer player)
    {
        //TODO format
        writeQueue.add(getTime() + " | "
                + world.provider.dimensionId + ", " + x + ", " + y + ", " + z
                + " | " + player.getGameProfile().getName()
                + " | " + player.getGameProfile().getId()
                + " | " + type
                + " | " + data);
    }

    @Override
    public void run()
    {
        while (run)
        {
            try
            {
                saveAll();
                Thread.sleep(1000 * 60);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveAll()
    {
        if (!writeQueue.isEmpty() && writeQueue.peek() != null)
        {
            try
            {
                saveData(getNextSaveFile());
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void saveData(File file) throws IOException
    {
        if (!writeQueue.isEmpty() && writeQueue.peek() != null)
        {
            int lineCount = 0;

            FileOutputStream fos = new FileOutputStream(file);
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
            while (!writeQueue.isEmpty() && writeQueue.peek() != null && lineCount < ActionLogger.lineCountLimit)
            {
                bw.write(writeQueue.poll());
                bw.newLine();
                lineCount++;
            }
            bw.close();

            System.out.println("ActionLogger: Saved " + lineCount + " log entries to file: " + file);
        }
    }

    public final File getNextSaveFile()
    {
        File file;
        int count = 0;
        do
        {
            file = new File(ActionLogger.saveFolder, "block-log-" + getTime() + (count == 0 ? "" : "--" + (++count)) + ".txt");
        }
        while (file.exists());

        return file;
    }

    public final String getTime()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        return simpleDateFormat.format(calendar.getTime());
    }
}
