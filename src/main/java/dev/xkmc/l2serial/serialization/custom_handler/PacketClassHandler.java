package dev.xkmc.l2serial.serialization.custom_handler;

import net.minecraft.network.PacketByteBuf;

public interface PacketClassHandler<T>
{
    T fromPacket(PacketByteBuf buf);

    void toPacket(PacketByteBuf buf, Object obj);
}