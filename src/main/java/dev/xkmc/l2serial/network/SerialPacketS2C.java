package dev.xkmc.l2serial.network;

import net.minecraft.client.network.ClientPlayerEntity;

public interface SerialPacketS2C extends SerialPacketBase
{
    void handle(ClientPlayerEntity player);
}
