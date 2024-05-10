package dev.xkmc.l2serial.network;

import net.minecraft.server.network.ServerPlayerEntity;

public interface SerialPacketC2S extends SerialPacketBase
{
    void handle(ServerPlayerEntity player);
}
