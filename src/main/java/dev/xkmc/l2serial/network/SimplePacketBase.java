package dev.xkmc.l2serial.network;

import net.minecraft.network.PacketByteBuf;

public interface SimplePacketBase
{
    void write(PacketByteBuf buf);
}