package net.gabbalis.votifier;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;


public class Config {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pair<RewardConfig, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(RewardConfig::new);
    public static final RewardConfig CONFIG = pair.getLeft();
    public static final ForgeConfigSpec SPEC = pair.getRight();
    private static Reward[] rewards;
    private static boolean UnsafeDebug = false;
    private static int port;
    private static AtomicBoolean initialized = new AtomicBoolean(false);
    private static void initConfig(){

        if(initialized.equals(true)){
            LOGGER.error("initialized was: " + initialized.get());
            throw new IllegalStateException("Config initialization has been attempted a second time.");
        }
        bakeConfig();
        if (initialized.compareAndSet(false,true) != true){
            throw new IllegalStateException("Config initialization has been attempted a second time.");
        };
    }


    public static void onLoad(final ModConfigEvent.Loading configEvent) {
        LOGGER.info("Config load Event.");
        initConfig();
    }

    public static void onFileChange(final ModConfigEvent.Reloading configEvent) {
        LOGGER.info("Config Reload Event.");
        bakeConfig();
    }

    private static void bakeConfig(){
        // Set up the config fields. Guaranteed to have been called at least once before accessors can be called.
        port = 8192;

        String RewardCommand = CONFIG.RewardCommand.get();
        LOGGER.info("reward command in config is: " + RewardCommand);
        String[] rewardStrings = {RewardCommand};
        Reward[] demoRewards = new Reward[rewardStrings.length];

        for (int i = 0; i< rewardStrings.length; i++){
            demoRewards[i] = new CommandReward(rewardStrings[i]);
        }
        rewards = demoRewards;

        UnsafeDebug = CONFIG.UnsafeDebug.get();
    }
    public static int getPort(){
        if(initialized.equals(false)){
            throw new IllegalStateException("Config state queried before initialization.");
        }
        return port;
    };

    public static Reward[] getRewards(){
        if(initialized.equals(false)){
            throw new IllegalStateException("Config state queried before initialization.");
        }
        return rewards;
    }
    public static Boolean logKeysInsecurelyOnFailure(){
        if(initialized.equals(false)){
            throw new IllegalStateException("Config state queried before initialization.");
        }
        if (UnsafeDebug==true){
            LOGGER.warn("UnsafeDebug is set to true. If Decryption fails, encryption keys may be logged. For security set this option to false except for debugging purposes.");
            return true;
        }
        return false;
    }

    public static class RewardConfig {

        private final ForgeConfigSpec.ConfigValue<String> RewardCommand;
        private final ForgeConfigSpec.BooleanValue UnsafeDebug;

        RewardConfig(ForgeConfigSpec.Builder builder) {

            RewardCommand = builder
                    .comment("The command to run to reward players for voting. Use <player> to use the player name in the command.")
                    .translation("Reward Command")
                    .define("RewardCommand", "/say <player> has voted!");
            UnsafeDebug = builder
                    .comment("Whether to print the keys being used and the encrypted packets to console when decryption fails.")
                    .translation("Unsafe Debug Output")
                    .define("UnsafeDebug", false);
        }
    }
}
