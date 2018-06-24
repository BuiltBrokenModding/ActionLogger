package com.builtbroken.logger.data;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public interface IDataPoolObject
{
    /**
     * Called to clear any cached data
     */
    void onReclaimed();
}
