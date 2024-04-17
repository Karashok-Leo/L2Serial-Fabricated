package dev.xkmc.l2serial.serialization.custom_handler;

import com.google.gson.JsonElement;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Function;

@SuppressWarnings({"unsafe", "unchecked"})
public class ClassHandler<R extends NbtElement, T> implements JsonClassHandler<T>, NBTClassHandler<R, T>, PacketClassHandler<T>
{
    public final Function<Object, JsonElement> toJson;
    public final Function<JsonElement, ?> fromJson;
    public final Function<PacketByteBuf, ?> fromPacket;
    public final BiConsumer<PacketByteBuf, Object> toPacket;
    public final Function<NbtElement, ?> fromTag;
    public final Function<Object, NbtElement> toTag;

    @SuppressWarnings("unchecked")
    public ClassHandler(
            @NotNull Class<T> cls,
            Function<T, JsonElement> tj,
            Function<JsonElement, T> fj,
            Function<PacketByteBuf, T> fp,
            BiConsumer<PacketByteBuf, T> tp,
            Function<R, T> ft,
            Function<T, R> tt,
            @NotNull Class<?>... others
    )
    {
        this.toJson = (Function<Object, JsonElement>) tj;
        this.fromJson = fj;
        this.fromPacket = fp;
        this.toPacket = (BiConsumer<PacketByteBuf, Object>) tp;
        fromTag = (Function<NbtElement, ?>) ft;
        toTag = (Function<Object, NbtElement>) tt;
        put(cls);
        for (Class<?> c : others)
            put(c);
    }

    private void put(Class<?> cls)
    {
        if (toJson != null && fromJson != null) Handlers.JSON_MAP.putIfAbsent(cls, this);
        if (fromTag != null && toTag != null) Handlers.NBT_MAP.putIfAbsent(cls, this);
        if (fromPacket != null && toPacket != null) Handlers.PACKET_MAP.putIfAbsent(cls, this);
    }

    @Override
    public T fromTag(NbtElement tag)
    {
        return (T) fromTag.apply(tag);
    }

    @Override
    public R toTag(Object obj)
    {
        return (R) toTag.apply(obj);
    }

    @Override
    public JsonElement toJson(Object obj)
    {
        return toJson.apply(obj);
    }

    @Override
    public T fromJson(JsonElement e)
    {
        return (T) fromJson.apply(e);
    }

    @Override
    public void toPacket(PacketByteBuf buf, Object obj)
    {
        toPacket.accept(buf, obj);
    }

    @Override
    public T fromPacket(PacketByteBuf buf)
    {
        return (T) fromPacket.apply(buf);
    }
}
