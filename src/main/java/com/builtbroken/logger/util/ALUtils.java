package com.builtbroken.logger.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public class ALUtils
{
    private static String dateFormat = "yyyy-MM-dd hh-mm-ss";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);

    public static String getTime()
    {
        return simpleDateFormat.format(new Date());
    }

    public static String getTime(long time)
    {
        return simpleDateFormat.format(new Date(time));
    }
}
