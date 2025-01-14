package com.forgeessentials.api.permissions.query;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import net.minecraft.world.World;

public class PermQueryBlanketZone extends PermQuery {
    public Zone toCheck;

    public PermQueryBlanketZone(String permission, String zoneID)
    {
        toCheck = APIRegistry.zones.getZone(zoneID);
        checkForward = false;
    }

    public PermQueryBlanketZone(String permission, String zoneID, boolean checkForward)
    {
        toCheck = APIRegistry.zones.getZone(zoneID);
        this.checkForward = checkForward;
    }

    public PermQueryBlanketZone(String permission, Zone zone)
    {
        toCheck = zone;
        checkForward = false;
    }

    public PermQueryBlanketZone(String permission, Zone zone, boolean checkForward)
    {
        toCheck = zone;
        this.checkForward = checkForward;
    }

    /**
     * uses the WorldZone for the specified world
     *
     * @param world
     */
    public PermQueryBlanketZone(String permission, World world)
    {
        toCheck = APIRegistry.zones.getWorldZone(world);
    }

    /**
     * Assumes GLOBAL as the zone
     */
    public PermQueryBlanketZone(String permission)
    {
        toCheck = APIRegistry.zones.getGLOBAL();
    }
}
