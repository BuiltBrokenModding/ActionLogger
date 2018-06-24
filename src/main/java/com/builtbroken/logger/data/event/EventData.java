package com.builtbroken.logger.data.event;

import com.builtbroken.logger.data.ActionType;
import com.builtbroken.logger.data.IDataPoolObject;
import com.builtbroken.logger.data.IEventData;
import com.builtbroken.logger.util.ALUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public abstract class EventData implements IEventData, IDataPoolObject
{
    public final ActionType type;
    public long time;
    public int dim;
    public int x;
    public int y;
    public int z;

    protected EventData(ActionType type, long time, int dim, int x, int y, int z)
    {
        this.type = type;
        this.time = time;
        this.dim = dim;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void onReclaimed()
    {
        time = 0;
        dim = 0;
        x = 0;
        y = 0;
        z = 0;
    }

    @Override
    public String generateFlatFile()
    {
        String out = ALUtils.getTime(time);
        out += " | " + dim + ", " + x + ", " + y + ", " + z;
        out += " | " + type;
        return out;
    }

    protected final static String heldItemAsString(EntityPlayer player)
    {
        ItemStack stack = player.getHeldItem();
        if (stack != null)
        {
            return "HELD: " + stack.getUnlocalizedName();
        }
        return "HAND";
    }
}
