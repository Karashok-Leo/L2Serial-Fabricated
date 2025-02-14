package dev.xkmc.l2serial.network;

import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.Nullable;

public interface SimplePacketBase
{
    @Nullable
    default <T extends SimplePacketBase> T read(PacketByteBuf buf)
    {
        return null;
    }

    void write(PacketByteBuf buf);
}