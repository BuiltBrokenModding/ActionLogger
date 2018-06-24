package com.builtbroken.logger.data.event;

import com.builtbroken.logger.data.ActionType;
import com.builtbroken.logger.data.DataPool;
import net.minecraft.block.Block;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

/**
 * @see <a href="https://github.com/BuiltBrokenModding/VoltzEngine/blob/development/license.md">License</a> for what you can and can't do with the code.
 * Created by Dark(DarkGuardsman, Robert) on 6/23/2018.
 */
public class EventDataInteraction extends EventData
{
    private final static String query = "INSERT INTO INTERACTION (time, dim, x, y, z, face, action, username, uuid, item, block, meta, tile)"
            + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    private final static DataPool<EventDataInteraction> dataPool = new DataPool(100);

    public String username;
    public UUID uuid;
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
        username = null;
        uuid = null;
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
        preparedStmt.setInt(1, dim);
        preparedStmt.setInt(2, x);
        preparedStmt.setInt(3, y);
        preparedStmt.setInt(4, z);
        preparedStmt.setInt(5, face);
        preparedStmt.setInt(6, action.ordinal());
        preparedStmt.setString(7, username);
        preparedStmt.setString(8, uuid.toString());
        preparedStmt.setString(9, heldItem);
        preparedStmt.setString(10, blockName);
        preparedStmt.setInt(11, blockMeta);
        preparedStmt.setString(12, tileName);
        preparedStmt.execute();
    }

    public static EventDataInteraction get(long time, int dim, int x, int y, int z)
    {
        EventDataInteraction object = dataPool.get();
        if (object != null)
        {
            object = new EventDataInteraction(time, dim, x, y, z);
        }
        return object;
    }

    public static EventDataInteraction get(PlayerInteractEvent event)
    {
        EventDataInteraction object = get(System.currentTimeMillis(), event.world.provider.dimensionId, event.x, event.y, event.z);
        object.username = event.entityPlayer.getGameProfile().getName();
        object.uuid = event.entityPlayer.getGameProfile().getId();
        object.action = event.action;
        object.heldItem = heldItemAsString(event.entityPlayer);
        object.blockName = Block.blockRegistry.getNameForObject(event.world.getBlock(event.x, event.y, event.z));
        object.blockMeta = event.world.getBlockMetadata(event.x, event.y, event.z);
        object.tileName = "" + event.world.getTileEntity(event.x, event.y, event.z);
        return object;
    }

}
