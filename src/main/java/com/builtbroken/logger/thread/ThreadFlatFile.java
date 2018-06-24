package com.builtbroken.logger.thread;

import com.builtbroken.logger.ActionLogger;
import com.builtbroken.logger.data.IEventData;

import java.io.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/21/2018.
 */
public class ThreadFlatFile extends ThreadWriter
{
    private static final String dateFormat = "yyyy-MM-dd hh-mm";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    @Override
    public void run()
    {
        while (run)
        {
            try
            {
                if (writeQueue.size() > 1000)
                {
                    saveAll(false);
                }
                Thread.sleep(1000 * 60 * 10);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveAll(boolean exit)
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
            while (!writeQueue.isEmpty() && writeQueue.peek() != null && lineCount < ActionLogger.lineCountLimit && run)
            {
                final IEventData data = writeQueue.poll();
                if (data != null)
                {
                    try
                    {
                        String line = data.generateFlatFile();
                        if (line != null && !line.isEmpty())
                        {
                            bw.write(line.trim());
                            bw.newLine();
                            lineCount++;
                        }
                    }
                    catch (Exception e)
                    {
                        //TODO write data to log file using reflection
                        ActionLogger.logger.error("Failed to output data to flat file for " + data, e);
                    }
                }
            }
            bw.close();

            System.out.println("ActionLogger: Saved " + lineCount + " log entries to file: " + file);
        }
    }

    public final File getNextSaveFile()
    {
        File file;
        int count = 0;

        //Get data and time
        final Date date = new Date();
        final LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        final int year = localDate.getYear();
        final int month = localDate.getMonthValue();
        final int day = localDate.getDayOfMonth();

        //Get path
        final String path = "events/" + year + "/" + month + "/" + day;
        final String name = "/log-" + getTime();

        //Find file so not to write over existing
        do
        {
            file = new File(ActionLogger.saveFolder, path + name + (count == 0 ? "" : "--" + (++count)) + ".txt");
        }
        while (file.exists());

        //Create folder if missing
        File folder = new File(ActionLogger.saveFolder, path);
        if (!folder.exists())
        {
            folder.mkdirs();
        }

        return file;
    }

    public final String getTime()
    {
        return simpleDateFormat.format(new Date());
    }
}
