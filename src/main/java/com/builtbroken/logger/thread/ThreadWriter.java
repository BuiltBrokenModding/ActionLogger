package com.builtbroken.logger.thread;

import com.builtbroken.logger.ActionLogger;
import com.builtbroken.logger.data.ActionType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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

    public void logAction(World world, int x, int y, int z, ActionType type, String data, EntityPlayer player)
    {
        //TODO format
        String out = getTime();
        out += " | " + world.provider.dimensionId + ", " + x + ", " + y + ", " + z;

        if (player != null)
        {
            out += " | " + player.getGameProfile().getName();
            out += " | " + player.getGameProfile().getId();
        }

        out += " | " + type;
        out += " | " + data;

        writeQueue.add(out);
    }

    @Override
    public void run()
    {
        while (run)
        {
            try
            {
                if(writeQueue.size() > 1000)
                {
                    saveAll();
                }
                Thread.sleep(1000 * 60 * 10);
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

        Date date = new Date();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int year  = localDate.getYear();
        int month = localDate.getMonthValue();
        int day   = localDate.getDayOfMonth();

        do
        {
            file = new File(ActionLogger.saveFolder, year + "/" + month + "/" + day + "/block-log-" + getTime() + (count == 0 ? "" : "--" + (++count)) + ".txt");
        }
        while (file.exists());

        return file;
    }

    public final String getTime()
    {
        return simpleDateFormat.format(new Date());
    }
}
