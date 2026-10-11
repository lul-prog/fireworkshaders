package com.fireworkshaders.network;

import com.fireworkshaders.menu.PyrotechnicWorkstationMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Small, bounded C2S message for the workstation's name field. */
public final class SetRocketNamePacket {
    private final int containerId;
    private final String name;

    public SetRocketNamePacket(int containerId, String name) {
        this.containerId = containerId;
        this.name = name == null ? "" : name;
    }

    public static void encode(SetRocketNamePacket packet, FriendlyByteBuf buffer) {
        buffer.writeVarInt(packet.containerId);
        buffer.writeUtf(packet.name, 64);
    }

    public static SetRocketNamePacket decode(FriendlyByteBuf buffer) {
        return new SetRocketNamePacket(buffer.readVarInt(), buffer.readUtf(64));
    }

    public static void handle(SetRocketNamePacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null && player.containerMenu.containerId == packet.containerId
                    && player.containerMenu instanceof PyrotechnicWorkstationMenu menu
                    && menu.stillValid(player)) {
                menu.setRocketName(packet.name);
            }
        });
        context.setPacketHandled(true);
    }
}
