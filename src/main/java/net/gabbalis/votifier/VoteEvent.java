package net.gabbalis.votifier;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class VoteEvent extends Event {
    String siteName;
    String address;
    ServerPlayer player;
    String timeStamp;
    public VoteEvent(String siteName, String address, ServerPlayer player, String timeStamp){
        this.siteName = siteName;
        this.address = address;
        this.player = player;
        this.timeStamp = timeStamp;
    }

    public String getSiteName() {
        return siteName;
    }
    public String getAddress() {
        return address;
    }
    public ServerPlayer getPlayer() {
        return player;
    }
    public String getTimeStamp() {
        return timeStamp;
    }
}
