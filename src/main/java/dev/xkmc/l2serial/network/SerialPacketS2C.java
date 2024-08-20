package dev.xkmc.l2serial.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.network.ClientPlayerEntity;

public interface SerialPacketS2C extends SerialPacketBase
{
    @Environment(EnvType.CLIENT)
    void handle(ClientPlayerEntity player);
}
