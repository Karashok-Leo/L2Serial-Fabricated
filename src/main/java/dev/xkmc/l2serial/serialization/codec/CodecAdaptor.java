package dev.xkmc.l2serial.serialization.codec;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.xkmc.l2serial.util.Wrappers;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.PacketByteBuf;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.function.UnaryOperator;

public record CodecAdaptor<T>(Class<T> cls, UnaryOperator<T> validator) implements Codec<T>
{
    public CodecAdaptor(Class<T> cls)
    {
        this(cls, e -> e);
    }

    @Override
    public <E> DataResult<Pair<T, E>> decode(DynamicOps<E> ops, E input)
    {
        if (ops instanceof JsonOps && input instanceof JsonElement elem)
        {
            T val = JsonCodec.from(elem, cls, null);
            return DataResult.success(Pair.of(validator.apply(val), input));
        }
        if (ops instanceof NbtOps && input instanceof NbtElement tag)
        {
            T val = TagCodec.valueFromTag(tag, cls);
            return DataResult.success(Pair.of(validator.apply(val), input));
        }
        return DataResult.error(() -> "Unknown ops type " + ops.getClass().getSimpleName() + " and value " + input.getClass().getSimpleName());
    }

    @Override
    public <E> DataResult<E> encode(T input, DynamicOps<E> ops, E prefix)
    {
        if (ops instanceof JsonOps)
        {
            var json = JsonCodec.toJson(input, cls);
            assert json != null;
            if (!ops.empty().equals(prefix))
            {
                if (json instanceof JsonObject a && prefix instanceof JsonObject b)
                {
                    for (var e : b.entrySet())
                    {
                        a.add(e.getKey(), e.getValue());
                    }
                } else
                {
                    return DataResult.error(() -> "Non-empty prefix for type " + ops.getClass().getSimpleName());
                }
            }
            return DataResult.success(Wrappers.cast(json));
        }
        if (ops instanceof NbtOps)
        {
            var tag = TagCodec.valueToTag(cls, input, e -> true);
            assert tag != null;
            if (!ops.empty().equals(prefix))
            {
                if (tag instanceof NbtCompound a && prefix instanceof NbtCompound b)
                {
                    for (var e : b.getKeys())
                    {
                        a.put(e, Objects.requireNonNull(b.get(e)));
                    }
                } else
                {
                    return DataResult.error(() -> "Non-empty prefix for type " + ops.getClass().getSimpleName());
                }
            }
            return DataResult.success(Wrappers.cast(tag));
        }
        return DataResult.error(() -> "Unknown ops type " + ops.getClass().getSimpleName());
    }

    public Codec<T> toNetwork()
    {
        return Codec.BYTE_BUFFER.xmap(
                e -> PacketCodec.from(new PacketByteBuf(Unpooled.wrappedBuffer(e)), cls, null),
                e -> ByteBuffer.wrap(PacketCodec.toBytes(e, cls, x -> true)));
    }
}