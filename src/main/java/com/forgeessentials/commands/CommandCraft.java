package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.ContainerCheatyWorkbench;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.packet.Packet100OpenWindow;

public class CommandCraft extends FEcmdModuleCommands {
    @Override
    public String getCommandName()
    {
        return "craft";
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        EntityPlayerMP player = (EntityPlayerMP) sender;
        player.incrementWindowID();
        player.playerNetServerHandler.sendPacketToPlayer(new Packet100OpenWindow(player.currentWindowId, 1, "Crafting", 9, true));
        player.openContainer = new ContainerCheatyWorkbench(player.inventory, player.worldObj);
        player.openContainer.windowId = player.currentWindowId;
        player.openContainer.addCraftingToCrafters(player);
    }

    @Override
    public void processCommandConsole(ICommandSender sender, String[] args)
    {
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.MEMBERS;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/craft Open a crafting window.";
    }
}
