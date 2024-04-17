package dev.xkmc.l2serial.network;

import dev.xkmc.l2serial.serialization.codec.PacketCodec;
import net.minecraft.network.PacketByteBuf;

import java.util.Objects;

public interface SerialPacketBase extends SimplePacketBase
{
    static <T extends SimplePacketBase> T serial(Class<T> cls, PacketByteBuf buf)
    {
        return Objects.requireNonNull(PacketCodec.from(buf, cls, null));
    }

    @Override
    default void write(PacketByteBuf buffer)
    {
        PacketCodec.to(buffer, this);
    }
}