package com.forgeessentials.commands;

import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.commands.util.FEcmdModuleCommands;
import com.forgeessentials.util.OutputHandler;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumChatFormatting;

import java.util.List;

public class CommandBind extends FEcmdModuleCommands {
    public static final String color = EnumChatFormatting.RESET + "" + EnumChatFormatting.AQUA;

    @Override
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par1)
    {
        return false;
    }

    @Override
    public String getCommandName()
    {
        return "bind";
    }

    @Override
    public RegGroup getReggroup()
    {
        return RegGroup.OWNERS;
    }

    @Override
    public void processCommandPlayer(EntityPlayer sender, String[] args)
    {
        if (args.length == 0 || !(args[0].equalsIgnoreCase("left") || args[0].equalsIgnoreCase("right") || args[0].equalsIgnoreCase("clear")))
        {
            OutputHandler.chatError(sender, "Improper syntax. Please try this instead: <left|right|clear> <command [args]>");
        }
        else if (sender.inventory.getCurrentItem() == null)
        {
            OutputHandler.chatError(sender, "You are not holding a valid item.");
        }
        else
        {
            ItemStack is = sender.inventory.getCurrentItem();
            if (!is.hasTagCompound())
            {
                is.stackTagCompound = new NBTTagCompound();
            }
            if (args[0].equalsIgnoreCase("clear"))
            {
                is.getTagCompound().removeTag("FEbinding");
                NBTTagCompound display = is.getTagCompound().getCompoundTag("display");
                display.setTag("Lore", new NBTTagList());
            }
            else
            {
                StringBuilder cmd = new StringBuilder();
                for (int i = 1; i < args.length; i++)
                {
                    cmd.append(args[i] + " ");
                }

                NBTTagCompound nbt = is.getTagCompound().getCompoundTag("FEbinding");
                nbt.setString(args[0].toLowerCase(), cmd.toString().trim());

                NBTTagCompound display = is.getTagCompound().getCompoundTag("display");
                NBTTagList list = display.getTagList("Lore");
                if (list.tagCount() != 0)
                {
                    System.out.println("NOT 0");
                    for (int j = 0; j < list.tagCount(); ++j)
                    {
                        if (((NBTTagString) list.tagAt(j)).data.startsWith(color + args[0].toLowerCase()))
                        {
                            System.out.println("Macht found");
                            list.removeTag(j);
                        }
                    }
                }
                list.appendTag(new NBTTagString("list", color + args[0].toLowerCase() + "> " + cmd));
                display.setTag("Lore", list);

                is.getTagCompound().setCompoundTag("display", display);
                is.getTagCompound().setCompoundTag("FEbinding", nbt);
            }
            OutputHandler.chatConfirmation(sender, "Command bound to object.");
        }
    }

    @Override
    public boolean canConsoleUseCommand()
    {
        return false;
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, "left", "right", "clear");
        }
        return null;
    }

    @Override
    public String getCommandUsage(ICommandSender sender)
    {

        return "/bind <left|right|clear> <command[args]> Bind a command to an object.";
    }
}