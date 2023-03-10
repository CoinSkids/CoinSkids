package me.throwing.coinskids.events;

import me.throwing.coinskids.Config;
import me.throwing.coinskids.Main;
import me.throwing.coinskids.Reference;
import me.throwing.coinskids.utils.Utils;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Locale;

public class OnChatReceived {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void chat(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (message.startsWith("Your new API key is ")) {
            String key = message.split("key is ")[1];
            Config.apiKey = key;
            Utils.sendMessageWithPrefix("§aAPI Key set to " + key);
        }
        for (String filter : Main.chatFilters) {
            if (message.toLowerCase(Locale.ROOT).contains(filter) && message.contains(": ")) { // containing a colon means it is a user message
                event.setCanceled(true);
                Reference.logger.info("The following message was ignored due to a chat filter: " + message);
                return;
            }
        }
    }
}
