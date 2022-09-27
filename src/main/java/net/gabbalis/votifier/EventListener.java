package net.gabbalis.votifier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.*;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.*;
import net.minecraftforge.api.distmarker.Dist;

@Mod.EventBusSubscriber(modid = Votifier.MOD_ID, bus = Bus.FORGE, value = Dist.DEDICATED_SERVER)
public class EventListener {


    @SubscribeEvent
    public static void onVoteEvent(final VoteEvent event){
        ServerPlayerEntity player = event.getPlayer();
        Reward[] rewards = Config.getRewards();
        for (int i = 0; i< rewards.length; i++){
            rewards[i].execute(player);
        }
    }
}
