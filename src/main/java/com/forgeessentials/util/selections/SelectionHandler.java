package com.forgeessentials.util.selections;

import com.forgeessentials.commons.network.Packet1SelectionUpdate;
import net.minecraft.entity.player.EntityPlayerMP;

import com.forgeessentials.commons.network.NetworkUtils;
import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.util.OutputHandler;

public class SelectionHandler
{

    public static ISelectionProvider selectionProvider = pickBestSelectionProvider();

    private static ISelectionProvider pickBestSelectionProvider()
    {
        if (ModuleLauncher.getModuleList().contains("WEIntegrationTools"))
        {
            try
            {
                Class<?> weprovider = Class.forName("com.forgeessentials.compat.worldedit.WESelectionHandler");
                return (ISelectionProvider) weprovider.newInstance();
            }
            catch (ClassNotFoundException | InstantiationException | IllegalAccessException e3)
            {
                OutputHandler.felog.warning("There was a problem starting the WorldEdit selection provider. Switching to FE's own provider.");
            }
        }
        return new PlayerInfoSelectionProvider();
    }

    public static void sendUpdate(EntityPlayerMP player)
    {
        NetworkUtils.netHandler.sendTo(new Packet1SelectionUpdate(selectionProvider.getSelection(player)), player);
    }

}
