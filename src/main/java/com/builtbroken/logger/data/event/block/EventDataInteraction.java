package com.builtbroken.logger.data.event.block;

import com.builtbroken.logger.data.ActionType;
import com.builtbroken.logger.data.DataPool;
import com.builtbroken.logger.data.event.EventData;
import com.builtbroken.logger.data.user.User;
import com.builtbroken.logger.database.EventDatabase;
import com.builtbroken.logger.database.UserDatabase;
import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public class EventDataInteraction extends EventData
{
    private final static String query = "INSERT INTO " + EventDatabase.INTERACTION_TABLE + " (time, dim, x, y, z, face, action, player, item, block, meta, tile)"
            + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final static DataPool<EventDataInteraction> dataPool = new DataPool(100);

    public User user;
    public PlayerInteractEvent.Action action;

    public String heldItem;
    public int face;

    public String blockName;
    public String tileName;
    public int blockMeta;

    protected EventDataInteraction(long time, int dim, int x, int y, int z)
    {
        super(ActionType.INTERACTION, time, dim, x, y, z);
    }

    @Override
    public void onReclaimed()
    {
        super.onReclaimed();
        user = null;
        action = null;
        heldItem = null;
        face = 0;
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
        data += " - A: " + action;
        if (action != PlayerInteractEvent.Action.RIGHT_CLICK_AIR)
        {
            data += " - F: " + face;
            data += " - B: " + blockName;
            data += " - M: " + blockMeta;
            data += " - T: " + tileName;
        }
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
        preparedStmt.setInt(6, face);
        preparedStmt.setInt(7, action.ordinal());
        preparedStmt.setInt(8, user.id);
        preparedStmt.setString(9, heldItem);
        preparedStmt.setString(10, blockName);
        preparedStmt.setInt(11, blockMeta);
        preparedStmt.setString(12, tileName);
        preparedStmt.execute();
    }

    public static EventDataInteraction get(long time, int dim, int x, int y, int z)
    {
        EventDataInteraction object = dataPool.get();
        if (object == null)
        {
            object = new EventDataInteraction(time, dim, x, y, z);
        }
        return object;
    }

    public static EventDataInteraction get(PlayerInteractEvent event)
    {
        EventDataInteraction object = get(System.currentTimeMillis(), event.world.provider.dimensionId, event.x, event.y, event.z);

        object.user = UserDatabase.getUser(event.entityPlayer);
        object.action = event.action;
        object.face = event.face;
        object.heldItem = heldItemAsString(event.entityPlayer);
        object.blockName = Block.blockRegistry.getNameForObject(event.world.getBlock(event.x, event.y, event.z));
        object.blockMeta = event.world.getBlockMetadata(event.x, event.y, event.z);
        object.tileName = "" + event.world.getTileEntity(event.x, event.y, event.z);
        return object;
    }

}
