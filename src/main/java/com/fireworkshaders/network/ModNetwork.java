package com.fireworkshaders.network;

import com.fireworkshaders.FireworkShaders;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public final class ModNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(FireworkShaders.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    private static int nextId;
    private static boolean registered;

    private ModNetwork() {
    }

    public static void register() {
        if (registered) return;
        registered = true;
        CHANNEL.registerMessage(nextId++, SetRocketNamePacket.class,
                SetRocketNamePacket::encode,
                SetRocketNamePacket::decode,
                SetRocketNamePacket::handle,
                Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }
}
