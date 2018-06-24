package com.builtbroken.logger.data;

import java.util.LinkedList;
import java.util.Queue;

/**
 * Used to cache and recycle data objects to reduce memory usage
 *
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public class DataPool<D extends IDataPoolObject>
{
    private final Queue<D> pool = new LinkedList();
    private final int limit;
    private int count;

    public DataPool(int limit)
    {
        this.limit = limit;
    }

    public D get()
    {
        if (pool.peek() != null)
        {
            count--;
            return pool.poll();
        }
        return null;
    }

    public void reclaim(D object)
    {
        if (object != null && count < limit)
        {
            object.onReclaimed();
            pool.add(object);
            count++;
        }
    }
}
