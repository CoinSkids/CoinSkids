package me.throwing.coinskids.commands;

import gg.essential.api.EssentialAPI;
import me.throwing.coinskids.Main;
import me.throwing.coinskids.Reference;
import me.throwing.coinskids.commands.subcommands.Subcommand;
import me.throwing.coinskids.utils.Utils;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public class CoinSkidsCommand extends CommandBase {
    private final Subcommand[] subcommands;

    public CoinSkidsCommand(Subcommand[] subcommands) {
        this.subcommands = subcommands;
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    @Override
    public List<String> getCommandAliases() {
        return Arrays.asList("coinskids", "coinskids");
    }

    @Override
    public String getCommandName() {
        return "coinskids";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/coinskids <subcommand> <arguments>";
    }

    public void sendHelp(ICommandSender sender) {
        List<String> commandUsages = new LinkedList<>();
        for (Subcommand subcommand : this.subcommands) {
            if (!subcommand.isHidden()) {
                commandUsages.add(EnumChatFormatting.AQUA + "/coinskids " + subcommand.getCommandName() + " "
                    + subcommand.getCommandUsage() + EnumChatFormatting.DARK_AQUA + " - " + subcommand.getCommandDescription());
            }
        }
        sender.addChatMessage(new ChatComponentText(EnumChatFormatting.GOLD + "CoinSkids " + EnumChatFormatting.GREEN
            + Reference.VERSION + "\n" + String.join("\n", commandUsages)));
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            EssentialAPI.getGuiUtil().openScreen(Main.config.gui());
            return;
        }
        for (Subcommand subcommand : this.subcommands) {
            if (Objects.equals(args[0], subcommand.getCommandName())) {
                if (!subcommand.processCommand(sender, Arrays.copyOfRange(args, 1, args.length))) {
                    // processCommand returned false
                    Utils.sendMessageWithPrefix("&cFailed to execute command, command usage: /coinskids "
                        + subcommand.getCommandName() + " " + subcommand.getCommandUsage());
                }
                return;
            }
        }
        Utils.sendMessageWithPrefix(EnumChatFormatting.RED
            + "The subcommand wasn't found, please refer to the help message below for the list of subcommands");
        sendHelp(sender);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        List<String> possibilities = new LinkedList<>();
        for (Subcommand subcommand : subcommands) {
            possibilities.add(subcommand.getCommandName());
        }
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, possibilities);
        }
        return null;
    }
}
