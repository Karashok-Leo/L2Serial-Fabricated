package dev.xkmc.l2serial.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class PacketHandler
{
    private final Map<Class<? extends SimplePacketBase>, PacketType<?>> map = new LinkedHashMap<>();
    private final String namespace;

    public PacketHandler(String namespace)
    {
        this.namespace = namespace;
    }

    public <T extends SimplePacketBase> BasePayload<T> getPacket(T packet)
    {
        return new BasePayload<>(packet, map.get(packet.getClass()));
    }

    @SuppressWarnings("unchecked")
    public <T extends SimplePacketBase> PacketType<BasePayload<T>> getPacketType(Class<T> cls)
    {
        var type = map.get(cls);
        if (type == null)
            throw new IllegalStateException("PacketType for Class<" + cls.getName() + "> doesn't exist, call PacketHandler.configure(Class<? extends SimplePacketBase> type) to configure it.");
        return (PacketType<BasePayload<T>>) type;
    }

    public void configure(Class<? extends SimplePacketBase> type)
    {
        map.put(type, PacketType.create(of(type), buf -> getPacket(SerialPacketBase.serial(type, buf))));
    }

    @SafeVarargs
    public final void configure(Class<? extends SimplePacketBase>... types)
    {
        for (Class<? extends SimplePacketBase> type : types)
            configure(type);
    }

    @Environment(EnvType.CLIENT)
    public void configureS2C(Class<? extends SerialPacketS2C> type)
    {
        ClientPlayNetworking.registerGlobalReceiver(this.getPacketType(type), (packet, player, responseSender) -> packet.packet().handle(player));
    }

    @Environment(EnvType.CLIENT)
    @SafeVarargs
    public final void configureS2C(Class<? extends SerialPacketS2C>... types)
    {
        for (Class<? extends SerialPacketS2C> type : types)
            configureS2C(type);
    }

    public void configureC2S(Class<? extends SerialPacketC2S> type)
    {
        ServerPlayNetworking.registerGlobalReceiver(this.getPacketType(type), (packet, player, responseSender) -> packet.packet().handle(player));
    }

    @SafeVarargs
    public final void configureC2S(Class<? extends SerialPacketC2S>... types)
    {
        for (Class<? extends SerialPacketC2S> type : types)
            configureC2S(type);
    }

    private Identifier of(Class<? extends SimplePacketBase> cls)
    {
        String name = cls.getSimpleName();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++)
        {
            char ch = name.charAt(i);
            if (ch >= 'a' && ch <= 'z' || ch >= '0' && ch <= '9')
                builder.append(ch);
            else if (ch >= 'A' && ch <= 'Z')
                builder.append((char) (ch - 'A' + 'a'));
        }
        return new Identifier(namespace, builder.toString());
    }
}
