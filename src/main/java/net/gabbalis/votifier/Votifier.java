package net.gabbalis.votifier;

import net.minecraftforge.fml.ModLoadingContext;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.event.server.FMLServerStoppingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


@Mod(Votifier.MOD_ID)
public class Votifier
{
    public static final String MOD_ID = "votifier";
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();

    private VoteListener listener = null;
    public Votifier() {
        // Register the config file
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Config::onLoad);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Config::onFileChange);
        ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, Config.SPEC);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new EventListener());
    }

    @SubscribeEvent
    public void onServerStarting(FMLServerStartingEvent event) {
        // do something when the server starts
        LOGGER.info("HELLO from server starting");
        listener = new VoteListener();
        listener.start();
    }
    @SubscribeEvent
    public void onServerShutdown(FMLServerStoppingEvent event) {
        if (listener != null) {
            listener.shutdown();
            try {
                listener.join(5000);
            }
            catch(InterruptedException e){
                LOGGER.error("Main thread failed to join with listener thread during shutdown.");
            }
        }
    }
}
