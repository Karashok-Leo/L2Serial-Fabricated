package dev.xkmc.l2serial.network;

import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record BasePayload<T extends SimplePacketBase>(
        T packet,
        PacketType<?> type
) implements FabricPacket
{
    @Override
    public void write(PacketByteBuf buf)
    {
        packet.write(buf);
    }

    @Override
    public PacketType<?> getType()
    {
        return type;
    }
}
