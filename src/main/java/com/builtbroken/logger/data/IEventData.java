package com.builtbroken.logger.data;

import java.sql.Connection;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public interface IEventData
{
    /**
     * Called to convert the data to a flat file entry
     *
     * @return string of the data for file writing
     */
    String generateFlatFile();

    /**
     * Called to write the data to the database
     *
     * @param connection
     */
    void writeToDataBase(Connection connection);
}
