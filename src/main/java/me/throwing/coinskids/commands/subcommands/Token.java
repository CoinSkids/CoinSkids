package me.throwing.coinskids.commands.subcommands;

import me.throwing.coinskids.Config;
import me.throwing.coinskids.Main;
import me.throwing.coinskids.utils.Utils;
import net.minecraft.command.ICommandSender;
import net.minecraft.event.ClickEvent;

public class Token implements Subcommand {
    @Override
    public String getCommandName() {
        return "token";
    }

    @Override
    public boolean isHidden() {
        return true;
    }

    @Override
    public String getCommandUsage() {
        return "";
    }

    @Override
    public String getCommandDescription() {
        return "Shows the current session token for API debugging purposes, do NOT share this with anyone!";
    }

    @Override
    public boolean processCommand(ICommandSender sender, String[] args) {
        if (Config.debug) {
            Utils.sendMessageWithPrefix("&7Click to copy the token", new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, Main.authenticator.getToken()));
        }
        return true;
    }
}
