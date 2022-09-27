package net.gabbalis.votifier;

import net.minecraft.entity.player.ServerPlayerEntity;

public abstract class Reward {
    abstract void execute(ServerPlayerEntity player);
}
