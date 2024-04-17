package dev.xkmc.l2serial.network;

import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.util.Identifier;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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
        return Objects.requireNonNull((PacketType<BasePayload<T>>) map.get(cls));
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
