package me.throwing.coinskids;

import me.throwing.coinskids.commands.CoinSkidsCommand;
import me.throwing.coinskids.commands.subcommands.Help;
import me.throwing.coinskids.commands.subcommands.Subcommand;
import me.throwing.coinskids.commands.subcommands.Toggle;
import me.throwing.coinskids.commands.subcommands.Token;
import me.throwing.coinskids.events.OnChatReceived;
import me.throwing.coinskids.events.OnGuiOpen;
import me.throwing.coinskids.events.OnTick;
import me.throwing.coinskids.events.OnTooltip;
import me.throwing.coinskids.events.OnWorldJoin;
import me.throwing.coinskids.objects.AverageItem;
import me.throwing.coinskids.utils.ApiHandler;
import me.throwing.coinskids.utils.Utils;
import me.throwing.coinskids.utils.updater.GitHub;
import me.throwing.coinskids.websocket.Client;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ProgressManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION)
public class Main {
    public static Config config = new Config();
    public static Authenticator authenticator;
    public static boolean checkedForUpdate = false;
    public static CoinSkidsCommand commandManager = new CoinSkidsCommand(new Subcommand[]{
        new Toggle(),
        new Help(),
        new Token()
    });
    public static Map<String, AverageItem> averageItemMap = new HashMap<>();
    public static Map<String, Date> processedItem = new HashMap<>(); // Date is the expiry time, indicates when the auction ends and should be purged to save memory for the long run
    public static Map<String, Integer> lbinItem = new HashMap<>();
    public static Map<String, Integer> bazaarItem = new HashMap<>(); // Long is the item's instant sell price
    public static Map<String, Integer> npcItem = new HashMap<>();
    public static List<String> chatFilters = new LinkedList<>();
    public static double balance = 0;
    public static boolean justPlayedASound = false; // This is to prevent multiple flips coming in at once and dinging the heck out of the user
    public static File jarFile;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        jarFile = event.getSourceFile();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	GitHub.autoUpdate();
        ProgressManager.ProgressBar progressBar = ProgressManager.push("Coin Skids", 4);
        authenticator = new Authenticator(progressBar);
        try {
            authenticator.authenticate(true);
        } catch (Exception e) {
            while (progressBar.getStep() < (progressBar.getSteps() - 1))
                progressBar.step("loading-failed-" + progressBar.getStep());
            e.printStackTrace();
            Reference.logger.error("CoinSkids have been disabled due to an error while authenticating. Please check the logs for more information.");
            return;
        }
        progressBar.step("Registering events, commands, hooks & tasks");
        config.preload();
        ClientCommandHandler.instance.registerCommand(commandManager);
        GitHub.downloadDeleteTask();
        MinecraftForge.EVENT_BUS.register(new OnWorldJoin());
        MinecraftForge.EVENT_BUS.register(new OnTick());
        MinecraftForge.EVENT_BUS.register(new OnTooltip());
        MinecraftForge.EVENT_BUS.register(new OnChatReceived());
        MinecraftForge.EVENT_BUS.register(new OnGuiOpen());
        Tasks.updateBalance.start();
        Tasks.updateBazaarItem.start();
        Tasks.updateFilters.start();
        Utils.runInAThread(ApiHandler::updateNPC);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            Reference.logger.info("Logging out...");
            try {
                authenticator.logout();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        progressBar.step("Establishing WebSocket Connection");
        Client.connectWithToken();
        ProgressManager.pop(progressBar);
    }
}
