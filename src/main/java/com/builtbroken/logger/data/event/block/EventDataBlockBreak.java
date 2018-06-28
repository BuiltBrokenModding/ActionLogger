package com.builtbroken.logger.data.event.block;

import com.builtbroken.logger.data.ActionType;
import com.builtbroken.logger.data.DataPool;
import com.builtbroken.logger.data.event.EventData;
import com.builtbroken.logger.data.user.User;
import com.builtbroken.logger.database.EventDatabase;
import com.builtbroken.logger.database.UserDatabase;
import net.minecraft.block.Block;
import net.minecraftforge.event.world.BlockEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public class EventDataBlockBreak extends EventData
{
    private final static String query = "INSERT INTO " + EventDatabase.BLOCK_BREAK_TABLE + " (time, dim, x, y, z, player, item, block, meta, tile)"
            + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final static DataPool<EventDataBlockBreak> dataPool = new DataPool(100);

    public User user;

    public String heldItem;

    public String blockName;
    public String tileName;
    public int blockMeta;

    protected EventDataBlockBreak(long time, int dim, int x, int y, int z)
    {
        super(ActionType.INTERACTION, time, dim, x, y, z);
    }

    @Override
    public void onReclaimed()
    {
        super.onReclaimed();
        user = null;
        heldItem = null;
        blockName = null;
        tileName = null;
        blockMeta = 0;
    }

    @Override
    public void reclaim()
    {
        dataPool.reclaim(this);
    }

    @Override
    public String generateFlatFile()
    {
        String out = super.generateFlatFile();

        String data = "[ H: " + heldItem;
        data += " - B: " + blockName;
        data += " - M: " + blockMeta;
        data += " - T: " + tileName;
        data += " ]";

        return out + " | " + data;
    }

    @Override
    public void writeToDataBase(Connection connection) throws SQLException
    {
        PreparedStatement preparedStmt = connection.prepareStatement(query);
        preparedStmt.setLong(1, time);
        preparedStmt.setInt(2, dim);
        preparedStmt.setInt(3, x);
        preparedStmt.setInt(4, y);
        preparedStmt.setInt(5, z);
        preparedStmt.setInt(6, user.id);
        preparedStmt.setString(7, heldItem);
        preparedStmt.setString(8, blockName);
        preparedStmt.setInt(9, blockMeta);
        preparedStmt.setString(10, tileName);
        preparedStmt.execute();
    }

    public static EventDataBlockBreak get(long time, int dim, int x, int y, int z)
    {
        EventDataBlockBreak object = dataPool.get();
        if (object == null)
        {
            object = new EventDataBlockBreak(time, dim, x, y, z);
        }
        return object;
    }

    public static EventDataBlockBreak get(BlockEvent.BreakEvent event)
    {
        EventDataBlockBreak object = get(System.currentTimeMillis(), event.world.provider.dimensionId, event.x, event.y, event.z);

        object.user = UserDatabase.getUser(event.getPlayer());
        object.heldItem = heldItemAsString(event.getPlayer());
        object.blockName = Block.blockRegistry.getNameForObject(event.block);
        object.blockMeta = event.blockMetadata;
        object.tileName = "" + event.world.getTileEntity(event.x, event.y, event.z);
        return object;
    }

}
