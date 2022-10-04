package net.gabbalis.votifier;

import net.minecraft.server.level.ServerPlayer;

public abstract class Reward {
    abstract void execute(ServerPlayer player);
}
