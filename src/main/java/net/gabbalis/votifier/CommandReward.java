package net.gabbalis.votifier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandReward extends Reward{
    private static final Logger LOGGER = LogManager.getLogger();
    String command;
    public CommandReward(String command){
        this.command = command;
        if (this.command.equals(null)){
            LOGGER.error("Command string is Null. Initializing error command.");
            this.command = "/say Error- Attempted to Execute Undefined Reward Command.";
        };


    }
    @Override
    void execute(ServerPlayerEntity player) {
        MinecraftServer server = LogicalSidedProvider.INSTANCE.get(LogicalSide.SERVER);
        String[] parts = command.split("<player>");
        String finalCommand = null;
        if(parts.length==1){
            finalCommand = parts[0];
        }
        else{
            String playerName = player.getDisplayName().getString();
            StringBuilder b = new StringBuilder();
            for (int i=0; i<parts.length; i++){
                b.append(parts[i]);
                if (i < parts.length-1){
                    b.append(playerName);
                }
            }
            finalCommand = b.toString();
        }
        LOGGER.info("Cdaommand Run: " + finalCommand);
        server.getCommandManager().handleCommand(server.getCommandSource(), finalCommand);
    }
}
